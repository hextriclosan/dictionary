package in.solomk.dictionary.ft.client;

import in.solomk.dictionary.api.group.dto.CreateWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.EditWordsGroupRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@Component
@AllArgsConstructor
public class WordGroupsTestClient {

    private final WebTestClient webTestClient;

    public ResponseSpec getAllWordGroups(String token, String languageCode) {
        return webTestClient.get()
                            .uri("/api/languages/{languageCode}/groups", languageCode)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public ResponseSpec getWordGroup(String token, String languageCode, String groupId) {
        return webTestClient.get()
                            .uri("/api/languages/{languageCode}/groups/{groupId}", languageCode, groupId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public ResponseSpec createWordGroup(String token, String languageCode,
                                        CreateWordsGroupRequest createWordsGroupRequest) {
        return webTestClient.post()
                            .uri("/api/languages/{languageCode}/groups", languageCode)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(createWordsGroupRequest)
                            .exchange();
    }

    public ResponseSpec deleteWordGroup(String token, String languageCode, String groupId) {
        return webTestClient.delete()
                            .uri("/api/languages/{languageCode}/groups/{groupId}", languageCode, groupId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public ResponseSpec editWordGroup(String token, String languageCode, String groupId,
                                      EditWordsGroupRequest editWordsGroupRequest) {
        return webTestClient.patch()
                            .uri("/api/languages/{languageCode}/groups/{groupId}", languageCode, groupId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(editWordsGroupRequest)
                            .exchange();
    }
}
