package in.solomk.dictionary.ft.client;

import in.solomk.dictionary.api.learning_item.dto.CreateLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.EditLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;


@Component
@AllArgsConstructor
public class LearningItemsTestClient {

    private final WebTestClient webTestClient;

    public WebTestClient.ResponseSpec addLearningItem(String token, String languageCode, CreateLearningItemRequest request) {
        return webTestClient.post()
                            .uri("/api/languages/{languageCode}/learning-items", languageCode)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(request)
                            .exchange();
    }

    public WebTestClient.ResponseSpec editLearningItem(String token, String languageCode, String learningItemId, EditLearningItemRequest request) {
        return webTestClient.patch()
                            .uri("/api/languages/{languageCode}/learning-items/{learningItemId}", languageCode, learningItemId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(request)
                            .exchange();
    }

    public WebTestClient.ResponseSpec getLearningItems(String token, String languageCode) {
        return webTestClient.get()
                            .uri("/api/languages/{languageCode}/learning-items", languageCode)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public WebTestClient.ResponseSpec getLearningItemSpec(String token, String languageCode, String learningItemId) {
        return webTestClient.get()
                            .uri("/api/languages/{languageCode}/learning-items/{learningItemId}", languageCode, learningItemId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public LearningItemResponse getLearningItem(String token, String languageCode, String learningItemId) {
        return getLearningItemSpec(token, languageCode, learningItemId)
                .expectStatus().isOk()
                .expectBody(LearningItemResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public WebTestClient.ResponseSpec deleteLearningItem(String token, String languageCode, String learningItemId) {
        return webTestClient.delete()
                            .uri("/api/languages/{languageCode}/learning-items/{learningItemId}", languageCode, learningItemId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }
}

