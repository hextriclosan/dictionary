package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.api.group.dto.EditGroupRequest;
import in.solomk.dictionary.api.group.dto.GroupResponse;
import in.solomk.dictionary.api.group.mapper.GroupWebApiMapper;
import in.solomk.dictionary.service.group.GroupsService;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.language.UserLanguagesService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
@AllArgsConstructor
public class EditGroupHandler implements HandlerFunction<ServerResponse> {

    private final GroupsService groupsService;
    private final UserLanguagesService userLanguagesService;
    private final GroupWebApiMapper mapper;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> ServerResponse.ok()
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .body(editAndValidateStudiedLanguage(request, userId), GroupResponse.class));
    }

    private Mono<GroupResponse> editAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(editGroup(request, userId));
    }

    private Mono<GroupResponse> editGroup(ServerRequest request, String userId) {
        return extractRequestBody(request)
                .flatMap(editGroupRequest -> groupsService
                        .editGroup(userId,
                                   extractLanguageCode(request),
                                   mapper.toGroup(extractGroupId(request), editGroupRequest)))
                .map(mapper::toGroupResponse);
    }

    private Mono<EditGroupRequest> extractRequestBody(ServerRequest request) {
        return request.bodyToMono(EditGroupRequest.class);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        return SupportedLanguage.getByLanguageCode(request.pathVariable("languageCode"))
                                .orElseThrow(() -> new IllegalArgumentException("Language code is not supported"));
    }

    private String extractGroupId(ServerRequest request) {
        return request.pathVariable("groupId");
    }
}
