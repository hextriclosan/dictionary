package in.solomk.dictionary.api.word.handler;

import in.solomk.dictionary.api.word.dto.UserWordsResponse;
import in.solomk.dictionary.api.word.mapper.UserWordsWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.words.UsersWordsService;
import lombok.AllArgsConstructor;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
@AllArgsConstructor
public class GetWordHandler implements HandlerFunction<ServerResponse> {

    private final UsersWordsService usersWordsService;
    private final UserWordsWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = UserWordsResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> usersWordsService.getWord(userId, extractLanguageCode(request), request.pathVariable("wordId")))
                      .map(mapper::toWordResponse)
                      .flatMap(wordResponse -> ServerResponse.ok()
                                                             .contentType(MediaType.APPLICATION_JSON)
                                                             .bodyValue(wordResponse))
                      .switchIfEmpty(ServerResponse.notFound()
                                                   .build());
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }
}
