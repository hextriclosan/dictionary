package in.solomk.dictionary.repository.words;

import in.solomk.dictionary.repository.words.document.WordDocument;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.words.UserWordsRepository;
import in.solomk.dictionary.service.words.model.UnsavedWord;
import in.solomk.dictionary.service.words.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class PersistentUserWordsRepository implements UserWordsRepository {

    private final ReactiveMongoUserWordsRepository repository;

    @Override
    public Mono<Void> deleteAllUserWords(String userId, SupportedLanguage language) {
        return repository.deleteByUserIdAndLanguageCode(userId, language.getLanguageCode());
    }

    @Override
    public Mono<Void> deleteWord(String wordId) {
        return repository.deleteById(wordId);
    }

    @Override
    public Mono<Word> saveWord(String userId, SupportedLanguage language, UnsavedWord unsavedWord) {
        return repository.save(new WordDocument(null, userId, language.getLanguageCode(),
                                                unsavedWord.wordText(), null, unsavedWord.translation()))
                         .map(WordDocument::toModel);
    }

    @Override
    public Flux<Word> getUserWords(String userId, SupportedLanguage language) {
        return repository.findAllByUserIdAndLanguageCode(userId, language.getLanguageCode())
                         .map(WordDocument::toModel);
    }
}
