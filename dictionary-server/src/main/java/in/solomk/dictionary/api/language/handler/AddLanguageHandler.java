package in.solomk.dictionary.api.language.handler;

import in.solomk.dictionary.api.language.dto.LearningLanguagesAggregatedResponse;
import in.solomk.dictionary.api.language.mapper.LearningLanguagesWebApiMapper;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.service.language.UserLanguagesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@AllArgsConstructor
public class AddLanguageHandler implements HandlerFunction<ServerResponse> {

    private final UserLanguagesService userLanguagesService;
    private final LearningLanguagesWebApiMapper mapper;

    @Override
    @RegisterReflectionForBinding(value = LearningItemResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.principal()
                      .map(Principal::getName)
                      .flatMap(userId -> ServerResponse.ok()
                                                       .contentType(APPLICATION_JSON)
                                                       .body(addLanguage(request, userId),
                                                             LearningLanguagesAggregatedResponse.class));
    }

    private Mono<LearningLanguagesAggregatedResponse> addLanguage(ServerRequest request, String userId) {
        var languageCode = request.pathVariable("languageCode");
        log.trace("Adding language {} for user {}", languageCode, userId);
        return userLanguagesService.createLearningLanguage(userId, languageCode)
                                   .map(mapper::toLearningLanguagesAggregatedResponse);
    }

}
