package in.solomk.dictionary.api.group;

import in.solomk.dictionary.api.group.handler.AddLearningItemToGroupHandler;
import in.solomk.dictionary.api.group.handler.CreateGroupHandler;
import in.solomk.dictionary.api.group.handler.DeleteGroupHandler;
import in.solomk.dictionary.api.group.handler.EditGroupHandler;
import in.solomk.dictionary.api.group.handler.GetGroupHandler;
import in.solomk.dictionary.api.group.handler.GetAllGroupsHandler;
import in.solomk.dictionary.api.group.handler.RemoveLearningItemFromGroupHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GroupsRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> groupsRoute(GetAllGroupsHandler getAllGroupsHandler,
                                                      CreateGroupHandler createGroupHandler,
                                                      DeleteGroupHandler deleteGroupHandler,
                                                      EditGroupHandler editGroupHandler,
                                                      GetGroupHandler getGroupHandler,
                                                      AddLearningItemToGroupHandler addLearningItemToGroupHandler,
                                                      RemoveLearningItemFromGroupHandler removeLearningItemFromGroupHandler) {
        return RouterFunctions.route()
                              .GET("", getAllGroupsHandler)
                              .POST("", createGroupHandler)
                              .GET("/{groupId}", getGroupHandler)
                              .DELETE("/{groupId}", deleteGroupHandler)
                              .PATCH("/{groupId}", editGroupHandler)
                              .PUT("/{groupId}/learning-items/{learningItemId}", addLearningItemToGroupHandler)
                              .DELETE("/{groupId}/learning-items/{learningItemId}", removeLearningItemFromGroupHandler)
                              .build();
    }
}
