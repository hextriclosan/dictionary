package in.solomk.dictionary.api.word.dto;

import java.util.Set;

public record WordResponse(
        String id,
        String wordText,
        String translation,
        Set<String> groupIds
) {
}
