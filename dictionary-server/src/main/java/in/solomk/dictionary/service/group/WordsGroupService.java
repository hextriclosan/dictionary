package in.solomk.dictionary.service.group;

import in.solomk.dictionary.service.group.model.UnsavedWordsGroup;
import in.solomk.dictionary.service.group.model.WordsGroup;
import in.solomk.dictionary.service.language.SupportedLanguage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class WordsGroupService {

    public WordsGroupRepository repository;

    public Flux<WordsGroup> getAllUserGroups(String userId, SupportedLanguage language) {
        return repository.findAllByUserIdAndLanguageCode(userId, language.getLanguageCode());
    }

    public Mono<WordsGroup> saveWordsGroup(String userId, SupportedLanguage language, UnsavedWordsGroup unsavedWordsGroup) {
        return repository.save(userId, language, unsavedWordsGroup);
    }

    public Mono<WordsGroup> editWordsGroup(String userId, SupportedLanguage language, WordsGroup wordsGroup) {
        return repository.editGroup(userId, language, wordsGroup);
    }

    public Flux<WordsGroup> deleteWordsGroup(String userId, SupportedLanguage language, String groupId) {
        return repository.deleteGroup(groupId)
                         .thenMany(getAllUserGroups(userId, language));
    }

    public Mono<Void> deleteAllUserGroups(String userId, String languageCode) {
        return repository.deleteAllUserGroups(userId, languageCode);
    }
}
