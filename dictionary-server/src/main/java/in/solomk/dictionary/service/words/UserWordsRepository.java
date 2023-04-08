package in.solomk.dictionary.service.words;

import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.words.model.UnsavedWord;
import in.solomk.dictionary.service.words.model.Word;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserWordsRepository {

    Mono<Void> deleteAllUserWords(String userId, SupportedLanguage language);


    Mono<Void> deleteWord(String wordId);

    Mono<Word> saveWord(String userId, SupportedLanguage language, UnsavedWord unsavedWord);

    Flux<Word> getUserWords(String userId, SupportedLanguage language);
}
