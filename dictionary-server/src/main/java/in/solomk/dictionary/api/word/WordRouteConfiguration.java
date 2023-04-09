package in.solomk.dictionary.api.word;

import in.solomk.dictionary.api.word.handler.AddWordHandler;
import in.solomk.dictionary.api.word.handler.DeleteWordHandler;
import in.solomk.dictionary.api.word.handler.EditWordHandler;
import in.solomk.dictionary.api.word.handler.GetWordsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WordRouteConfiguration {

    @Bean
    public RouterFunction<ServerResponse> wordsRoute(AddWordHandler addWordHandler,
                                                     DeleteWordHandler deleteWordHandler,
                                                     EditWordHandler editWordHandler,
                                                     GetWordsHandler getWordHandler,
                                                     GetWordsHandler getWordsHandler) {
        return RouterFunctions.route()
                              .GET("", getWordsHandler)
                              .POST("", addWordHandler)
                              .GET("/{wordId}", getWordHandler)
                              .DELETE("/{wordId}", deleteWordHandler)
                              .PATCH("/{wordId}", editWordHandler)
                              .build();
    }
}
