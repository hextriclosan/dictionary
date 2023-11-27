package in.solomk.dictionary.api.learning_item.dto;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record CreateLearningItemRequest(
        String text,
        String comment,
        String imageUrl,
        List<ItemDefinitionWebDto> definitions
) {
}
