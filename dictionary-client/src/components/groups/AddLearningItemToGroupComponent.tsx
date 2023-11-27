import React, {useEffect, useState} from "react";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {useGroupsClient} from "../../client/groups/groups-client";
import {LearningItem} from "../../client/model/learning-item";
import {useDictionaryClient} from "../../client/learning-items/learning-items-client";

interface AddGroupComponentProps {
    groupId: string,
    existingGroupLearningItems: string[],
    onLearningItemAdded: (learningItem: LearningItem) => void,
}

export function AddLearningItemToGroupComponent(props: AddGroupComponentProps) {
    const groupsClient = useGroupsClient();
    const dictionaryClient = useDictionaryClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;
    const [candidates, setCandidates] = useState<LearningItem[]>([])
    const [showAddLearningItem, setShowAddLearningItem] = useState(false);


    useEffect(() => {
        if (!currentLanguage) return;
        const fetchData = async () => {
            const allLearningItems = await dictionaryClient.getLearningItems(currentLanguage!);
            setCandidates(allLearningItems.learningItems.filter(learningItem => !props.existingGroupLearningItems.includes(learningItem.id)));
        }

        fetchData()
            .catch(console.error);
    }, [currentLanguage, dictionaryClient, props.existingGroupLearningItems]);


    async function handleAddLearningItems(learningItem: LearningItem) {
        try {
            await groupsClient.addLearningItemToGroup(currentLanguage!, props.groupId, learningItem.id);
        } catch (error) {
            console.error(`Error adding learningItem with id ${learningItem.id} to group with id ${props.groupId}:`, error);
        }
        props.onLearningItemAdded(learningItem);
    }

    if (!showAddLearningItem) {
        return <button onClick={() => setShowAddLearningItem(true)}>Add learning item</button>
    }

    return (
        <div>
            {candidates.map((learningItem) => (
                <div key={learningItem.id}>
                    <span>{learningItem.text} - {learningItem.translation}</span>
                    <button onClick={() => handleAddLearningItems(learningItem)}>Add</button>
                </div>
            ))}
        </div>
    );
}
