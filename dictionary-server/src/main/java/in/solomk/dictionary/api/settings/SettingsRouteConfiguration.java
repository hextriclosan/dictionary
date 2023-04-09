package in.solomk.dictionary.api.settings;

import in.solomk.dictionary.api.settings.handler.GetLanguageSettingsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SettingsRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> settingsRoute(GetLanguageSettingsHandler getLanguageSettingsHandler) {
        return RouterFunctions.route()
                              .GET("/languages", getLanguageSettingsHandler)
                              .build();
    }
}
