package in.solomk.dictionary.ft;

import in.solomk.dictionary.api.learning_item.dto.CreateLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.EditLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.api.learning_item.dto.LearningItemListResponse;
import in.solomk.dictionary.service.language.SupportedLanguage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.solomk.dictionary.service.language.SupportedLanguage.ENGLISH;
import static in.solomk.dictionary.service.language.SupportedLanguage.UKRAINIAN;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("DataFlowIssue")
public class LearningItemsApiTest extends BaseFuncTest {

    @Test
    void returnsEmptyLearningItemList() {
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(emptyList()));
    }

    @Test
    void addsLearningItemForUser() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var request = new CreateLearningItemRequest("learningItem-1", "meaning-1");
        LearningItemResponse learningItemResponse = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(), request)
                                                                           .expectStatus().isOk()
                                                                           .expectBody(LearningItemResponse.class)
                                                                           .returnResult()
                                                                           .getResponseBody();

        assertThat(learningItemResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new LearningItemResponse(null, "learningItem-1", "meaning-1", null));
        assertThat(learningItemResponse.id()).isNotBlank();

        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse)));
    }

    @Test
    void getsLearningItemById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdLearningItemResponse = learningItemsTestClient
                .addLearningItem(userToken, ENGLISH.getLanguageCode(), new CreateLearningItemRequest("learningItem-1", "meaning-1"))
                .expectStatus().isOk()
                .expectBody(LearningItemResponse.class)
                .returnResult()
                .getResponseBody();

        var requestedLearningItem = learningItemsTestClient.getLearningItemSpec(userToken, ENGLISH.getLanguageCode(), createdLearningItemResponse.id())
                                                   .expectStatus().isOk()
                                                   .expectBody(LearningItemResponse.class)
                                                   .returnResult()
                                                   .getResponseBody();
        assertThat(requestedLearningItem)
                .isEqualTo(createdLearningItemResponse);
    }

    @Test
    void returnsBadRequestIfLanguageIsNotSupported() {
        var request = new CreateLearningItemRequest("learningItem-1", "meaning-1");
        learningItemsTestClient.addLearningItem(userToken, "xxx", request)
                               .expectStatus()
                               .isBadRequest()
                               .expectBody()
                               .json("""
                                         {
                                           "path": "/api/languages/xxx/learning-items",
                                           "status": 400,
                                           "error": "Bad Request",
                                           "message": "Language code is not supported"
                                         }""")
                               .jsonPath("$.requestId").isNotEmpty();
    }

    @Test
    void addsLearningItemsFromDifferentLanguages() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        LearningItemResponse learningItemResponse = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                            new CreateLearningItemRequest("learningItem-1", "meaning-1"))
                                                                           .expectStatus().isOk()
                                                                           .expectBody(LearningItemResponse.class)
                                                                           .returnResult()
                                                                           .getResponseBody();
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse)));

        userLanguagesTestClient.addLanguage(userToken, UKRAINIAN.getLanguageCode());
        LearningItemResponse learningItemResponse2 = learningItemsTestClient.addLearningItem(userToken, UKRAINIAN.getLanguageCode(),
                                                                                             new CreateLearningItemRequest("слава", "glory"))
                                                                            .expectStatus().isOk()
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();

        verifyLearningItemListResponse(UKRAINIAN, new LearningItemListResponse(List.of(learningItemResponse2)));
    }

    @Test
    void returnsBadRequestIfAddingLearningItemForNotStudiedLanguage() {
        learningItemsTestClient.addLearningItem(userToken, UKRAINIAN.getLanguageCode(),
                                                new CreateLearningItemRequest("слава", "glory"))
                               .expectStatus()
                               .isBadRequest()
                               .expectBody()
                               .json("""
                                         {
                                           "path": "/api/languages/uk/learning-items",
                                           "status": 400,
                                           "error": "Bad Request",
                                           "message": "Language is not studied. Language code: uk"
                                         }""")
                               .jsonPath("$.requestId").isNotEmpty();
    }

    @Test
    void deletesLearningItemById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        LearningItemResponse learningItemResponse1 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             new CreateLearningItemRequest("learningItem-1", "meaning-1"))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();
        LearningItemResponse learningItemResponse2 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             new CreateLearningItemRequest("learningItem-2", "meaning-2"))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse1, learningItemResponse2)));

        learningItemsTestClient.deleteLearningItem(userToken, ENGLISH.getLanguageCode(), learningItemResponse1.id())
                               .expectStatus().isOk()
                               .expectBody(LearningItemListResponse.class)
                               .isEqualTo(new LearningItemListResponse(List.of(learningItemResponse2)));
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse2)));

        learningItemsTestClient.deleteLearningItem(userToken, ENGLISH.getLanguageCode(), learningItemResponse2.id())
                               .expectStatus()
                               .isOk();
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(emptyList()));
    }

    @Test
    void returnsLearningItemsInOrderOfCreation() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        LearningItemResponse learningItemResponse1 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             new CreateLearningItemRequest("z-learningItem", "meaning-1"))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();
        LearningItemResponse learningItemResponse2 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             new CreateLearningItemRequest("x-learningItem", "meaning-2"))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();
        LearningItemResponse learningItemResponse3 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             new CreateLearningItemRequest("y-learningItem", "meaning-3"))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();

        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse1, learningItemResponse2, learningItemResponse3)));
    }

    @Test
    void editsLearningItem() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        LearningItemResponse learningItemResponse = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                            new CreateLearningItemRequest("learningItem-1", "meaning-1"))
                                                                           .expectBody(LearningItemResponse.class)
                                                                           .returnResult()
                                                                           .getResponseBody();
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse)));

        var request = new EditLearningItemRequest("learningItem-1-edited", "meaning-1-edited");
        LearningItemResponse editedLearningItemResponse = learningItemsTestClient.editLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                                   learningItemResponse.id(), request)
                                                                                 .expectStatus().isOk()
                                                                                 .expectBody(LearningItemResponse.class)
                                                                                 .returnResult()
                                                                                 .getResponseBody();

        assertThat(editedLearningItemResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new LearningItemResponse(null, "learningItem-1-edited", "meaning-1-edited", null));
        assertThat(editedLearningItemResponse.id()).isNotBlank();

        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(editedLearningItemResponse)));
    }

    private void verifyLearningItemListResponse(SupportedLanguage language, LearningItemListResponse expectedValue) {
        learningItemsTestClient.getLearningItems(userToken, language.getLanguageCode())
                               .expectStatus()
                               .isOk()
                               .expectBody(LearningItemListResponse.class)
                               .isEqualTo(expectedValue);
    }

}
