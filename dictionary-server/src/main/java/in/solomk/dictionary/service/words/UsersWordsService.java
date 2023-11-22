package in.solomk.dictionary.service.words;

import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.words.model.UnsavedWord;
import in.solomk.dictionary.service.words.model.UserWords;
import in.solomk.dictionary.service.words.model.Word;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@AllArgsConstructor
public class UsersWordsService {

    private final UserWordsRepository repository;

    public Mono<UserWords> getUserWords(String userId, SupportedLanguage language) {
        return repository.getUserWords(userId, language)
                         .collectList()
                         .map(UserWords::new);
    }

    public Mono<Word> getWord(String userId, SupportedLanguage language, String wordId) {
        return repository.getWord(userId, language, wordId);
    }

    public Mono<UserWords> deleteUserWord(String userId, SupportedLanguage language, String wordId) {
        return repository.deleteWord(wordId)
                         .then(getUserWords(userId, language));
    }

    public Mono<Void> deleteAllUserWords(String userId, SupportedLanguage language) {
        return repository.deleteAllUserWords(userId, language);
    }

    public Mono<Word> saveWord(String userId, SupportedLanguage language, UnsavedWord unsavedWord) {
        return repository.saveWord(userId, language, unsavedWord);
    }

    public Mono<Word> editWord(String userId, SupportedLanguage language, Word word) {
        return repository.editWord(userId, language, word);
    }

    public Mono<Boolean> allWordsExist(String userId, SupportedLanguage language, Set<String> wordIds) {
        return repository.allWordsExist(userId, language, wordIds);
    }

}
