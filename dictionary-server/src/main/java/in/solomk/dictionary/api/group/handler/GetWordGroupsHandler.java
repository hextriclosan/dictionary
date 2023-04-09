package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.api.group.mapper.WordsGroupWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.WordsGroupService;
import in.solomk.dictionary.service.language.SupportedLanguage;
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
public class GetWordGroupsHandler implements HandlerFunction<ServerResponse> {

    private final WordsGroupService wordsGroupService;
    private final WordsGroupWebApiMapper mapper;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMapMany(userId -> wordsGroupService.getAllUserGroups(userId, extractLanguageCode(request)))
                      .collectList()
                      .map(mapper::toAllWordGroupsResponse)
                      .flatMap(allWordGroupsResponse -> ServerResponse.ok()
                                                                      .contentType(MediaType.APPLICATION_JSON)
                                                                      .bodyValue(allWordGroupsResponse))
                      .switchIfEmpty(ServerResponse.notFound()
                                                   .build());
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }
}
