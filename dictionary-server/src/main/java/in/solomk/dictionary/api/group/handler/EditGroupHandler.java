package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.api.group.dto.EditWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.WordsGroupResponse;
import in.solomk.dictionary.api.group.mapper.WordsGroupWebApiMapper;
import in.solomk.dictionary.service.group.WordsGroupService;
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

    private final WordsGroupService wordsGroupService;
    private final UserLanguagesService userLanguagesService;
    private final WordsGroupWebApiMapper mapper;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> ServerResponse.ok()
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .body(editAndValidateStudiedLanguage(request, userId), WordsGroupResponse.class));
    }

    private Mono<WordsGroupResponse> editAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(editGroup(request, userId));
    }

    private Mono<WordsGroupResponse> editGroup(ServerRequest request, String userId) {
        return extractRequestBody(request)
                .flatMap(editWordsGroupRequest -> wordsGroupService
                        .editWordsGroup(userId,
                                        extractLanguageCode(request),
                                        mapper.toWordsGroup(extractGroupId(request), editWordsGroupRequest)))
                .map(mapper::toWordsGroupResponse);
    }

    private Mono<EditWordsGroupRequest> extractRequestBody(ServerRequest request) {
        return request.bodyToMono(EditWordsGroupRequest.class);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        return SupportedLanguage.getByLanguageCode(request.pathVariable("languageCode"))
                                .orElseThrow(() -> new IllegalArgumentException("Language code is not supported"));
    }

    private String extractGroupId(ServerRequest request) {
        return request.pathVariable("groupId");
    }
}
