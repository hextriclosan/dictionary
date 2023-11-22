package in.solomk.dictionary.repository.group;

import in.solomk.dictionary.repository.group.document.WordsGroupDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveMongoWordsGroupRepository extends ReactiveMongoRepository<WordsGroupDocument, String> {

    Mono<Void> deleteByUserIdAndLanguageCode(String userId, String languageCode);

    Flux<WordsGroupDocument> findAllByUserIdAndLanguageCode(String userId, String languageCode);

    Mono<WordsGroupDocument> findByUserIdAndLanguageCodeAndId(String userId, String languageCode, String groupId);
}
