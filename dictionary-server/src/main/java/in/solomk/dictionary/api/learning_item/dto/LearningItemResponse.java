package in.solomk.dictionary.api.learning_item.dto;

import java.util.Set;

public record LearningItemResponse(
        String id,
        String text,
        String translation,
        Set<String> groupIds
) {
}
