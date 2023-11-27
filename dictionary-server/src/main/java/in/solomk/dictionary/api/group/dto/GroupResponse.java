package in.solomk.dictionary.api.group.dto;

import java.util.List;

public record GroupResponse(String id,
                            String name,
                            List<String> learningItemIds) {
}
