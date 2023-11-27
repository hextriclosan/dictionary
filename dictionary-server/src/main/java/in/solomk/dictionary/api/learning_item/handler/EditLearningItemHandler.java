package in.solomk.dictionary.api.learning_item.handler;

import in.solomk.dictionary.api.learning_item.dto.EditLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.api.learning_item.mapper.LearningItemsWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
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
public class EditLearningItemHandler implements HandlerFunction<ServerResponse> {

    private final LearningItemsService learningItemsService;
    private final UserLanguagesService userLanguagesService;
    private final LearningItemsWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = LearningItemResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> ServerResponse.ok()
                                                       .contentType(APPLICATION_JSON)
                                                       .body(editAndValidateStudiedLanguage(request, userId),
                                                             LearningItemResponse.class));
    }

    private Mono<LearningItemResponse> editAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(editLearningItem(request, userId));
    }

    private Mono<LearningItemResponse> editLearningItem(ServerRequest request, String userId) {
        return extractRequestBody(request)
                .flatMap(editWordRequest -> learningItemsService
                        .editLearningItem(userId,
                                          extractLanguageCode(request),
                                          mapper.toLearningItem(extractlearningItemId(request), editWordRequest)))
                .map(mapper::toLearningItemResponse);
    }

    private Mono<EditLearningItemRequest> extractRequestBody(ServerRequest request) {
        return request.bodyToMono(EditLearningItemRequest.class);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }

    private String extractlearningItemId(ServerRequest request) {
        return request.pathVariable("learningItemId");
    }

}
