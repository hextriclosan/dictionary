package in.solomk.dictionary.api.language.handler;

import in.solomk.dictionary.api.language.mapper.LearningLanguagesWebApiMapper;
import in.solomk.dictionary.api.learning_item.dto.LearningItemListResponse;
import in.solomk.dictionary.service.language.UserLanguagesService;
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
public class GetLanguagesHandler implements HandlerFunction<ServerResponse> {

    private final UserLanguagesService userLanguagesService;
    private final LearningLanguagesWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = LearningItemListResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                .map(Principal::getName)
                .flatMap(userLanguagesService::getLearningLanguagesByUserId)
                .map(mapper::toLearningLanguagesAggregatedResponse)
                .flatMap(languagesResponse -> ServerResponse.ok()
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .bodyValue(languagesResponse))
                .switchIfEmpty(ServerResponse.notFound()
                                             .build());
    }
}
