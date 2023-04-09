package in.solomk.dictionary.api.word.dto;

public record CreateWordRequest(
        String wordText,
        String translation
) {
}
