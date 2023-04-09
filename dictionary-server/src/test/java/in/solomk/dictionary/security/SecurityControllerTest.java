package in.solomk.dictionary.security;

import in.solomk.dictionary.api.RouteConfiguration;
import in.solomk.dictionary.api.auth.handler.AuthHandler;
import in.solomk.dictionary.api.auth.security.TokenService;
import in.solomk.dictionary.api.profile.ProfileRouteConfiguration;
import in.solomk.dictionary.api.profile.handler.ProfileHandler;
import in.solomk.dictionary.config.SecurityConfiguration;
import in.solomk.dictionary.service.profile.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;

@WebFluxTest
@Import({RouteConfiguration.class,
        ProfileRouteConfiguration.class,
        SecurityConfiguration.class,
        TokenService.class,
        ProfileHandler.class,
        AuthHandler.class})
@MockBeans({
        @MockBean(UserProfileService.class),
        @MockBean(name = "settingsRoute", classes = RouterFunction.class),
        @MockBean(name = "languagesRoute", classes = RouterFunction.class),
        @MockBean(name = "wordsRoute", classes = RouterFunction.class),
        @MockBean(name = "wordsGroupRoute", classes = RouterFunction.class),
})
@ActiveProfiles("test")
public class SecurityControllerTest {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenUnauthenticatedReturns401() {
        webTestClient.get()
                     .uri("/api/me")
                     .exchange()
                     .expectStatus()
                     .isUnauthorized();
    }

    @Test
    void whenAuthenticatedPrintsHello() {
        String token = tokenService.generateToken("someUserId");

        webTestClient.get()
                     .uri("/api/me")
                     .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.message", "Hello, max");
    }

}
