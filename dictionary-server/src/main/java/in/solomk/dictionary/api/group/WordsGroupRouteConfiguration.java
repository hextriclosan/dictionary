package in.solomk.dictionary.api.group;

import in.solomk.dictionary.api.group.handler.AddWordToGroupHandler;
import in.solomk.dictionary.api.group.handler.CreateGroupHandler;
import in.solomk.dictionary.api.group.handler.DeleteGroupHandler;
import in.solomk.dictionary.api.group.handler.EditGroupHandler;
import in.solomk.dictionary.api.group.handler.GetGroupHandler;
import in.solomk.dictionary.api.group.handler.GetAllGroupsHandler;
import in.solomk.dictionary.api.group.handler.RemoveWordFromGroupHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WordsGroupRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> wordsGroupRoute(GetAllGroupsHandler getAllGroupsHandler,
                                                          CreateGroupHandler createGroupHandler,
                                                          DeleteGroupHandler deleteGroupHandler,
                                                          EditGroupHandler editGroupHandler,
                                                          GetGroupHandler getGroupHandler,
                                                          AddWordToGroupHandler addWordToGroupHandler,
                                                          RemoveWordFromGroupHandler removeWordFromGroupHandler) {
        return RouterFunctions.route()
                              .GET("", getAllGroupsHandler)
                              .POST("", createGroupHandler)
                              .GET("/{groupId}", getGroupHandler)
                              .DELETE("/{groupId}", deleteGroupHandler)
                              .PATCH("/{groupId}", editGroupHandler)
                              .PUT("/{groupId}/words/{wordId}", addWordToGroupHandler)
                              .DELETE("/{groupId}/words/{wordId}", removeWordFromGroupHandler)
                              .build();
    }
}
