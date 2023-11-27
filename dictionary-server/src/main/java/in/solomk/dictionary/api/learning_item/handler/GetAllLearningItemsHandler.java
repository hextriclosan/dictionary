package in.solomk.dictionary.api.learning_item.handler;

import in.solomk.dictionary.api.learning_item.dto.LearningItemListResponse;
import in.solomk.dictionary.api.learning_item.mapper.LearningItemsWebApiMapper;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.learning_items.LearningItemsService;
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
public class GetAllLearningItemsHandler implements HandlerFunction<ServerResponse> {

    private final LearningItemsService learningItemsService;
    private final LearningItemsWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = LearningItemListResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                .map(Principal::getName)
                .flatMap(userId -> learningItemsService.getLearningItems(userId, extractLanguageCode(request)))
                .map(mapper::toLearningItemListResponse)
                .flatMap(learningItemListResponse -> ServerResponse.ok()
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .bodyValue(learningItemListResponse))
                .switchIfEmpty(ServerResponse.notFound()
                                             .build());
    }

    private SupportedLanguage extractLanguageCode(ServerRequest request) {
        var languageCode = request.pathVariable("languageCode");
        return SupportedLanguage.getByLanguageCode(languageCode)
                                .orElseThrow(() -> new BadRequestException("Language code is not supported", languageCode));
    }
}
