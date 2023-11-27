package in.solomk.dictionary.ft;

import in.solomk.dictionary.api.learning_item.dto.LearningItemListResponse;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.ft.fixture.LearningItemFixture;
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
        LearningItemResponse learningItemResponse = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(), LearningItemFixture.buildCreateRequest())
                                                                           .expectStatus().isOk()
                                                                           .expectBody(LearningItemResponse.class)
                                                                           .returnResult()
                                                                           .getResponseBody();

        assertThat(learningItemResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(LearningItemFixture.buildResponse());
        assertThat(learningItemResponse.id()).isNotBlank();

        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse)));
    }

    @Test
    void getsLearningItemById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdLearningItemResponse = learningItemsTestClient
                .addLearningItem(userToken, ENGLISH.getLanguageCode(), LearningItemFixture.buildCreateRequest())
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
        learningItemsTestClient.addLearningItem(userToken, "xxx", LearningItemFixture.buildCreateRequest())
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
        var learningItemResponse = learningItemsTestClient.addLearningItem(
                                                                  userToken, ENGLISH.getLanguageCode(),
                                                                  LearningItemFixture.buildCreateRequest())
                                                          .expectStatus().isOk()
                                                          .expectBody(LearningItemResponse.class)
                                                          .returnResult()
                                                          .getResponseBody();
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse)));

        userLanguagesTestClient.addLanguage(userToken, UKRAINIAN.getLanguageCode());
        var learningItemResponse2 = learningItemsTestClient.addLearningItem(
                                                                   userToken, UKRAINIAN.getLanguageCode(),
                                                                   LearningItemFixture.buildCustomCreateRequest(0, "слава", "glory"))
                                                           .expectStatus().isOk()
                                                           .expectBody(LearningItemResponse.class)
                                                           .returnResult()
                                                           .getResponseBody();

        verifyLearningItemListResponse(UKRAINIAN, new LearningItemListResponse(List.of(learningItemResponse2)));
    }

    @Test
    void returnsBadRequestIfAddingLearningItemForNotStudiedLanguage() {
        learningItemsTestClient.addLearningItem(userToken, UKRAINIAN.getLanguageCode(),
                                                LearningItemFixture.buildCustomCreateRequest(0, "слава", "glory"))
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
                                                                                             LearningItemFixture.buildCreateRequest(1))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();
        LearningItemResponse learningItemResponse2 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             LearningItemFixture.buildCreateRequest(2))
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
                                                                                             LearningItemFixture.buildCreateRequest(1))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();
        LearningItemResponse learningItemResponse2 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             LearningItemFixture.buildCreateRequest(2))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();
        LearningItemResponse learningItemResponse3 = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                             LearningItemFixture.buildCreateRequest(3))
                                                                            .expectBody(LearningItemResponse.class)
                                                                            .returnResult()
                                                                            .getResponseBody();

        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse1, learningItemResponse2, learningItemResponse3)));
    }

    @Test
    void editsLearningItem() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        LearningItemResponse learningItemResponse = learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                            LearningItemFixture.buildCreateRequest())
                                                                           .expectBody(LearningItemResponse.class)
                                                                           .returnResult()
                                                                           .getResponseBody();
        verifyLearningItemListResponse(ENGLISH, new LearningItemListResponse(List.of(learningItemResponse)));

        var definitions = List.of(LearningItemFixture.definitionWebBuilder(0)
                                                     .definition("meaning-1-edited")
                                                     .build());
        var request = LearningItemFixture.editRequestBuilder(0)
                                         .text("learningItem-1-edited")
                                         .definitions(definitions)
                                         .build();
        LearningItemResponse editedLearningItemResponse = learningItemsTestClient.editLearningItem(userToken, ENGLISH.getLanguageCode(),
                                                                                                   learningItemResponse.id(), request)
                                                                                 .expectStatus().isOk()
                                                                                 .expectBody(LearningItemResponse.class)
                                                                                 .returnResult()
                                                                                 .getResponseBody();

        assertThat(editedLearningItemResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(LearningItemFixture.responseBuilder(0)
                                              .text("learningItem-1-edited")
                                              .definitions(definitions)
                                              .build());
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
