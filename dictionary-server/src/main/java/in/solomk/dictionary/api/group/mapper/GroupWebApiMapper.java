package in.solomk.dictionary.api.group.mapper;

import in.solomk.dictionary.api.group.dto.CreateGroupRequest;
import in.solomk.dictionary.api.group.dto.EditGroupRequest;
import in.solomk.dictionary.api.group.dto.GroupResponse;
import in.solomk.dictionary.api.group.dto.GroupListResponse;
import in.solomk.dictionary.service.group.model.Group;
import in.solomk.dictionary.service.group.model.UnsavedGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupWebApiMapper {

    GroupResponse toGroupResponse(Group group);

    UnsavedGroup toUnsavedGroup(CreateGroupRequest createGroupRequest);

    @Mapping(target = "id", source = "groupId")
    Group toGroup(String groupId, EditGroupRequest editGroupRequest);

    List<GroupResponse> toListOfGroupResponse(List<Group> groups);

    default GroupListResponse tGroupListResponse(List<Group> groups) {
        return new GroupListResponse(toListOfGroupResponse(groups));
    }
}
