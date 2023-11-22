package in.solomk.dictionary.repository.group;

import in.solomk.dictionary.repository.group.document.WordsGroupDocument;
import in.solomk.dictionary.repository.words.document.WordDocument;
import in.solomk.dictionary.service.group.WordsGroupRepository;
import in.solomk.dictionary.service.group.model.UnsavedWordsGroup;
import in.solomk.dictionary.service.group.model.WordsGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.emptyList;

@Slf4j
@Repository
@AllArgsConstructor
public class PersistentWordsGroupRepository implements WordsGroupRepository {

    private final ReactiveMongoWordsGroupRepository repository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<WordsGroup> save(String userId, SupportedLanguage language, UnsavedWordsGroup unsavedWordsGroup) {
        return repository.save(new WordsGroupDocument(null, userId, language.getLanguageCode(),
                                                      unsavedWordsGroup.name(), null))
                         .map(WordsGroupDocument::toModel);
    }

    @Override
    public Mono<WordsGroup> editGroup(String userId, SupportedLanguage langauge, WordsGroup updatedGroup) {
        return reactiveMongoTemplate.findAndModify(createGroupQuery(userId, langauge, updatedGroup.id()),
                                                   new Update().set("name", updatedGroup.name()),
                                                   WordsGroupDocument.class)
                                    .map(WordsGroupDocument::toModel)
                                    .map(group -> new WordsGroup(group.id(), updatedGroup.name(), null));
    }

    @Override
    public Mono<Void> deleteGroup(String groupId) {
        return repository.deleteById(groupId);
    }

    @Override
    @Transactional
    public Mono<Void> addWordToGroup(String userId, SupportedLanguage language, String groupId, String wordId) {
        Update groupUpdate = new Update().push("wordIds", wordId);
        Update wordUpdate = new Update().push("groupIds", groupId);

        return reactiveMongoTemplate.updateFirst(createGroupQuery(userId, language, groupId), groupUpdate, WordsGroupDocument.class)
                                    .then(reactiveMongoTemplate.updateFirst(createWordQuery(userId, language, wordId), wordUpdate, WordDocument.class))
                .mapNotNull(updateResult -> {
                    log.debug("Successfully linked wordId and groupId [userId={}, wordId={}, groupId={}, ack={}]",
                              userId, groupId, wordId, updateResult.wasAcknowledged());
                    return null;
                });
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

    @Override
    public Mono<WordsGroup> findByGroupId(String userId, String languageCode, String groupId) {
        return repository.findByUserIdAndLanguageCodeAndId(userId, languageCode, groupId)
                         .map(WordsGroupDocument::toModel);
    }

    private static Query createGroupQuery(String userId, SupportedLanguage langauge, String groupId) {
        return new Query()
                .addCriteria(Criteria.where("id").is(groupId)
                                     .and("languageCode").is(langauge.getLanguageCode())
                                     .and("userId").is(userId));
    }

    private static Query createWordQuery(String userId, SupportedLanguage langauge, String wordId) {
        return new Query()
                .addCriteria(Criteria.where("id").is(wordId)
                                     .and("languageCode").is(langauge.getLanguageCode())
                                     .and("userId").is(userId));
    }
}
