import {useGroupsClient} from "../../client/groups/groups-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {LearningItem} from "../../client/model/learning-item";
import {useEffect, useState} from "react";
import {Group} from "../../client/groups/group";
import {useParams} from "react-router";
import {AddLearningItemToGroupComponent} from "./AddLearningItemToGroupComponent";
import {useDictionaryClient} from "../../client/learning-items/learning-items-client";


export function SingleGroupComponent() {
    const {groupId} = useParams();
    const groupsClient = useGroupsClient();
    const currentLanguageContext = useCurrentLanguage();
    const dictionaryClient = useDictionaryClient();

    const [group, setGroup] = useState<Group | undefined>(undefined);
    const [learningItems, setLearningItems] = useState<LearningItem[]>([]);

    useEffect(() => {
        console.log("SingleGroupComponent init", currentLanguageContext);
        const fetchData = async () => {
            if (!currentLanguageContext.currentLanguage) return;
            if (!groupId) {
                console.error("No groupId provided");
                return;
            }
            ;
            const fetchedGroup = await groupsClient.getGroup(currentLanguageContext.currentLanguage!, groupId)
            setGroup(fetchedGroup);

            // todo: make one batch request / search query
            const learningItemMapping: Map<string, LearningItem> = new Map<string, LearningItem>();
            const learningItemIds = fetchedGroup.learningItemIds ?? [];
            await Promise.all(learningItemIds.map(async (learningItemId) => {
                const learningItem = await dictionaryClient.getLearningItem(currentLanguageContext.currentLanguage!, learningItemId);
                learningItemMapping.set(learningItem.id, learningItem);
            }));

            setLearningItems((fetchedGroup.learningItemIds ?? []).map(learningItemId => learningItemMapping.get(learningItemId))
                .filter(w => w !== undefined) as LearningItem[]);
        }

        fetchData()
            .catch(console.error);
    }, [currentLanguageContext, dictionaryClient, groupId, groupsClient]);

    if (!currentLanguageContext) {
        // Context value is not available yet
        return <div>
            Loading...
        </div>
    }

    async function handleLearningItemRemoval(learningItem: LearningItem) {
        await groupsClient.removeLearningItemFromGroup(currentLanguageContext.currentLanguage!, group?.id!, learningItem.id);
        setLearningItems(learningItems.filter(w => w.id !== learningItem.id));
    }

    return (
        <div hidden={!group}>
            <h1>Group {group?.name}</h1>
            <div>Learning Items</div>
            <ul>
                {learningItems.map(w =>
                    <li key={w.id}>{w.text}<button onClick={() => handleLearningItemRemoval(w)}>Remove</button></li>
                )}
            </ul>
            <AddLearningItemToGroupComponent
                groupId={group?.id!}
                existingGroupLearningItems={group?.learningItemIds ?? []}
                onLearningItemAdded={(learningItem) => setLearningItems([...learningItems, learningItem])}
            />
        </div>
    );
}
