package in.solomk.dictionary.repository.words;

import in.solomk.dictionary.repository.words.document.WordDocument;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.words.UserWordsRepository;
import in.solomk.dictionary.service.words.model.UnsavedWord;
import in.solomk.dictionary.service.words.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

import static java.util.Collections.emptySet;

@Repository
@RequiredArgsConstructor
public class PersistentUserWordsRepository implements UserWordsRepository {

    private final ReactiveMongoUserWordsRepository repository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

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
                                                unsavedWord.wordText(), null, unsavedWord.translation(),
                                                null))
                         .map(WordDocument::toModel);
    }

    @Override
    public Mono<Word> editWord(String userId, SupportedLanguage language, Word updatedWord) {
        var query = new Query().addCriteria(Criteria.where("id").is(updatedWord.id())
                                                    .and("userId").is(userId));
        var update = new Update().set("meaning", updatedWord.meaning())
                                 .set("translation", updatedWord.translation())
                                 .set("wordText", updatedWord.wordText());
        return reactiveMongoTemplate.findAndModify(query,
                                                   update,
                                                   WordDocument.class)
                                    .map(WordDocument::toModel)
                                    .map(word -> word.withWordText(updatedWord.wordText())
                                                     .withTranslation(updatedWord.translation())
                                                     .withMeaning(updatedWord.meaning()));
    }

    @Override
    public Flux<Word> getUserWords(String userId, SupportedLanguage language) {
        return repository.findAllByUserIdAndLanguageCode(userId, language.getLanguageCode())
                         .map(WordDocument::toModel);
    }

    @Override
    public Mono<Word> getWord(String userId, SupportedLanguage language, String wordId) {
        return repository.findByUserIdAndLanguageCodeAndId(userId, language.getLanguageCode(), wordId)
                .map(WordDocument::toModel);
    }

    @Override
    public Mono<Boolean> allWordsExist(String userId, SupportedLanguage language, Set<String> wordIds) {
        if (wordIds.isEmpty()) return Mono.just(true);

        Criteria criteria = Criteria.where("_id").in(wordIds);
        Query query = Query.query(criteria);

        return reactiveMongoTemplate.exists(query, WordDocument.class);
    }
}
