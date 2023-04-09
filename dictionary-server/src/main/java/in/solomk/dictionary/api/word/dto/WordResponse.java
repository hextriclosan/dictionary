package in.solomk.dictionary.api.word.dto;

public record WordResponse(
        String id,
        String wordText,
        String translation
) {
}
