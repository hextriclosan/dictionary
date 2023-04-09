package in.solomk.dictionary.api.word.handler;

import in.solomk.dictionary.api.word.dto.EditWordRequest;
import in.solomk.dictionary.api.word.dto.WordResponse;
import in.solomk.dictionary.api.word.mapper.UserWordsWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.language.UserLanguagesService;
import in.solomk.dictionary.service.words.UsersWordsService;
import lombok.AllArgsConstructor;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@AllArgsConstructor
@Component
public class EditWordHandler implements HandlerFunction<ServerResponse> {

    private final UsersWordsService usersWordsService;
    private final UserLanguagesService userLanguagesService;
    private final UserWordsWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = WordResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> ServerResponse.ok()
                                                       .contentType(APPLICATION_JSON)
                                                       .body(editAndValidateStudiedLanguage(request, userId),
                                                             WordResponse.class));
    }

    private Mono<WordResponse> editAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(editWord(request, userId));
    }

    private Mono<WordResponse> editWord(ServerRequest request, String userId) {
        return extractRequestBody(request)
                .flatMap(editWordRequest -> usersWordsService
                        .editWord(userId,
                                  extractLanguageCode(request),
                                  mapper.toWord(extractWordId(request), editWordRequest)))
                .map(mapper::toWordResponse);
    }

    private Mono<EditWordRequest> extractRequestBody(ServerRequest request) {
        return request.bodyToMono(EditWordRequest.class);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }

    private String extractWordId(ServerRequest request) {
        return request.pathVariable("wordId");
    }

}
