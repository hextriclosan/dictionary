package in.solomk.dictionary.api.learning_item.mapper;

import in.solomk.dictionary.api.learning_item.dto.CreateLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.EditLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.api.learning_item.dto.LearningItemListResponse;
import in.solomk.dictionary.service.learning_items.model.LearningItemsList;
import in.solomk.dictionary.service.learning_items.model.UnsavedLearningItem;
import in.solomk.dictionary.service.learning_items.model.LearningItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface LearningItemsWebApiMapper {

    LearningItemListResponse toLearningItemListResponse(LearningItemsList learningItemsListDocument);

    UnsavedLearningItem toUnsavedLearningItem(CreateLearningItemRequest createLearningItemRequest);

    LearningItemResponse toLearningItemResponse(LearningItem learningItem);

    @Mapping(target = "id", source = "learningItemId")
    @Mapping(target = "text", source = "editLearningItemRequest.text")
    @Mapping(target = "translation", source = "editLearningItemRequest.translation")
    LearningItem toLearningItem(String learningItemId, EditLearningItemRequest editLearningItemRequest);
}
