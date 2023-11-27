package in.solomk.dictionary.api.group.handler;

import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.GroupsService;
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
public class RemoveLearningItemFromGroupHandler implements HandlerFunction<ServerResponse> {
    private final GroupsService groupsService;
    private final UserLanguagesService userLanguagesService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> deleteLearningItemFromGroupAndValidateStudiedLanguage(request, userId))
                      .then(Mono.defer(() -> ServerResponse.ok().build()));
    }

    private Mono<Void> deleteLearningItemFromGroupAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(deleteLearningItemFromGroup(request, userId));
    }

    private Mono<Void> deleteLearningItemFromGroup(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        var groupId = request.pathVariable("groupId");
        var learningItemId = request.pathVariable("learningItemId");
        return groupsService.deleteLearningItemFromGroup(userId, supportedLanguage, groupId, learningItemId);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }
}
