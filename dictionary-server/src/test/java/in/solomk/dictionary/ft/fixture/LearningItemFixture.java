package in.solomk.dictionary.ft.fixture;

import in.solomk.dictionary.api.learning_item.dto.CreateLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.EditLearningItemRequest;
import in.solomk.dictionary.api.learning_item.dto.EditLearningItemRequest.EditLearningItemRequestBuilder;
import in.solomk.dictionary.api.learning_item.dto.ItemDefinitionWebDto;
import in.solomk.dictionary.api.learning_item.dto.ItemDefinitionWebDto.ItemDefinitionWebDtoBuilder;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse;
import in.solomk.dictionary.api.learning_item.dto.LearningItemResponse.LearningItemResponseBuilder;

import java.util.List;

public class LearningItemFixture {


    private static final String TEXT = "text-";
    private static final String COMMENT = "comment-";
    private static final String IMAGE_URL = "imageUrl-";
    private static final String ID = "id-";
    private static final String DEFINITION = "definition-";
    private static final String TRANSLATION = "translation-";
    private static final String DEFINITION_COMMENT = "definition-comment-";

    public static CreateLearningItemRequest buildCreateRequest(int index) {
        return new CreateLearningItemRequest(TEXT + index,
                                             COMMENT + index,
                                             IMAGE_URL + index,
                                             List.of(new ItemDefinitionWebDto(DEFINITION + index,
                                                                              TRANSLATION + index,
                                                                              DEFINITION_COMMENT + index)));
    }

    public static CreateLearningItemRequest buildCreateRequest() {
        return buildCreateRequest(0);
    }

    public static EditLearningItemRequestBuilder editRequestBuilder(int index) {
        return EditLearningItemRequest.builder()
                                      .text(TEXT + index)
                                      .comment(COMMENT + index)
                                      .imageUrl(IMAGE_URL + index)
                                      .definitions(List.of(new ItemDefinitionWebDto(DEFINITION + index,
                                                                                    TRANSLATION + index,
                                                                                    DEFINITION_COMMENT + index)));
    }

    public static CreateLearningItemRequest buildCustomCreateRequest(int index, String text, String translation) {
        return new CreateLearningItemRequest(text + "-" + index,
                                             COMMENT + index,
                                             IMAGE_URL + index,
                                             List.of(new ItemDefinitionWebDto(DEFINITION + index,
                                                                              translation + "-" + index,
                                                                              DEFINITION_COMMENT + index)));
    }

    public static LearningItemResponse buildResponse(int index) {
        return new LearningItemResponse(ID + index,
                                        TEXT + index,
                                        COMMENT + index,
                                        IMAGE_URL + index,
                                        List.of(new ItemDefinitionWebDto(DEFINITION + index,
                                                                         TRANSLATION + index,
                                                                         DEFINITION_COMMENT + index)),
                                        null);
    }

    public static LearningItemResponse buildResponse() {
        return buildResponse(0);
    }

    public static LearningItemResponseBuilder responseBuilder(int index) {
        return LearningItemResponse.builder()
                                   .id(ID + index)
                                   .text(TEXT + index)
                                   .comment(COMMENT + index)
                                   .imageUrl(IMAGE_URL + index)
                                   .definitions(List.of(new ItemDefinitionWebDto(DEFINITION + index,
                                                                                 TRANSLATION + index,
                                                                                 DEFINITION_COMMENT + index)));
    }

    public static ItemDefinitionWebDtoBuilder definitionWebBuilder(int index) {
        return ItemDefinitionWebDto.builder()
                                   .definition(DEFINITION + index)
                                   .translation(TRANSLATION + index)
                                   .comment(DEFINITION_COMMENT + index);
    }
}
