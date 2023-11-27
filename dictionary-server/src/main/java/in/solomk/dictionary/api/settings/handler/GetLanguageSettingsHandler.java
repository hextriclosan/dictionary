package in.solomk.dictionary.api.settings.handler;

import in.solomk.dictionary.api.settings.dto.LanguageSettingsResponse;
import in.solomk.dictionary.api.settings.dto.SupportedLanguageResponse;
import in.solomk.dictionary.api.learning_item.dto.LearningItemListResponse;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.settings.LanguageSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class GetLanguageSettingsHandler implements HandlerFunction<ServerResponse> {

    private final LanguageSettingsService languageSettingsService;

    @Override
    @RegisterReflectionForBinding(value = LearningItemListResponse.class)
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .bodyValue(getLanguageSettingsResponse());
    }

    private LanguageSettingsResponse getLanguageSettingsResponse() {
        return new LanguageSettingsResponse(languageSettingsService
                                                    .getSupportedLanguages()
                                                    .stream()
                                                    .map(this::toResponse)
                                                    .toList());
    }

    private SupportedLanguageResponse toResponse(SupportedLanguage supportedLanguage) {
        return new SupportedLanguageResponse(supportedLanguage.getLanguageCode(), supportedLanguage.getLanguageName());
    }
}
