package in.solomk.dictionary.api.mapper;

import in.solomk.dictionary.api.dto.words.CreateWordRequest;
import in.solomk.dictionary.api.dto.words.EditWordRequest;
import in.solomk.dictionary.api.dto.words.UserWordsResponse;
import in.solomk.dictionary.api.dto.words.WordResponse;
import in.solomk.dictionary.service.words.model.UnsavedWord;
import in.solomk.dictionary.service.words.model.UserWords;
import in.solomk.dictionary.service.words.model.Word;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserWordsWebApiMapper {

    UserWordsResponse toUserWordsResponse(UserWords userWordsDocument);

    UnsavedWord toUnsavedWord(CreateWordRequest createWordRequest);

    WordResponse toWordResponse(Word word);

    @Mapping(target = "id", source = "wordId")
    @Mapping(target = "wordText", source = "editWordRequest.wordText")
    @Mapping(target = "translation", source = "editWordRequest.translation")
    Word toWord(String wordId, EditWordRequest editWordRequest);
}
