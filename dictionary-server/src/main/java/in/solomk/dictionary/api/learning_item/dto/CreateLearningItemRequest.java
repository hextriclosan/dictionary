package in.solomk.dictionary.api.learning_item.dto;

public record CreateLearningItemRequest(
        String text,
        String translation
) {
}
