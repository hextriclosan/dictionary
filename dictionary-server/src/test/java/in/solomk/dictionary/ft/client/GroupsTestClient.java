package in.solomk.dictionary.ft.client;

import in.solomk.dictionary.api.group.dto.CreateGroupRequest;
import in.solomk.dictionary.api.group.dto.EditGroupRequest;
import in.solomk.dictionary.api.group.dto.GroupResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@Component
@AllArgsConstructor
public class GroupsTestClient {

    private final WebTestClient webTestClient;

    public ResponseSpec getAllGroups(String token, String languageCode) {
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

    public GroupResponse getGroup(String token, String languageCode, String groupId) {
        return getGroupSpec(token, languageCode, groupId)
                .expectStatus().isOk()
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
    }


    public ResponseSpec createGroup(String token, String languageCode,
                                    CreateGroupRequest createGroupRequest) {
        return webTestClient.post()
                            .uri("/api/languages/{languageCode}/groups", languageCode)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(createGroupRequest)
                            .exchange();
    }

    public ResponseSpec deleteGroup(String token, String languageCode, String groupId) {
        return webTestClient.delete()
                            .uri("/api/languages/{languageCode}/groups/{groupId}", languageCode, groupId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public ResponseSpec editGroup(String token, String languageCode, String groupId,
                                  EditGroupRequest editGroupRequest) {
        return webTestClient.patch()
                            .uri("/api/languages/{languageCode}/groups/{groupId}", languageCode, groupId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .bodyValue(editGroupRequest)
                            .exchange();
    }

    public ResponseSpec addLearningItemToGroup(String token, String languageCode, String groupId, String learningItemId) {
        return webTestClient.put()
                            .uri("/api/languages/{languageCode}/groups/{groupId}/learning-items/{learningItemId}", languageCode, groupId, learningItemId)
                            .headers(headers -> headers.setBearerAuth(token))
                            .exchange();
    }

    public ResponseSpec deleteLearningItemFromGroup(String userToken, String languageCode, String groupId, String learningItemId) {
        return webTestClient.delete()
                            .uri("/api/languages/{languageCode}/groups/{groupId}/learning-items/{learningItemId}", languageCode, groupId, learningItemId)
                            .headers(headers -> headers.setBearerAuth(userToken))
                            .exchange();
    }
}
