package in.solomk.dictionary.api.profile;

import in.solomk.dictionary.api.profile.handler.ProfileHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProfileRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> profileRoute(ProfileHandler profileHandler) {
        return RouterFunctions.route()
                              .GET("", profileHandler)
                              .build();
    }
}
