package in.solomk.dictionary.repository.group.document;

import in.solomk.dictionary.service.group.model.WordsGroup;
import org.springframework.data.annotation.Id;

import java.util.List;

public record WordsGroupDocument(
        @Id
        String id,
        String userId,
        String languageCode,
        String name,
        List<String> wordIds
) {
    public WordsGroup toModel() {
        return new WordsGroup(id, name, wordIds);
    }
}
