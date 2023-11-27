package in.solomk.dictionary.api.learning_item.handler;

import in.solomk.dictionary.api.learning_item.dto.CreateLearningItemRequest;
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
public class AddLearningItemHandler implements HandlerFunction<ServerResponse> {

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
                                                       .body(addAndValidateStudiedLanguage(request, userId),
                                                             LearningItemResponse.class));
    }

    private Mono<LearningItemResponse> addAndValidateStudiedLanguage(ServerRequest request, String userId) {
        var supportedLanguage = extractLanguageCode(request);
        return userLanguagesService.validateLanguageIsStudied(userId, supportedLanguage)
                                   .then(addLearningItem(request, userId));
    }

    private Mono<LearningItemResponse> addLearningItem(ServerRequest request, String userId) {
        return extractRequestBody(request)
                .flatMap(createLearningItemRequest -> learningItemsService.saveLearningItem(
                        userId,
                        extractLanguageCode(request),
                        mapper.toUnsavedLearningItem(createLearningItemRequest)))
                .map(mapper::toLearningItemResponse);
    }

    private Mono<CreateLearningItemRequest> extractRequestBody(ServerRequest request) {
        return request.bodyToMono(CreateLearningItemRequest.class);
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }

}
