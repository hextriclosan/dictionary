package in.solomk.dictionary.api.learning_item;

import in.solomk.dictionary.api.learning_item.handler.AddLearningItemHandler;
import in.solomk.dictionary.api.learning_item.handler.DeleteLearningItemHandler;
import in.solomk.dictionary.api.learning_item.handler.EditLearningItemHandler;
import in.solomk.dictionary.api.learning_item.handler.GetAllLearningItemsHandler;
import in.solomk.dictionary.api.learning_item.handler.GetLearningItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LearningItemRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> learningItemsRoute(AddLearningItemHandler addLearningItemHandler,
                                                             DeleteLearningItemHandler deleteLearningItemHandler,
                                                             EditLearningItemHandler editLearningItemHandler,
                                                             GetLearningItemHandler getLearningItemHandler,
                                                             GetAllLearningItemsHandler getAllLearningItemsHandler) {
        return RouterFunctions.route()
                              .GET("", getAllLearningItemsHandler)
                              .POST("", addLearningItemHandler)
                              .GET("/{learningItemId}", getLearningItemHandler)
                              .DELETE("/{learningItemId}", deleteLearningItemHandler)
                              .PATCH("/{learningItemId}", editLearningItemHandler)
                              .build();
    }
}
