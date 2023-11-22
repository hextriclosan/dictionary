package in.solomk.dictionary.api.group.dto;

import java.util.List;

public record EditWordsGroupRequest(String name,
                                    List<String> wordIds) {
}
