package in.solomk.dictionary.service.group.model;

import java.util.List;

public record Group(String id,
                    String name,
                    List<String> learningItemIds) {
}
