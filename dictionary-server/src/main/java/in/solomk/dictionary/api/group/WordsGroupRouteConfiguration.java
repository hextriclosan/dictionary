package in.solomk.dictionary.api.group;

import in.solomk.dictionary.api.group.handler.AddWordsGroupHandler;
import in.solomk.dictionary.api.group.handler.DeleteWordsGroupHandler;
import in.solomk.dictionary.api.group.handler.EditWordsGroupHandler;
import in.solomk.dictionary.api.group.handler.GetWordGroupsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WordsGroupRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> wordsGroupRoute(GetWordGroupsHandler getWordGroupsHandler,
                                                          AddWordsGroupHandler addWordsGroupHandler,
                                                          DeleteWordsGroupHandler deleteWordsGroupHandler,
                                                          EditWordsGroupHandler editWordsGroupHandler) {
        return RouterFunctions.route()
                              .GET("", getWordGroupsHandler)
                              .POST("", addWordsGroupHandler)
                              .DELETE("/{groupId}", deleteWordsGroupHandler)
                              .PATCH("/{groupId}", editWordsGroupHandler)
                              .build();
    }
}
