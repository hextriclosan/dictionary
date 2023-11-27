package in.solomk.dictionary.service.group;

import in.solomk.dictionary.service.group.model.Group;
import in.solomk.dictionary.service.group.model.UnsavedGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupRepository {

    Mono<Group> save(String userId, SupportedLanguage language, UnsavedGroup unsavedGroup);

    Mono<Group> editGroup(String userId, SupportedLanguage langauge, Group updatedGroup);

    Mono<Void> deleteGroup(String groupId);

    Mono<Void> deleteAllUserGroups(String userId, String languageCode);

    Flux<Group> findAllByUserIdAndLanguageCode(String userId, String languageCode);

    Mono<Group> findByGroupId(String userId, String languageCode, String groupId);

    Mono<Void> addLearningItemToGroup(String userId, SupportedLanguage langauge, String groupId, String learningItemId);

    Mono<Void> deleteLearningItemFromGroup(String userId, SupportedLanguage language, String groupId, String learningItemId);
}
