package in.solomk.dictionary.ft;

import in.solomk.dictionary.api.group.dto.CreateGroupRequest;
import in.solomk.dictionary.api.group.dto.GroupListResponse;
import in.solomk.dictionary.api.group.dto.EditGroupRequest;
import in.solomk.dictionary.api.group.dto.GroupResponse;
import in.solomk.dictionary.api.learning_item.dto.CreateLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.ft.client.GroupsTestClient;
import in.solomk.dictionary.service.language.SupportedLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static in.solomk.dictionary.service.language.SupportedLanguage.ENGLISH;
import static in.solomk.dictionary.service.language.SupportedLanguage.UKRAINIAN;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("DataFlowIssue")
public class GroupsApiTest extends BaseFuncTest {

    @Autowired
    private GroupsTestClient groupsTestClient;

    @Test
    void returnsEmptyGroupListIfNoGroups() {
        verifyGroupListResponse(ENGLISH, new GroupListResponse(emptyList()));
    }

    @Test
    void createdGroup() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());

        var createdGroup = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", "/api/languages/en/groups/\\w+")
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdGroup)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new GroupResponse(null, "group-1", null));
        assertThat(createdGroup.id()).isNotNull();

        verifyGroupListResponse(ENGLISH, new GroupListResponse(List.of(createdGroup)));
    }

    @Test
    void getsGroupById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdGroup = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectStatus().isCreated()
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();

        var requestedGroup = groupsTestClient.getGroup(userToken, ENGLISH.getLanguageCode(), createdGroup.id());

        assertThat(requestedGroup)
                .isEqualTo(createdGroup);
    }

    @Test
    void returnsBadRequestIfLanguageIsNotStudied() {
        groupsTestClient.createGroup(userToken, "ua", new CreateGroupRequest("group-1"))
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
    void addsGroupsFromDifferentLanguages() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        userLanguagesTestClient.addLanguage(userToken, UKRAINIAN.getLanguageCode());

        var createdGroup = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", "/api/languages/en/groups/\\w+")
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
        var createdGroupUa = groupsTestClient
                .createGroup(userToken, UKRAINIAN.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", "/api/languages/uk/groups/\\w+")
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();

        verifyGroupListResponse(ENGLISH, new GroupListResponse(List.of(createdGroup)));
        verifyGroupListResponse(UKRAINIAN, new GroupListResponse(List.of(createdGroupUa)));
    }

    @Test
    void deletesGroupById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdGroup1 = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
        var createdGroup2 = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
        verifyGroupListResponse(ENGLISH, new GroupListResponse(List.of(createdGroup1, createdGroup2)));

        groupsTestClient.deleteGroup(userToken, ENGLISH.getLanguageCode(), createdGroup1.id())
                        .expectStatus().isOk()
                        .expectBody(GroupListResponse.class)
                        .isEqualTo(new GroupListResponse(List.of(createdGroup2)));

        verifyGroupListResponse(ENGLISH, new GroupListResponse(List.of(createdGroup2)));
    }

    @Test
    void returnsGroupsInOrderOfCreation() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());

        var createdGroup1 = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("y-group"))
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
        var createdGroup2 = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("x-group"))
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();

        verifyGroupListResponse(ENGLISH, new GroupListResponse(List.of(createdGroup1, createdGroup2)));
    }

    @Test
    void editsGroup() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdGroup = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();

        var editedGroup = groupsTestClient
                .editGroup(userToken, ENGLISH.getLanguageCode(), createdGroup.id(), new EditGroupRequest("group-1-edited", emptyList()))
                .expectStatus().isOk()
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
        assertThat(editedGroup)
                .isEqualTo(new GroupResponse(createdGroup.id(), "group-1-edited", null));

        verifyGroupListResponse(ENGLISH, new GroupListResponse(List.of(editedGroup)));
    }

    @Test
    void addsLearningItemToGroup() {
        // todo: should handle if no LearningItem id
        // todo: should handle if no group id
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var group = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
        var learningItem = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                           new CreateLearningItemRequest("learningItem-1", "meaning-1"))
                                          .expectBody(LearningItemResponse.class)
                                          .returnResult()
                                          .getResponseBody();

        groupsTestClient.addLearningItemToGroup(userToken, ENGLISH.getLanguageCode(), group.id(), learningItem.id())
                        .expectStatus().isOk()
                        .expectBody().isEmpty();

        group = groupsTestClient.getGroup(userToken, ENGLISH.getLanguageCode(), group.id());
        learningItem = learningItemsTestClient.getLearningItemSpec(userToken, ENGLISH.getLanguageCode(), learningItem.id())
                                      .expectStatus().isOk()
                                      .expectBody(LearningItemResponse.class)
                                      .returnResult()
                                      .getResponseBody();

        assertThat(group.learningItemIds())
                .containsOnly(learningItem.id());
        assertThat(learningItem.groupIds())
                .containsOnly(group.id());
    }

    @Test
    void removesLearningItemFromGroup() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var group = groupsTestClient
                .createGroup(userToken, ENGLISH.getLanguageCode(), new CreateGroupRequest("group-1"))
                .expectBody(GroupResponse.class)
                .returnResult()
                .getResponseBody();
        var learningItem = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                           new CreateLearningItemRequest("learningItem-1", "meaning-1"))
                                          .expectBody(LearningItemResponse.class)
                                          .returnResult()
                                          .getResponseBody();
        groupsTestClient.addLearningItemToGroup(userToken, ENGLISH.getLanguageCode(), group.id(), learningItem.id())
                        .expectStatus().isOk();

        groupsTestClient.deleteLearningItemFromGroup(userToken, ENGLISH.getLanguageCode(), group.id(), learningItem.id())
                        .expectStatus().isOk()
                        .expectBody().isEmpty();

        group = groupsTestClient.getGroup(userToken, ENGLISH.getLanguageCode(), group.id());
        learningItem = learningItemsTestClient.getLearningItem(userToken, ENGLISH.getLanguageCode(), learningItem.id());

        assertThat(group.learningItemIds())
                .isEmpty();
        assertThat(learningItem.groupIds())
                .isEmpty();
    }

    private void verifyGroupListResponse(SupportedLanguage language, GroupListResponse expectedResponse) {
        groupsTestClient.getAllGroups(userToken, language.getLanguageCode())
                        .expectStatus().isOk()
                        .expectBody(GroupListResponse.class)
                        .isEqualTo(expectedResponse);
    }

}
