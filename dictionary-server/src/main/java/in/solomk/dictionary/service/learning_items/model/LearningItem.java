package in.solomk.dictionary.service.learning_items.model;

import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
public record LearningItem(String id,
                           String text,
                           String comment,
                           String imageUrl,
                           List<ItemDefinition> definitions,
                           Set<String> groupIds
) {
}
