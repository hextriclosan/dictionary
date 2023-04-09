package in.solomk.dictionary.repository.group.document;

import in.solomk.dictionary.service.group.model.WordsGroup;
import org.springframework.data.annotation.Id;

public record WordsGroupDocument(
        @Id
        String id,
        String userId,
        String languageCode,
        String name
) {
    public WordsGroup toModel() {
        return new WordsGroup(id, name);
    }
}
