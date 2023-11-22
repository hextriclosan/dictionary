package in.solomk.dictionary.repository.words.document;

import in.solomk.dictionary.service.words.model.Word;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "user_words")
public record WordDocument(
        @Id
        String id,
        String userId,
        String languageCode,
        String wordText,
        String meaning,
        String translation,
        Set<String> groupIds
) {

    public Word toModel() {
        return new Word(id, wordText, meaning, translation, groupIds);
    }
}
