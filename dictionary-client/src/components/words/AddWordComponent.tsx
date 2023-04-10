import React, {useEffect, useState} from "react";
import {useDictionaryClient} from "../../client/dictionary-client";
import {UnsavedWord} from "../../client/model/unsaved-word";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {Word} from "../../client/model/word";

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
    return (
        <>
            {showAddWordInput ? (
                <div className="add-word-input-container">
                    <h1>Add word:</h1>
                    <div className="add-word-input">
                        <label htmlFor="word">Word:</label>
                        <input
                            type="text"
                            value={wordText}
                            onChange={(event) => setWordText(event.target.value)}
                            onKeyDown={handleKeyDown}
                        />
                    </div>
                    <div className="add-word-input">
                        <label htmlFor="translation">Translation:</label>
                        <input
                            type="text"
                            value={translation}
                            onChange={(event) => setTranslation(event.target.value)}
                            onKeyDown={handleKeyDown}
                        />
                    </div>
                    <div className="add-word-buttons">
                        <button onClick={onAddWord}>Add word</button>
                        <button onClick={() => setShowAddWordInput(false)}>Cancel</button>
                    </div>
                </div>
            ) : (
                <button onClick={() => setShowAddWordInput(true)}>Add word</button>
            )}
        </>
    );
}

export default AddWordComponent;
