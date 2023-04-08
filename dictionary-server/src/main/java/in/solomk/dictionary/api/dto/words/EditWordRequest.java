package in.solomk.dictionary.api.dto.words;

public record EditWordRequest(
        String wordText,
        String translation
) {
}
