package in.solomk.dictionary.repository.learning_item;

import in.solomk.dictionary.repository.learning_item.document.LearningItemDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveMongoLearningItemsRepository extends ReactiveMongoRepository<LearningItemDocument, String> {

    Mono<Void> deleteByUserIdAndLanguageCode(String userId, String languageCode);

    Flux<LearningItemDocument> findAllByUserIdAndLanguageCode(String userId, String languageCode);

    Mono<LearningItemDocument> findByUserIdAndLanguageCodeAndId(String userId, String languageCode, String learningItemId);
}
