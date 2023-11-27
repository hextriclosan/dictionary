package in.solomk.dictionary.repository.learning_item.document;

import in.solomk.dictionary.service.learning_items.model.ItemDefinition;
import in.solomk.dictionary.service.learning_items.model.LearningItem;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Builder
@Document(collection = "learning_items")
public record LearningItemDocument(
        @Id
        String id,
        String userId,
        String languageCode,
        String text,
        String comment,
        String imageUrl,
        List<ItemDefinitionContainer> definitions,
        Set<String> groupIds
) {

    public LearningItem toModel() {
        List<ItemDefinition> definitionList = definitions == null ? null :
                definitions.stream()
                           .map(LearningItemDocument::toDefinition)
                           .toList();
        return new LearningItem(id, text, comment, imageUrl, definitionList, groupIds);
    }

    private static ItemDefinition toDefinition(ItemDefinitionContainer definitionContainer) {
        return new ItemDefinition(definitionContainer.definition(),
                                  definitionContainer.translation(),
                                  definitionContainer.comment());
    }
}
