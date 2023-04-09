package in.solomk.dictionary.api.language.mapper;

import in.solomk.dictionary.api.language.dto.LearningLanguageResponse;
import in.solomk.dictionary.api.language.dto.LearningLanguagesAggregatedResponse;
import in.solomk.dictionary.service.profile.model.LearningLanguageWithName;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LearningLanguagesWebApiMapper {

    List<LearningLanguageResponse> toLearningLanguageResponse(List<LearningLanguageWithName> languages);

    default LearningLanguagesAggregatedResponse toLearningLanguagesAggregatedResponse(List<LearningLanguageWithName> languages) {
        return new LearningLanguagesAggregatedResponse(toLearningLanguageResponse(languages));
    }
}
