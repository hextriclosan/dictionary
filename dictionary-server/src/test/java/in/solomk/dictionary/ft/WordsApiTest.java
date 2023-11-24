package in.solomk.dictionary.ft;

import in.solomk.dictionary.api.word.dto.CreateWordRequest;
import in.solomk.dictionary.api.word.dto.EditWordRequest;
import in.solomk.dictionary.api.word.dto.UserWordsResponse;
import in.solomk.dictionary.api.word.dto.WordResponse;
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
        WordResponse wordResponse = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(), request)
                                                   .expectStatus().isOk()
                                                   .expectBody(WordResponse.class)
                                                   .returnResult()
                                                   .getResponseBody();

        assertThat(wordResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new WordResponse(null, "word-1", "meaning-1", null));
        assertThat(wordResponse.id()).isNotBlank();

        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse)));
    }

    @Test
    void getsWordById() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        var createdWordResponse = wordsTestClient
                .addWord(userToken, ENGLISH.getLanguageCode(), new CreateWordRequest("word-1", "meaning-1"))
                .expectStatus().isOk()
                .expectBody(WordResponse.class)
                .returnResult()
                .getResponseBody();

        var requestedWord = wordsTestClient.getWordSpec(userToken, ENGLISH.getLanguageCode(), createdWordResponse.id())
                                           .expectStatus().isOk()
                                           .expectBody(WordResponse.class)
                                           .returnResult()
                                           .getResponseBody();
        assertThat(requestedWord)
                .isEqualTo(createdWordResponse);
    }

    @Test
    void returnsBadRequestIfLanguageIsNotSupported() {
        var request = new CreateWordRequest("word-1", "meaning-1");
        wordsTestClient.addWord(userToken, "xxx", request)
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
        WordResponse wordResponse = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                            new CreateWordRequest("word-1", "meaning-1"))
                                                   .expectStatus().isOk()
                                                   .expectBody(WordResponse.class)
                                                   .returnResult()
                                                   .getResponseBody();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse)));

        userLanguagesTestClient.addLanguage(userToken, UKRAINIAN.getLanguageCode());
        WordResponse wordResponse2 = wordsTestClient.addWord(userToken, UKRAINIAN.getLanguageCode(),
                                                             new CreateWordRequest("слава", "glory"))
                                                    .expectStatus().isOk()
                                                    .expectBody(WordResponse.class)
                                                    .returnResult()
                                                    .getResponseBody();

        verifyUserWordsResponse(UKRAINIAN, new UserWordsResponse(List.of(wordResponse2)));
    }

    @Test
    void returnsBadRequestIfAddingWordForNotStudiedLanguage() {
        wordsTestClient.addWord(userToken, UKRAINIAN.getLanguageCode(),
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
        WordResponse wordResponse1 = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                             new CreateWordRequest("word-1", "meaning-1"))
                                                    .expectBody(WordResponse.class)
                                                    .returnResult()
                                                    .getResponseBody();
        WordResponse wordResponse2 = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                             new CreateWordRequest("word-2", "meaning-2"))
                                                    .expectBody(WordResponse.class)
                                                    .returnResult()
                                                    .getResponseBody();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse1, wordResponse2)));

        wordsTestClient.deleteWord(userToken, ENGLISH.getLanguageCode(), wordResponse1.id())
                       .expectStatus().isOk()
                       .expectBody(UserWordsResponse.class)
                       .isEqualTo(new UserWordsResponse(List.of(wordResponse2)));
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse2)));

        wordsTestClient.deleteWord(userToken, ENGLISH.getLanguageCode(), wordResponse2.id())
                       .expectStatus()
                       .isOk();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(emptyList()));
    }

    @Test
    void returnsWordsInOrderOfCreation() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        WordResponse wordResponse1 = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                             new CreateWordRequest("z-word", "meaning-1"))
                                                    .expectBody(WordResponse.class)
                                                    .returnResult()
                                                    .getResponseBody();
        WordResponse wordResponse2 = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                             new CreateWordRequest("x-word", "meaning-2"))
                                                    .expectBody(WordResponse.class)
                                                    .returnResult()
                                                    .getResponseBody();
        WordResponse wordResponse3 = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                             new CreateWordRequest("y-word", "meaning-3"))
                                                    .expectBody(WordResponse.class)
                                                    .returnResult()
                                                    .getResponseBody();

        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse1, wordResponse2, wordResponse3)));
    }

    @Test
    void editsWord() {
        userLanguagesTestClient.addLanguage(userToken, ENGLISH.getLanguageCode());
        WordResponse wordResponse = wordsTestClient.addWord(userToken, ENGLISH.getLanguageCode(),
                                                            new CreateWordRequest("word-1", "meaning-1"))
                                                   .expectBody(WordResponse.class)
                                                   .returnResult()
                                                   .getResponseBody();
        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(wordResponse)));

        var request = new EditWordRequest("word-1-edited", "meaning-1-edited");
        WordResponse editedWordResponse = wordsTestClient.editWord(userToken, ENGLISH.getLanguageCode(),
                                                                   wordResponse.id(), request)
                                                         .expectStatus().isOk()
                                                         .expectBody(WordResponse.class)
                                                         .returnResult()
                                                         .getResponseBody();

        assertThat(editedWordResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(new WordResponse(null, "word-1-edited", "meaning-1-edited", null));
        assertThat(editedWordResponse.id()).isNotBlank();

        verifyUserWordsResponse(ENGLISH, new UserWordsResponse(List.of(editedWordResponse)));
    }

    private void verifyUserWordsResponse(SupportedLanguage language, UserWordsResponse expectedValue) {
        wordsTestClient.getUserWords(userToken, language.getLanguageCode())
                       .expectStatus()
                       .isOk()
                       .expectBody(UserWordsResponse.class)
                       .isEqualTo(expectedValue);
    }

}
