package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.api.group.dto.CreateWordsGroupRequest;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.WordsGroupService;
import in.solomk.dictionary.service.group.model.UnsavedWordsGroup;
import in.solomk.dictionary.service.group.model.WordsGroup;
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

    private final WordsGroupService wordsGroupService;
    private final UserLanguagesService userLanguagesService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> createAndValidateStudiedLanguage(request, userId))
                      .flatMap(langWithWordsGroup ->
                                       ServerResponse.created(buildCreatedUri(langWithWordsGroup.getT1(),
                                                                              langWithWordsGroup.getT2()))
                                                     .contentType(APPLICATION_JSON)
                                                     .bodyValue(langWithWordsGroup.getT2())
                      );
    }

    private Mono<Tuple2<SupportedLanguage, WordsGroup>> createAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(Mono.zip(Mono.just(supportedLanguage), createGroup(request, userId)));
    }

    private Mono<WordsGroup> createGroup(ServerRequest request, String userId) {
        return extractRequestBody(request)
                .flatMap(createWordsGroupRequest -> wordsGroupService.saveWordsGroup(
                        userId, extractLanguageCode(request), new UnsavedWordsGroup(createWordsGroupRequest.name())));
    }

    private Mono<CreateWordsGroupRequest> extractRequestBody(ServerRequest request) {
        return request.bodyToMono(CreateWordsGroupRequest.class);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }

    private static URI buildCreatedUri(SupportedLanguage language, WordsGroup wordsGroup) {
        return URI.create("/api/languages/%s/groups/%s".formatted(language.getLanguageCode(), wordsGroup.id()));
    }

}
