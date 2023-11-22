package in.solomk.dictionary.repository.words;

import in.solomk.dictionary.repository.words.document.WordDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveMongoUserWordsRepository extends ReactiveMongoRepository<WordDocument, String> {

    Mono<Void> deleteByUserIdAndLanguageCode(String userId, String languageCode);

    Flux<WordDocument> findAllByUserIdAndLanguageCode(String userId, String languageCode);

    Mono<WordDocument> findByUserIdAndLanguageCodeAndId(String userId, String languageCode, String wordId);
}
