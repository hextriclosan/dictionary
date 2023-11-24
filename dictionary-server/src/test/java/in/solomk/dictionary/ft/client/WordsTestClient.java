package in.solomk.dictionary.ft.client;

import in.solomk.dictionary.api.word.dto.CreateWordRequest;
import in.solomk.dictionary.api.word.dto.EditWordRequest;
import in.solomk.dictionary.api.word.dto.WordResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;


@Component
@AllArgsConstructor
public class WordsTestClient {

    private final WebTestClient webTestClient;

    public WebTestClient.ResponseSpec addWord(String token, String languageCode, CreateWordRequest request) {
        return webTestClient.post()
                            .uri("/api/languages/{languageCode}/words", languageCode)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(request)
                            .exchange();
    }

    public WebTestClient.ResponseSpec editWord(String token, String languageCode, String wordId, EditWordRequest request) {
        return webTestClient.patch()
                            .uri("/api/languages/{languageCode}/words/{wordId}", languageCode, wordId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(request)
                            .exchange();
    }

    public WebTestClient.ResponseSpec getUserWords(String token, String languageCode) {
        return webTestClient.get()
                            .uri("/api/languages/{languageCode}/words", languageCode)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public WebTestClient.ResponseSpec getWordSpec(String token, String languageCode, String wordId) {
        return webTestClient.get()
                            .uri("/api/languages/{languageCode}/words/{wordId}", languageCode, wordId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public WordResponse getWord(String token, String languageCode, String wordId) {
        return getWordSpec(token, languageCode, wordId)
                .expectStatus().isOk()
                .expectBody(WordResponse.class)
                .returnResult()
                .getResponseBody();
    }

    public WebTestClient.ResponseSpec deleteWord(String token, String languageCode, String wordId) {
        return webTestClient.delete()
                            .uri("/api/languages/{languageCode}/words/{wordId}", languageCode, wordId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }
}
