package in.solomk.dictionary.service.learning_items;

import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.learning_items.model.LearningItem;
import in.solomk.dictionary.service.learning_items.model.UnsavedLearningItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface LearningItemsRepository {

    Mono<Void> deleteAllLearningItems(String userId, SupportedLanguage language);


    Mono<Void> deleteLearningItem(String learningItemId);

    Mono<LearningItem> saveLearningItem(String userId, SupportedLanguage language, UnsavedLearningItem unsavedLearningItem);

    Mono<LearningItem> editLearningItem(String userId, SupportedLanguage language, LearningItem learningItem);

    Flux<LearningItem> getAllLearningItems(String userId, SupportedLanguage language);

    Mono<LearningItem> getLearningItem(String userId, SupportedLanguage language, String learningItemId);

    Mono<Boolean> allLearningItemsExist(String userId, SupportedLanguage language, Set<String> learningItemIds);
}
