package in.solomk.dictionary.api;

import in.solomk.dictionary.exception.AlreadyExistingException;
import in.solomk.dictionary.exception.BadRequestException;
import in.solomk.dictionary.exception.DictionaryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Slf4j
@Configuration
public class RouteConfiguration {

    @Bean
    RouterFunction<ServerResponse> routerFunction(
            RouterFunction<ServerResponse> profileRoute,
            RouterFunction<ServerResponse> settingsRoute,
            RouterFunction<ServerResponse> languagesRoute,
            RouterFunction<ServerResponse> learningItemsRoute,
            RouterFunction<ServerResponse> groupsRoute) {
        return RouterFunctions.route()
                              .nest(path("/api/settings"), () -> settingsRoute)
                              .nest(path("/api/languages"), () -> languagesRoute)
                              .nest(path("/api/languages/{languageCode}/learning-items"), () -> learningItemsRoute)
                              .nest(path("/api/languages/{languageCode}/groups"), () -> groupsRoute)
                              .nest(path("/api/me"), () -> profileRoute)
                              .build();
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
                Map<String, Object> errorAttributes = new LinkedHashMap<>();
                errorAttributes.put("path", request.path());
                Throwable error = getError(request);
                MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations
                        .from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
                HttpStatus errorStatus = determineHttpStatus(error, responseStatusAnnotation);
                errorAttributes.put("status", errorStatus.value());
                errorAttributes.put("error", errorStatus.getReasonPhrase());
                errorAttributes.put("message", determineMessage(error, responseStatusAnnotation));
                errorAttributes.put("requestId", request.exchange().getRequest().getId());

                log.error("Error occurred: {}", errorAttributes, error);
                return errorAttributes;
            }

            private HttpStatus determineHttpStatus(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
                if (error instanceof ResponseStatusException responseStatusException) {
                    HttpStatus httpStatus = HttpStatus.resolve(responseStatusException.getStatusCode().value());
                    if (httpStatus != null) {
                        return httpStatus;
                    }
                }
                if (error instanceof BadRequestException || error instanceof AlreadyExistingException) {
                    return HttpStatus.BAD_REQUEST;
                }
                return responseStatusAnnotation.getValue("code", HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            private String determineMessage(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
                if (error instanceof BindingResult) {
                    return error.getMessage();
                }
                if (error instanceof ResponseStatusException responseStatusException) {
                    return responseStatusException.getReason();
                }
                if (error instanceof DictionaryException) {
                    return error.getMessage();
                }
                String reason = responseStatusAnnotation.getValue("reason", String.class).orElse("");
                if (StringUtils.hasText(reason)) {
                    return reason;
                }
                return (error.getMessage() != null) ? error.getMessage() : "";
            }

        };
    }

}
