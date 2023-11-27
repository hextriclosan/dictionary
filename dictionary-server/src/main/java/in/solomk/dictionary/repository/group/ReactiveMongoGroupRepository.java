package in.solomk.dictionary.repository.group;

import in.solomk.dictionary.repository.group.document.GroupDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveMongoGroupRepository extends ReactiveMongoRepository<GroupDocument, String> {

    Mono<Void> deleteByUserIdAndLanguageCode(String userId, String languageCode);

    Flux<GroupDocument> findAllByUserIdAndLanguageCode(String userId, String languageCode);

    Mono<GroupDocument> findByUserIdAndLanguageCodeAndId(String userId, String languageCode, String groupId);
}
