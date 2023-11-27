package in.solomk.dictionary.repository.learning_item.document;

import in.solomk.dictionary.service.learning_items.model.LearningItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "learning_items")
public record LearningItemDocument(
        @Id
        String id,
        String userId,
        String languageCode,
        String text,
        String meaning,
        String translation,
        Set<String> groupIds
) {

    public LearningItem toModel() {
        return new LearningItem(id, text, meaning, translation, groupIds);
    }
}
