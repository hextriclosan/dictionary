package in.solomk.dictionary.repository.learning_item;

import in.solomk.dictionary.repository.learning_item.document.LearningItemDocument;
import in.solomk.dictionary.repository.profile.document.UserProfileDocument;
import in.solomk.dictionary.service.language.SupportedLanguage;
import in.solomk.dictionary.service.learning_items.model.LearningItem;
import in.solomk.dictionary.service.learning_items.model.UnsavedLearningItem;
import in.solomk.dictionary.service.profile.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LearningItemMapper {

    LearningItemDocument toDocument(LearningItem learningItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groupIds", ignore = true)
    @Mapping(target = "languageCode", source = "language")
    LearningItemDocument toDocument(String userId, SupportedLanguage language, UnsavedLearningItem unsavedLearningItem);

    LearningItem toModel(LearningItemDocument learningItemDocument);

    default String toLanguageCode(SupportedLanguage language) {
        return language.getLanguageCode();
    }
}
