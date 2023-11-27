package in.solomk.dictionary.repository.group;

import in.solomk.dictionary.repository.group.document.GroupDocument;
import in.solomk.dictionary.repository.learning_item.document.LearningItemDocument;
import in.solomk.dictionary.service.group.GroupRepository;
import in.solomk.dictionary.service.group.model.Group;
import in.solomk.dictionary.service.group.model.UnsavedGroup;
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

@Slf4j
@Repository
@AllArgsConstructor
public class PersistentGroupRepository implements GroupRepository {

    private final ReactiveMongoGroupRepository repository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Group> save(String userId, SupportedLanguage language, UnsavedGroup unsavedGroup) {
        return repository.save(new GroupDocument(null, userId, language.getLanguageCode(),
                                                 unsavedGroup.name(), null))
                         .map(GroupDocument::toModel);
    }

    @Override
    public Mono<Group> editGroup(String userId, SupportedLanguage langauge, Group updatedGroup) {
        return reactiveMongoTemplate.findAndModify(createGroupQuery(userId, langauge, updatedGroup.id()),
                                                   new Update().set("name", updatedGroup.name()),
                                                   GroupDocument.class)
                                    .map(GroupDocument::toModel)
                                    .map(group -> new Group(group.id(), updatedGroup.name(), null));
    }

    @Override
    public Mono<Void> deleteGroup(String groupId) {
        return repository.deleteById(groupId);
    }

    @Override
    @Transactional
    public Mono<Void> addLearningItemToGroup(String userId, SupportedLanguage language, String groupId, String learningItemId) {
        Update groupUpdate = new Update().push("learningItemIds", learningItemId);
        Update learningItemUpdate = new Update().push("groupIds", groupId);

        return reactiveMongoTemplate.updateFirst(createGroupQuery(userId, language, groupId), groupUpdate, GroupDocument.class)
                                    .then(reactiveMongoTemplate.updateFirst(createLearningItemQuery(userId, language, learningItemId), learningItemUpdate, LearningItemDocument.class))
                                    .mapNotNull(updateResult -> {
                                        log.debug("Successfully linked learningItemId and groupId [userId={}, learningItemId={}, groupId={}, ack={}]",
                                                  userId, groupId, learningItemId, updateResult.wasAcknowledged());
                                        return null;
                                    });
    }

    @Override
    public Mono<Void> deleteLearningItemFromGroup(String userId, SupportedLanguage language, String groupId, String learningItemId) {
        Update groupUpdate = new Update().pull("learningItemIds", learningItemId);
        Update learningItemUpdate = new Update().pull("groupIds", groupId);

        return reactiveMongoTemplate.updateFirst(createGroupQuery(userId, language, groupId), groupUpdate, GroupDocument.class)
                                    .then(reactiveMongoTemplate.updateFirst(createLearningItemQuery(userId, language, learningItemId), learningItemUpdate, LearningItemDocument.class))
                                    .mapNotNull(updateResult -> {
                                        log.debug("Successfully unlinked learningItemId and groupId [userId={}, learningItemId={}, groupId={}, ack={}]",
                                                  userId, groupId, learningItemId, updateResult.wasAcknowledged());
                                        return null;
                                    });
    }

    @Override
    public Mono<Void> deleteAllUserGroups(String userId, String languageCode) {
        return repository.deleteByUserIdAndLanguageCode(userId, languageCode);
    }

    @Override
    public Flux<Group> findAllByUserIdAndLanguageCode(String userId, String languageCode) {
        return repository.findAllByUserIdAndLanguageCode(userId, languageCode)
                         .map(GroupDocument::toModel);
    }

    @Override
    public Mono<Group> findByGroupId(String userId, String languageCode, String groupId) {
        return repository.findByUserIdAndLanguageCodeAndId(userId, languageCode, groupId)
                         .map(GroupDocument::toModel);
    }

    private static Query createGroupQuery(String userId, SupportedLanguage langauge, String groupId) {
        return new Query()
                .addCriteria(Criteria.where("id").is(groupId)
                                     .and("languageCode").is(langauge.getLanguageCode())
                                     .and("userId").is(userId));
    }

    private static Query createLearningItemQuery(String userId, SupportedLanguage langauge, String learningItemId) {
        return new Query()
                .addCriteria(Criteria.where("id").is(learningItemId)
                                     .and("languageCode").is(langauge.getLanguageCode())
                                     .and("userId").is(userId));
    }
}
