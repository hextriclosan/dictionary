import {ItemDefinition} from "./item-definition";

export interface UnsavedLearningItem {
    text: string;
    comment?: string;
    imageUrl?: string;
    definitions: ItemDefinition[];
}
