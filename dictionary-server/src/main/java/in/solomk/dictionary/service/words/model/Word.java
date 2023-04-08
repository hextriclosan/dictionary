package in.solomk.dictionary.service.words.model;

import lombok.With;

public record Word(String id,
                   @With String wordText,
                   @With String meaning,
                   @With String translation
) {
}
