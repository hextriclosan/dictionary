package in.solomk.dictionary.service.group;

import in.solomk.dictionary.service.group.model.Group;
import in.solomk.dictionary.service.group.model.UnsavedGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.learning_items.LearningItemsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

@Service
@AllArgsConstructor
public class GroupsService {

    public GroupRepository repository;
    public LearningItemsService learningItemsService;

    public Flux<Group> getAllUserGroups(String userId, SupportedLanguage language) {
        return repository.findAllByUserIdAndLanguageCode(userId, language.getLanguageCode());
    }

    public Mono<Group> getGroup(String userId, SupportedLanguage language, String groupId) {
        return repository.findByGroupId(userId, language.getLanguageCode(), groupId);
    }

    public Mono<Group> saveGroup(String userId, SupportedLanguage language, UnsavedGroup unsavedGroup) {
        return repository.save(userId, language, unsavedGroup);
    }

    public Mono<Group> editGroup(String userId, SupportedLanguage language, Group group) {
        // todo: validate elements in list unique
        return learningItemsService.allLearningItemsExist(userId, language, new HashSet<>(group.learningItemIds()))
                                   .flatMap(allExist -> {
                                       if (!allExist)
                                           return Mono.error(new RuntimeException("not exist")); // todo: throw real exception
                                       return repository.editGroup(userId, language, group);
                                   });
    }

    public Flux<Group> deleteGroup(String userId, SupportedLanguage language, String groupId) {
        return repository.deleteGroup(groupId)
                         .thenMany(getAllUserGroups(userId, language));
    }

    public Mono<Void> addLearningItemToGroup(String userId, SupportedLanguage language, String groupId, String learningItemId) {
        return repository.addLearningItemToGroup(userId, language, groupId, learningItemId);
    }

    public Mono<Void> deleteLearningItemFromGroup(String userId, SupportedLanguage language, String groupId, String learningItemId) {
        return repository.deleteLearningItemFromGroup(userId, language, groupId, learningItemId);
    }

    public Mono<Void> deleteAllUserGroups(String userId, String languageCode) {
        return repository.deleteAllUserGroups(userId, languageCode);
    }
}
