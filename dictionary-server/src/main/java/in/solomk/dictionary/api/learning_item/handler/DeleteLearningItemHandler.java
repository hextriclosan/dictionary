package in.solomk.dictionary.api.learning_item.handler;

import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.api.learning_item.mapper.LearningItemsWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.learning_items.LearningItemsService;
import in.solomk.dictionary.service.learning_items.model.LearningItemsList;
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
public class DeleteLearningItemHandler implements HandlerFunction<ServerResponse> {

    private final LearningItemsService learningItemsService;
    private final LearningItemsWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = LearningItemResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                .map(Principal::getName)
                .flatMap(userId -> deleteLearningItem(request, userId))
                .map(mapper::toLearningItemListResponse)
                .flatMap(learningItemListResponse -> ServerResponse.ok()
                                                            .contentType(APPLICATION_JSON)
                                                            .bodyValue(learningItemListResponse));
    }

    private Mono<LearningItemsList> deleteLearningItem(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        var learningItemId = request.pathVariable("learningItemId");
        return learningItemsService.deleteLearningItem(userId, supportedLanguage, learningItemId);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }

}
