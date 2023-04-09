package in.solomk.dictionary.service.group;

import in.solomk.dictionary.service.group.model.UnsavedWordsGroup;
import in.solomk.dictionary.service.group.model.WordsGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WordsGroupRepository {

    Mono<WordsGroup> save(String userId, SupportedLanguage language, UnsavedWordsGroup unsavedWordsGroup);

    Mono<WordsGroup> editGroup(String userId, SupportedLanguage langauge, WordsGroup updatedWord);

    Mono<Void> deleteGroup(String groupId);

    Mono<Void> deleteAllUserGroups(String userId, String languageCode);

    Flux<WordsGroup> findAllByUserIdAndLanguageCode(String userId, String languageCode);
}
