package in.solomk.dictionary.api.group.mapper;

import in.solomk.dictionary.api.group.dto.AllWordGroupsResponse;
import in.solomk.dictionary.api.group.dto.CreateWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.EditWordsGroupRequest;
import in.solomk.dictionary.api.group.dto.WordsGroupResponse;
import in.solomk.dictionary.service.group.model.UnsavedWordsGroup;
import in.solomk.dictionary.service.group.model.WordsGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WordsGroupWebApiMapper {

    WordsGroupResponse toWordsGroupResponse(WordsGroup wordsGroup);

    UnsavedWordsGroup toUnsavedWordsGroup(CreateWordsGroupRequest createWordsGroupRequest);

    @Mapping(target = "id", source = "groupId")
    WordsGroup toWordsGroup(String groupId, EditWordsGroupRequest editWordsGroupRequest);

    List<WordsGroupResponse> toListOfWordsGroupResponse(List<WordsGroup> wordsGroups);

    default AllWordGroupsResponse toAllWordGroupsResponse(List<WordsGroup> wordsGroups) {
        return new AllWordGroupsResponse(toListOfWordsGroupResponse(wordsGroups));
    }
}
