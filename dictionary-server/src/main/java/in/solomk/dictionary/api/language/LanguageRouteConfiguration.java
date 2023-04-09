package in.solomk.dictionary.api.language;

import in.solomk.dictionary.api.language.handler.AddLanguageHandler;
import in.solomk.dictionary.api.language.handler.DeleteLanguageHandler;
import in.solomk.dictionary.api.language.handler.GetLanguagesHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LanguageRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> languagesRoute(GetLanguagesHandler getLanguagesHandler,
                                                         AddLanguageHandler addLanguageHandler,
                                                         DeleteLanguageHandler deleteLanguageHandler) {
        return RouterFunctions.route()
                              .GET("", getLanguagesHandler)
                              .PUT("/{languageCode}", addLanguageHandler)
                              .DELETE("/{languageCode}", deleteLanguageHandler)
                              .build();
    }
}
