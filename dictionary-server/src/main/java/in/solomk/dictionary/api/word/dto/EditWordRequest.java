package in.solomk.dictionary.api.word.dto;

public record EditWordRequest(
        String wordText,
        String translation
) {
}
