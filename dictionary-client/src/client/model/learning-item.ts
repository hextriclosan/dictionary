import {ItemDefinition} from "./item-definition";

export interface LearningItem {
    id: string;
    text: string;
    comment?: string;
    imageUrl?: string;
    definitions?: ItemDefinition[];
    groupIds?: string[];
}
