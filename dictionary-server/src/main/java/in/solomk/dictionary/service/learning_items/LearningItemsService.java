package in.solomk.dictionary.service.learning_items;

import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.learning_items.model.LearningItem;
import in.solomk.dictionary.service.learning_items.model.UnsavedLearningItem;
import in.solomk.dictionary.service.learning_items.model.LearningItemsList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@AllArgsConstructor
public class LearningItemsService {

    private final LearningItemsRepository repository;

    public Mono<LearningItemsList> getLearningItems(String userId, SupportedLanguage language) {
        return repository.getAllLearningItems(userId, language)
                         .collectList()
                         .map(LearningItemsList::new);
    }

    public Mono<LearningItem> getLearningItem(String userId, SupportedLanguage language, String learningItemId) {
        return repository.getLearningItem(userId, language, learningItemId);
    }

    public Mono<LearningItemsList> deleteLearningItem(String userId, SupportedLanguage language, String learningItemId) {
        return repository.deleteLearningItem(learningItemId)
                         .then(getLearningItems(userId, language));
    }

    public Mono<Void> deleteAllUserLearningItems(String userId, SupportedLanguage language) {
        return repository.deleteAllLearningItems(userId, language);
    }

    public Mono<LearningItem> saveLearningItem(String userId, SupportedLanguage language, UnsavedLearningItem unsavedLearningItem) {
        return repository.saveLearningItem(userId, language, unsavedLearningItem);
    }

    public Mono<LearningItem> editLearningItem(String userId, SupportedLanguage language, LearningItem learningItem) {
        return repository.editLearningItem(userId, language, learningItem);
    }

    public Mono<Boolean> allLearningItemsExist(String userId, SupportedLanguage language, Set<String> learningItemIds) {
        return repository.allLearningItemsExist(userId, language, learningItemIds);
    }

}
