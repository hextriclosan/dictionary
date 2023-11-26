import {useGroupsClient} from "../../client/groups/groups-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {Word} from "../../client/model/word";
import {useEffect, useState} from "react";
import {useDictionaryClient} from "../../client/dictionary-client";
import {Group} from "../../client/groups/group";
import {useParams} from "react-router";
import {AddWordToGroupComponent} from "./AddWordToGroupComponent";


export function SingleGroupComponent() {
    const {groupId} = useParams();
    const groupsClient = useGroupsClient();
    const currentLanguageContext = useCurrentLanguage();
    const dictionaryClient = useDictionaryClient();

    const [group, setGroup] = useState<Group | undefined>(undefined);
    const [words, setWords] = useState<Word[]>([]);

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
            const wordMapping: Map<string, Word> = new Map<string, Word>();
            const wordIds = fetchedGroup.wordIds ?? [];
            await Promise.all(wordIds.map(async (wordId) => {
                const word = await dictionaryClient.getWord(currentLanguageContext.currentLanguage!, wordId);
                wordMapping.set(word.id, word);
            }));

            setWords((fetchedGroup.wordIds ?? []).map(wordId => wordMapping.get(wordId))
                .filter(w => w !== undefined) as Word[]);
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

    async function handleWordRemoval(word: Word) {
        await groupsClient.removeWordFromGroup(currentLanguageContext.currentLanguage!, group?.id!, word.id);
        setWords(words.filter(w => w.id !== word.id));
    }

    return (
        <div hidden={!group}>
            <h1>Group {group?.name}</h1>
            <div>Words</div>
            <ul>
                {words.map(w =>
                    <li key={w.id}>{w.wordText}<button onClick={() => handleWordRemoval(w)}>Remove</button></li>
                )}
            </ul>
            <AddWordToGroupComponent
                groupId={group?.id!}
                existingGroupWords={group?.wordIds ?? []}
                onWordAdded={(word) => setWords([...words, word])}
            />
        </div>
    );
}
