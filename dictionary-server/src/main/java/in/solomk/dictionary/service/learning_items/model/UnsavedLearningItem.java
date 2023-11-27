package in.solomk.dictionary.service.learning_items.model;

import java.util.List;

public record UnsavedLearningItem(
        String text,
        String comment,
        String imageUrl,
        List<ItemDefinition> definitions
) {
}
