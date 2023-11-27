package in.solomk.dictionary.api.learning_item.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record ItemDefinitionWebDto(String definition, String translation, String comment) {
}
