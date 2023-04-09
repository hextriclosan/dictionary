package in.solomk.dictionary.api.settings.dto;

import java.util.List;

public record LanguageSettingsResponse(List<SupportedLanguageResponse> supportedLanguages) {
}
