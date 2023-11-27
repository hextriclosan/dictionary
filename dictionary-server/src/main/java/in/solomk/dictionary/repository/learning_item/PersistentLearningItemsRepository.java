package in.solomk.dictionary.repository.learning_item;

import in.solomk.dictionary.repository.learning_item.document.LearningItemDocument;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.learning_items.LearningItemsRepository;
import in.solomk.dictionary.service.learning_items.model.LearningItem;
import in.solomk.dictionary.service.learning_items.model.UnsavedLearningItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PersistentLearningItemsRepository implements LearningItemsRepository {

    private final ReactiveMongoLearningItemsRepository repository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Void> deleteAllLearningItems(String userId, SupportedLanguage language) {
        return repository.deleteByUserIdAndLanguageCode(userId, language.getLanguageCode());
    }

    @Override
    public Mono<Void> deleteLearningItem(String learningItemId) {
        return repository.deleteById(learningItemId);
    }

    @Override
    public Mono<LearningItem> saveLearningItem(String userId, SupportedLanguage language, UnsavedLearningItem unsavedLearningItem) {
        return repository.save(new LearningItemDocument(null, userId, language.getLanguageCode(),
                                                        unsavedLearningItem.text(), null, unsavedLearningItem.translation(),
                                                        null))
                         .map(LearningItemDocument::toModel);
    }

    @Override
    public Mono<LearningItem> editLearningItem(String userId, SupportedLanguage language, LearningItem updatedLearningItem) {
        var query = new Query().addCriteria(Criteria.where("id").is(updatedLearningItem.id())
                                                    .and("userId").is(userId));
        var update = new Update().set("meaning", updatedLearningItem.meaning())
                                 .set("translation", updatedLearningItem.translation())
                                 .set("text", updatedLearningItem.text());
        return reactiveMongoTemplate.findAndModify(query,
                                                   update,
                                                   LearningItemDocument.class)
                                    .map(LearningItemDocument::toModel)
                                    .map(learningItem -> learningItem.withText(updatedLearningItem.text())
                                                                     .withTranslation(updatedLearningItem.translation())
                                                                     .withMeaning(updatedLearningItem.meaning()));
    }

    @Override
    public Flux<LearningItem> getAllLearningItems(String userId, SupportedLanguage language) {
        return repository.findAllByUserIdAndLanguageCode(userId, language.getLanguageCode())
                         .map(LearningItemDocument::toModel);
    }

    @Override
    public Mono<LearningItem> getLearningItem(String userId, SupportedLanguage language, String learningItemId) {
        return repository.findByUserIdAndLanguageCodeAndId(userId, language.getLanguageCode(), learningItemId)
                         .map(LearningItemDocument::toModel);
    }

    @Override
    public Mono<Boolean> allLearningItemsExist(String userId, SupportedLanguage language, Set<String> learningItemIds) {
        if (learningItemIds.isEmpty()) return Mono.just(true);

        Criteria criteria = Criteria.where("_id").in(learningItemIds);
        Query query = Query.query(criteria);

        return reactiveMongoTemplate.exists(query, LearningItemDocument.class);
    }
}
