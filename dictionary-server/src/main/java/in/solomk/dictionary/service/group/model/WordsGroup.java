package in.solomk.dictionary.service.group.model;

import java.util.List;

public record WordsGroup(String id,
                         String name,
                         List<String> wordIds) {
}
