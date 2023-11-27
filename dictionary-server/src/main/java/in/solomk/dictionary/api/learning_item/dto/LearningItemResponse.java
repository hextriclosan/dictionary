package in.solomk.dictionary.api.learning_item.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
public record LearningItemResponse(
        String id,
        String text,
        String comment,
        String imageUrl,
        List<ItemDefinitionWebDto> definitions,
        Set<String> groupIds
) {
}
