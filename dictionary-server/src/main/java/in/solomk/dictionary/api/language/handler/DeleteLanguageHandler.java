package in.solomk.dictionary.api.language.handler;

import in.solomk.dictionary.api.language.dto.LearningLanguagesAggregatedResponse;
import in.solomk.dictionary.api.language.mapper.LearningLanguagesWebApiMapper;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.group.GroupsService;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.language.UserLanguagesService;
import in.solomk.dictionary.service.learning_items.LearningItemsService;
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
public class DeleteLanguageHandler implements HandlerFunction<ServerResponse> {

    private final UserLanguagesService userLanguagesService;
    private final LearningItemsService learningItemsService;
    private final GroupsService groupsService;
    private final LearningLanguagesWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = LearningItemResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> ServerResponse.ok()
                                                       .contentType(APPLICATION_JSON)
                                                       .body(deleteLanguageAndLearningItemsAndGroups(request, userId),
                                                             LearningLanguagesAggregatedResponse.class));
    }

    // Should be transactional
    private Mono<LearningLanguagesAggregatedResponse> deleteLanguageAndLearningItemsAndGroups(ServerRequest request, String userId) {
        var supportedLanguage = getSafeLanguage(request.pathVariable("languageCode"));
        return learningItemsService.deleteAllUserLearningItems(userId, supportedLanguage)
                                   .then(groupsService.deleteAllUserGroups(userId, supportedLanguage.getLanguageCode()))
                                   .then(userLanguagesService.deleteLearningLanguage(userId, supportedLanguage))
                                   .map(mapper::toLearningLanguagesAggregatedResponse);
    }


    private SupportedLanguage getSafeLanguage(String languageCode) {
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported %s", languageCode));
    }

}
