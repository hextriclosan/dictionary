package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.api.group.dto.CreateGroupRequest;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.GroupsService;
import in.solomk.dictionary.service.group.model.Group;
import in.solomk.dictionary.service.group.model.UnsavedGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.language.UserLanguagesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.net.URI;
import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@AllArgsConstructor
public class CreateGroupHandler implements HandlerFunction<ServerResponse> {

    private final GroupsService groupsService;
    private final UserLanguagesService userLanguagesService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> createAndValidateStudiedLanguage(request, userId))
                      .flatMap(langWithGroup ->
                                       ServerResponse.created(buildCreatedUri(langWithGroup.getT1(),
                                                                              langWithGroup.getT2()))
                                                     .contentType(APPLICATION_JSON)
                                                     .bodyValue(langWithGroup.getT2())
                      );
    }

    private Mono<Tuple2<SupportedLanguage, Group>> createAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(Mono.zip(Mono.just(supportedLanguage), createGroup(request, userId)));
    }

    private Mono<Group> createGroup(ServerRequest request, String userId) {
        return extractRequestBody(request)
                .flatMap(createGroupRequest -> groupsService.saveGroup(
                        userId, extractLanguageCode(request), new UnsavedGroup(createGroupRequest.name())));
    }

    private Mono<CreateGroupRequest> extractRequestBody(ServerRequest request) {
        return request.bodyToMono(CreateGroupRequest.class);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }

    private static URI buildCreatedUri(SupportedLanguage language, Group group) {
        return URI.create("/api/languages/%s/groups/%s".formatted(language.getLanguageCode(), group.id()));
    }

}
