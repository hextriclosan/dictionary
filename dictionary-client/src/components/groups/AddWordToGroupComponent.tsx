import React, {useEffect, useState} from "react";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {useGroupsClient} from "../../client/groups/groups-client";
import {useDictionaryClient} from "../../client/dictionary-client";
import {Word} from "../../client/model/word";

interface AddGroupComponentProps {
    groupId: string,
    existingGroupWords: string[],
    onWordAdded: (word: Word) => void,
}

export function AddWordToGroupComponent(props: AddGroupComponentProps) {
    const groupsClient = useGroupsClient();
    const dictionaryClient = useDictionaryClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;
    const [candidates, setCandidates] = useState<Word[]>([])
    const [showAddWord, setShowAddWord] = useState(false);


    useEffect(() => {
        if (!currentLanguage) return;
        const fetchData = async () => {
            const allWords = await dictionaryClient.getWords(currentLanguage!);
            setCandidates(allWords.words.filter(w => !props.existingGroupWords.includes(w.id)));
        }

        fetchData()
            .catch(console.error);
    }, [currentLanguage, dictionaryClient, props.existingGroupWords]);


    async function handleAddWords(word: Word) {
        try {
            await groupsClient.addWordToGroup(currentLanguage!, props.groupId, word.id);
        } catch (error) {
            console.error(`Error adding word with id ${word.id} to group with id ${props.groupId}:`, error);
        }
        props.onWordAdded(word);
    }

    if (!showAddWord) {
        return <button onClick={() => setShowAddWord(true)}>Add word</button>
    }

    return (
        <div>
            {candidates.map((word) => (
                <div key={word.id}>
                    <span>{word.wordText} - {word.translation}</span>
                    <button onClick={() => handleAddWords(word)}>Add</button>
                </div>
            ))}
        </div>
    );
}
