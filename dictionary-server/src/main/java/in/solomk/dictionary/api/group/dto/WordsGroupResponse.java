package in.solomk.dictionary.api.group.dto;

import java.util.List;

public record WordsGroupResponse(String id,
                                 String name,
                                 List<String> wordIds) {
}
