package in.solomk.dictionary.ft;

import in.solomk.dictionary.api.group.dto.AllWordGroupsResponse;
import in.solomk.dictionary.api.group.dto.CreateWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.EditWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.WordsGroupResponse;
import in.solomk.dictionary.ft.client.WordGroupsTestClient;
import in.solomk.dictionary.service.language.SupportedLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static in.solomk.dictionary.service.language.SupportedLanguage.ENGLISH;
import static in.solomk.dictionary.service.language.SupportedLanguage.UKRAINIAN;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("DataFlowIssue")
public class WordGroupsApiTest extends BaseFuncTest {

    @Autowired
    private WordGroupsTestClient wordGroupsTestClient;

    @Test
    void returnsEmptyWordGroupsListIfNoWordGroups() {
        verifyWordGroupsResponse(ENGLISH, new AllWordGroupsResponse(emptyList()));
    }

    @Test
    void createdWordGroup() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());

        var createdWordsGroup = wordGroupsTestClient
                .createWordGroup(userToken, ENGLISH.getLanguageCode(), new CreateWordsGroupRequest("group-1"))
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", "/api/languages/en/groups/\\w+")
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdWordsGroup)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new WordsGroupResponse(null, "group-1"));
        assertThat(createdWordsGroup.id()).isNotNull();

        verifyWordGroupsResponse(ENGLISH, new AllWordGroupsResponse(List.of(createdWordsGroup)));
    }

    @Test
    void returnsBadRequestIfLanguageIsNotStudied() {
        wordGroupsTestClient.createWordGroup(userToken, "ua", new CreateWordsGroupRequest("group-1"))
                            .expectStatus()
                            .isBadRequest()
                            .expectBody()
                            .json("""
                                          {
                                              "path": "/api/languages/ua/groups",
                                              "status": 400,
                                              "message": "Language code is not supported"
                                          }
                                          """)
                            .jsonPath("$.requestId").isNotEmpty();
    }

    @Test
    void addsWordGroupsFromDifferentLanguages() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        userLanguagesTestClient.addLanguage(userToken, UKRAINIAN.getLanguageCode());

        var createdWordsGroup = wordGroupsTestClient
                .createWordGroup(userToken, ENGLISH.getLanguageCode(), new CreateWordsGroupRequest("group-1"))
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", "/api/languages/en/groups/\\w+")
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();
        var createdWordsGroupUa = wordGroupsTestClient
                .createWordGroup(userToken, UKRAINIAN.getLanguageCode(), new CreateWordsGroupRequest("group-1"))
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", "/api/languages/uk/groups/\\w+")
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();

        verifyWordGroupsResponse(ENGLISH, new AllWordGroupsResponse(List.of(createdWordsGroup)));
        verifyWordGroupsResponse(UKRAINIAN, new AllWordGroupsResponse(List.of(createdWordsGroupUa)));
    }

    @Test
    void deletesWordsGroupById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdWordsGroup1 = wordGroupsTestClient
                .createWordGroup(userToken, ENGLISH.getLanguageCode(), new CreateWordsGroupRequest("group-1"))
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();
        var createdWordsGroup2 = wordGroupsTestClient
                .createWordGroup(userToken, ENGLISH.getLanguageCode(), new CreateWordsGroupRequest("group-1"))
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();
        verifyWordGroupsResponse(ENGLISH, new AllWordGroupsResponse(List.of(createdWordsGroup1, createdWordsGroup2)));

        wordGroupsTestClient.deleteWordGroup(userToken, ENGLISH.getLanguageCode(), createdWordsGroup1.id())
                            .expectStatus().isOk()
                            .expectBody(AllWordGroupsResponse.class)
                            .isEqualTo(new AllWordGroupsResponse(List.of(createdWordsGroup2)));

        verifyWordGroupsResponse(ENGLISH, new AllWordGroupsResponse(List.of(createdWordsGroup2)));
    }

    @Test
    void returnsGroupsInOrderOfCreation() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());

        var createdWordsGroup1 = wordGroupsTestClient
                .createWordGroup(userToken, ENGLISH.getLanguageCode(), new CreateWordsGroupRequest("y-group"))
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();
        var createdWordsGroup2 = wordGroupsTestClient
                .createWordGroup(userToken, ENGLISH.getLanguageCode(), new CreateWordsGroupRequest("x-group"))
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();

        verifyWordGroupsResponse(ENGLISH, new AllWordGroupsResponse(List.of(createdWordsGroup1, createdWordsGroup2)));
    }

    @Test
    void editsWordsGroup() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdWordsGroup = wordGroupsTestClient
                .createWordGroup(userToken, ENGLISH.getLanguageCode(), new CreateWordsGroupRequest("group-1"))
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();

        var editedWordsGroup = wordGroupsTestClient
                .editWordGroup(userToken, ENGLISH.getLanguageCode(), createdWordsGroup.id(), new EditWordsGroupRequest("group-1-edited"))
                .expectStatus().isOk()
                .expectBody(WordsGroupResponse.class)
                .returnResult()
                .getResponseBody();
        assertThat(editedWordsGroup)
                .isEqualTo(new WordsGroupResponse(createdWordsGroup.id(), "group-1-edited"));

        verifyWordGroupsResponse(ENGLISH, new AllWordGroupsResponse(List.of(editedWordsGroup)));
    }

    private void verifyWordGroupsResponse(SupportedLanguage language, AllWordGroupsResponse expectedResponse) {
        wordGroupsTestClient.getAllWordGroups(userToken, language.getLanguageCode())
                            .expectStatus().isOk()
                            .expectBody(AllWordGroupsResponse.class)
                            .isEqualTo(expectedResponse);
    }

}
