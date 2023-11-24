package in.solomk.dictionary.ft.client;

import in.solomk.dictionary.api.group.dto.CreateWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.EditWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.WordsGroupResponse;
import in.solomk.dictionary.service.group.model.WordsGroup;
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

    public ResponseSpec getGroupSpec(String token, String languageCode, String groupId) {
        return webTestClient.get()
                            .uri("/api/languages/{languageCode}/groups/{groupId}", languageCode, groupId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public WordsGroupResponse getGroup(String token , String languageCode, String groupId) {
        return getGroupSpec(token, languageCode, groupId)
                .expectStatus().isOk()
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();
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

    public ResponseSpec addWordToGroup(String token, String languageCode, String groupId, String wordId) {
        return webTestClient.put()
                            .uri("/api/languages/{languageCode}/groups/{groupId}/words/{wordId}", languageCode, groupId, wordId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public ResponseSpec deleteWordFromGroup(String userToken, String languageCode, String groupId, String wordId) {
        return webTestClient.delete()
                            .uri("/api/languages/{languageCode}/groups/{groupId}/words/{wordId}", languageCode, groupId, wordId)
                            .headers(headers -> headers.setBearerAuth(userToken))
                            .exchange();
    }
}
