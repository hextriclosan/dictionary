package in.solomk.dictionary.repository.group;

import in.solomk.dictionary.repository.group.document.WordsGroupDocument;
import in.solomk.dictionary.service.group.WordsGroupRepository;
import in.solomk.dictionary.service.group.model.UnsavedWordsGroup;
import in.solomk.dictionary.service.group.model.WordsGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static java.util.Collections.emptyList;

@Repository
@AllArgsConstructor
public class PersistentWordsGroupRepository implements WordsGroupRepository {

    private final ReactiveMongoWordsGroupRepository repository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<WordsGroup> save(String userId, SupportedLanguage language, UnsavedWordsGroup unsavedWordsGroup) {
        return repository.save(new WordsGroupDocument(null, userId, language.getLanguageCode(),
                                                      unsavedWordsGroup.name(), emptyList()))
                         .map(WordsGroupDocument::toModel);
    }

    @Override
    public Mono<WordsGroup> editGroup(String userId, SupportedLanguage langauge, WordsGroup updatedGroup) {
        return reactiveMongoTemplate.findAndModify(new Query().addCriteria(Criteria.where("id").is(updatedGroup.id())
                                                                                   .and("userId").is(userId)),
                                                   new Update().set("name", updatedGroup.name()),
                                                   WordsGroupDocument.class)
                                    .map(WordsGroupDocument::toModel)
                                    .map(group -> new WordsGroup(group.id(), updatedGroup.name(), emptyList()));
    }

    @Override
    public Mono<Void> deleteGroup(String groupId) {
        return repository.deleteById(groupId);
    }

    @Override
    public Mono<Void> deleteAllUserGroups(String userId, String languageCode) {
        return repository.deleteByUserIdAndLanguageCode(userId, languageCode);
    }

    @Override
    public Flux<WordsGroup> findAllByUserIdAndLanguageCode(String userId, String languageCode) {
        return repository.findAllByUserIdAndLanguageCode(userId, languageCode)
                         .map(WordsGroupDocument::toModel);
    }
}
