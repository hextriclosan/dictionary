package in.solomk.dictionary.ft;

import in.solomk.dictionary.api.dto.words.CreateWordRequest;
import in.solomk.dictionary.api.dto.words.EditWordRequest;
import in.solomk.dictionary.api.dto.words.UserWordsResponse;
import in.solomk.dictionary.api.dto.words.WordResponse;
import in.solomk.dictionary.service.language.SupportedLanguage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static in.solomk.dictionary.service.language.SupportedLanguage.ENGLISH;
import static in.solomk.dictionary.service.language.SupportedLanguage.UKRAINIAN;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("DataFlowIssue")
public class WordsApiTest extends BaseFuncTest {

    @Test
    void returnsEmptyUserWords() {
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(emptyList()));
    }

    @Test
    void addsWordForUser() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var request = new CreateWordRequest("word-1", "meaning-1");
        WordResponse wordResponse = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(), request)
                                                       .expectStatus().isOk()
                                                       .expectBody(WordResponse.class)
                                                       .returnResult()
                                                       .getResponseBody();

        assertThat(wordResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new WordResponse(null, "word-1", "meaning-1"));
        assertThat(wordResponse.id()).isNotBlank();

        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse)));
    }

    @Test
    void returnsBadRequestIfLanguageIsNotSupported() {
        var request = new CreateWordRequest("word-1", "meaning-1");
        userWordsTestClient.addWord(userToken, "xxx", request)
                           .expectStatus()
                           .isBadRequest()
                           .expectBody()
                           .json("""
                                         {
                                           "path": "/api/languages/xxx/words",
                                           "status": 400,
                                           "error": "Bad Request",
                                           "message": "Language code is not supported"
                                         }""")
                           .jsonPath("$.requestId").isNotEmpty();
    }

    @Test
    void addsWordsFromDifferentLanguages() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        WordResponse wordResponse = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                                new CreateWordRequest("word-1", "meaning-1"))
                                                       .expectStatus().isOk()
                                                       .expectBody(WordResponse.class)
                                                       .returnResult()
                                                       .getResponseBody();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse)));

        userLanguagesTestClient.addLanguage(userToken, UKRAINIAN.getLanguageCode());
        WordResponse wordResponse2 = userWordsTestClient.addWord(userToken, UKRAINIAN.getLanguageCode(),
                                                                 new CreateWordRequest("слава", "glory"))
                                                        .expectStatus().isOk()
                                                        .expectBody(WordResponse.class)
                                                        .returnResult()
                                                        .getResponseBody();

        verifyUserWordsResponse(UKRAINIAN, new UserWordsResponse(List.of(wordResponse2)));
    }

    @Test
    void returnsBadRequestIfAddingWordForNotStudiedLanguage() {
        userWordsTestClient.addWord(userToken, UKRAINIAN.getLanguageCode(),
                                    new CreateWordRequest("слава", "glory"))
                           .expectStatus()
                           .isBadRequest()
                           .expectBody()
                           .json("""
                                         {
                                           "path": "/api/languages/uk/words",
                                           "status": 400,
                                           "error": "Bad Request",
                                           "message": "Language is not studied. Language code: uk"
                                         }""")
                           .jsonPath("$.requestId").isNotEmpty();
    }

    @Test
    void deletesWordById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        WordResponse wordResponse1 = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                                 new CreateWordRequest("word-1", "meaning-1"))
                                                        .expectBody(WordResponse.class)
                                                        .returnResult()
                                                        .getResponseBody();
        WordResponse wordResponse2 = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                                 new CreateWordRequest("word-2", "meaning-2"))
                                                        .expectBody(WordResponse.class)
                                                        .returnResult()
                                                        .getResponseBody();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse1, wordResponse2)));

        userWordsTestClient.deleteWord(userToken, ENGLISH.getLanguageCode(), wordResponse1.id())
                           .expectStatus().isOk()
                           .expectBody(UserWordsResponse.class)
                           .isEqualTo(new UserWordsResponse(List.of(wordResponse2)));
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse2)));

        userWordsTestClient.deleteWord(userToken, ENGLISH.getLanguageCode(), wordResponse2.id())
                           .expectStatus()
                           .isOk();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(emptyList()));
    }

    @Test
    void returnsWordsInOrderOfCreation() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        WordResponse wordResponse1 = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                                 new CreateWordRequest("z-word", "meaning-1"))
                                                        .expectBody(WordResponse.class)
                                                        .returnResult()
                                                        .getResponseBody();
        WordResponse wordResponse2 = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                                 new CreateWordRequest("x-word", "meaning-2"))
                                                        .expectBody(WordResponse.class)
                                                        .returnResult()
                                                        .getResponseBody();
        WordResponse wordResponse3 = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                                 new CreateWordRequest("y-word", "meaning-3"))
                                                        .expectBody(WordResponse.class)
                                                        .returnResult()
                                                        .getResponseBody();

        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse1, wordResponse2, wordResponse3)));
    }

    @Test
    void editsWord() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        WordResponse wordResponse = userWordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                                new CreateWordRequest("word-1", "meaning-1"))
                                                       .expectBody(WordResponse.class)
                                                       .returnResult()
                                                       .getResponseBody();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse)));

        var request = new EditWordRequest("word-1-edited", "meaning-1-edited");
        WordResponse editedWordResponse = userWordsTestClient.editWord(userToken, ENGLISH.getLanguageCode(),
                                                                       wordResponse.id(), request)
                                                             .expectStatus().isOk()
                                                             .expectBody(WordResponse.class)
                                                             .returnResult()
                                                             .getResponseBody();

        assertThat(editedWordResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new WordResponse(null, "word-1-edited", "meaning-1-edited"));
        assertThat(editedWordResponse.id()).isNotBlank();

        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(editedWordResponse)));
    }

    private void verifyUserWordsResponse(SupportedLanguage language, UserWordsResponse expectedValue) {
        userWordsTestClient.getUserWords(userToken, language.getLanguageCode())
                           .expectStatus()
                           .isOk()
                           .expectBody(UserWordsResponse.class)
                           .isEqualTo(expectedValue);
    }

}
