package in.solomk.dictionary.ft;

import in.solomk.dictionary.api.language.dto.LearningLanguageResponse;
import in.solomk.dictionary.api.language.dto.LearningLanguagesAggregatedResponse;
import in.solomk.dictionary.api.learning_item.dto.CreateLearningItemRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.solomk.dictionary.service.language.SupportedLanguage.ENGLISH;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class LanguagesApiTest extends BaseFuncTest {

    @Test
    void returnsEmptyUserLanguages() {
        verifyUserLanguagesResponse(new LearningLanguagesAggregatedResponse(emptyList()));
    }

    @Test
    void addsLanguageForUser() {
        LearningLanguagesAggregatedResponse expectedResponse = new LearningLanguagesAggregatedResponse(
                List.of(new LearningLanguageResponse("en", "English")));

        var userLanguagesResponse = userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode())
                                                           .expectStatus().isOk()
                                                           .expectBody(LearningLanguagesAggregatedResponse.class)
                                                           .returnResult()
                                                           .getResponseBody();

        assertThat(userLanguagesResponse).isEqualTo(expectedResponse);

        verifyUserLanguagesResponse(expectedResponse);
    }

    @Test
    void removesLanguageForUser() {
        LearningLanguagesAggregatedResponse expectedResponse = new LearningLanguagesAggregatedResponse(emptyList());

        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var userLanguagesResponse = userLanguagesTestClient.deleteLanguage(userToken, ENGLISH.getLanguageCode())
                                                           .expectStatus()
                                                           .isOk()
                                                           .expectBody(LearningLanguagesAggregatedResponse.class)
                                                           .returnResult()
                                                           .getResponseBody();
        assertThat(userLanguagesResponse).isEqualTo(expectedResponse);
        verifyUserLanguagesResponse(expectedResponse);
    }

    @Test
    void removesLanguageWithAllRelatedLearningItemsAndGroups() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        learningItemsTestClient.addLearningItem(userToken, ENGLISH.getLanguageCode(), new CreateLearningItemRequest("learningItem-1", "meaning-1"));

        userLanguagesTestClient.deleteLanguage(userToken, ENGLISH.getLanguageCode())
                               .expectStatus()
                               .isOk();
        learningItemsTestClient.getLearningItems(userToken, ENGLISH.getLanguageCode())
                               .expectStatus()
                               .isOk()
                               .expectBody()
                               .json("""
                                             {
                                               "learningItems": []
                                             }""", true);
        groupsTestClient.getAllGroups(userToken, ENGLISH.getLanguageCode())
                        .expectStatus()
                        .isOk()
                        .expectBody()
                        .json("""
                                      {
                                        "groups": []
                                      }""", true);

    }

    private void verifyUserLanguagesResponse(LearningLanguagesAggregatedResponse expectedValue) {
        userLanguagesTestClient.getLanguages(userToken)
                               .expectStatus()
                               .isOk()
                               .expectBody(LearningLanguagesAggregatedResponse.class)
                               .isEqualTo(expectedValue);
    }
}
