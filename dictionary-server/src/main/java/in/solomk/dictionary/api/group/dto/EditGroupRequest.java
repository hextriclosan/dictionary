package in.solomk.dictionary.api.group.dto;

import java.util.List;

public record EditGroupRequest(String name,
                               List<String> learningItemIds) {
}
