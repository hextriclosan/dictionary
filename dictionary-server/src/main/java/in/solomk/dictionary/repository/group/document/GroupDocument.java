package in.solomk.dictionary.repository.group.document;

import in.solomk.dictionary.service.group.model.Group;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "groups")
public record GroupDocument(
        @Id
        String id,
        String userId,
        String languageCode,
        String name,
        List<String> learningItemIds
) {
    public Group toModel() {
        return new Group(id, name, learningItemIds);
    }
}
