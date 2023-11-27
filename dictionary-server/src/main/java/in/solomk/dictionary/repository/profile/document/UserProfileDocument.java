package in.solomk.dictionary.repository.profile.document;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(collection = "user_profiles")
public record UserProfileDocument(
        String id,
        String name,
        String email,
        Set<String> socialProviderIds,
        List<LearningLanguageContainer> languages
) {

    public static UserProfileDocument empty() {
        return new UserProfileDocument(null, null, null, null, null);
    }
}
