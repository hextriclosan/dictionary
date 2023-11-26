import React, {useEffect, useState} from "react";
import {useDictionaryClient} from "../../client/dictionary-client";
import {UnsavedWord} from "../../client/model/unsaved-word";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {Word} from "../../client/model/word";
import {Button} from "antd";

interface AddWordComponentProps {
    onWordAdded: (word: Word) => void
}

function AddWordComponent(props: AddWordComponentProps) {
    const [wordText, setWordText] = useState("");
    const [translation, setTranslation] = useState("");
    const [showAddWordInput, setShowAddWordInput] = useState(false);
    const currentLanguageContext = useCurrentLanguage();

    const client = useDictionaryClient();

    function onAddWord() {
        console.log("Adding word", currentLanguageContext, wordText, translation);
        client
            .addWord(
                currentLanguageContext.currentLanguage!,
                new UnsavedWord(wordText, translation)
            )
            .then((word) => props.onWordAdded(word));
        setShowAddWordInput(false);
    }

    function handleKeyDown(event: React.KeyboardEvent<HTMLInputElement>) {
        if (event.key === "Enter") {
            onAddWord();
        } else if (event.key === "Escape") {
            setShowAddWordInput(false);
        }
    }

    useEffect(() => {
        function handleEscapeKey(event: KeyboardEvent) {
            if (event.key === "Escape") {
                setShowAddWordInput(false);
            }
        }

        window.addEventListener("keydown", handleEscapeKey);

        return () => {
            window.removeEventListener("keydown", handleEscapeKey);
        };
    }, []);

    if (!showAddWordInput) {
        return <Button onClick={() => setShowAddWordInput(true)} type="primary" style={{ marginBottom: 16 }}>
            Add word
        </Button>
    }

    return <div>
        <h1>Add word:</h1>
        <div>
            <label htmlFor="word">Word:</label>
            <input
                id="word"
                type="text"
                value={wordText}
                onChange={(event) => setWordText(event.target.value)}
                onKeyDown={handleKeyDown}
            />
        </div>
        <div>
            <label htmlFor="translation">Translation:</label>
            <input
                id="translation"
                type="text"
                value={translation}
                onChange={(event) => setTranslation(event.target.value)}
                onKeyDown={handleKeyDown}
            />
        </div>
        <div>
            <Button onClick={onAddWord} type="primary" style={{ marginBottom: 16 }}>AddWord</Button>
            <Button onClick={() => setShowAddWordInput(false)} type="default" style={{ marginBottom: 16 }}>Cancel</Button>
        </div>
    </div>
}

export default AddWordComponent;
