package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.WordsGroupService;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.language.UserLanguagesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Slf4j
@Component
@AllArgsConstructor
public class AddWordToGroupHandler implements HandlerFunction<ServerResponse> {

    private final WordsGroupService wordsGroupService;
    private final UserLanguagesService userLanguagesService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> createAndValidateStudiedLanguage(request, userId))
                      .then(Mono.defer(() -> ServerResponse.ok().build()));
    }

    private Mono<Void> createAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(addWordToGroup(request, userId));
    }

    private Mono<Void> addWordToGroup(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        var groupId = request.pathVariable("groupId");
        var wordId = request.pathVariable("wordId");
        return wordsGroupService.addWordToGroup(userId, supportedLanguage, groupId, wordId);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }

}
