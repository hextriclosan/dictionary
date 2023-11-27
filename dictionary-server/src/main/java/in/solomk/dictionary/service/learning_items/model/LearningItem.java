package in.solomk.dictionary.service.learning_items.model;

import lombok.With;

import java.util.Set;

public record LearningItem(String id,
                           @With String text,
                           @With String meaning,
                           @With String translation,
                           Set<String> groupIds
) {
}
