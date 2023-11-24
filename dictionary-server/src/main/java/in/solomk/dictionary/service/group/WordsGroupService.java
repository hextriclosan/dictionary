package in.solomk.dictionary.service.group;

import in.solomk.dictionary.service.group.model.UnsavedWordsGroup;
import in.solomk.dictionary.service.group.model.WordsGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.words.UsersWordsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

@Service
@AllArgsConstructor
public class WordsGroupService {

    public WordsGroupRepository repository;
    public UsersWordsService usersWordsService;

    public Flux<WordsGroup> getAllUserGroups(String userId, SupportedLanguage language) {
        return repository.findAllByUserIdAndLanguageCode(userId, language.getLanguageCode());
    }

    public Mono<WordsGroup> getGroup(String userId, SupportedLanguage language, String groupId) {
        return repository.findByGroupId(userId, language.getLanguageCode(), groupId);
    }

    public Mono<WordsGroup> saveWordsGroup(String userId, SupportedLanguage language, UnsavedWordsGroup unsavedWordsGroup) {
        return repository.save(userId, language, unsavedWordsGroup);
    }

    public Mono<WordsGroup> editWordsGroup(String userId, SupportedLanguage language, WordsGroup wordsGroup) {
        // todo: validate elements in list unique
        return usersWordsService.allWordsExist(userId, language, new HashSet<>(wordsGroup.wordIds()))
                                .flatMap(allExist -> {
                                    if (!allExist)
                                        return Mono.error(new RuntimeException("not exist")); // todo: throw real exception
                                    return repository.editGroup(userId, language, wordsGroup);
                                });
    }

    public Flux<WordsGroup> deleteWordsGroup(String userId, SupportedLanguage language, String groupId) {
        return repository.deleteGroup(groupId)
                         .thenMany(getAllUserGroups(userId, language));
    }

    public Mono<Void> addWordToGroup(String userId, SupportedLanguage language, String groupId, String wordId) {
        return repository.addWordToGroup(userId, language, groupId, wordId);
    }

    public Mono<Void> deleteWordFromGroup(String userId, SupportedLanguage language, String groupId, String wordId) {
        return repository.deleteWordFromGroup(userId, language, groupId, wordId);
    }

    public Mono<Void> deleteAllUserGroups(String userId, String languageCode) {
        return repository.deleteAllUserGroups(userId, languageCode);
    }
}
