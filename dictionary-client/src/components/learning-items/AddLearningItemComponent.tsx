import React, {useEffect, useState} from "react";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {LearningItem} from "../../client/model/learning-item";
import {Button} from "antd";
import {UnsavedLearningItem} from "../../client/model/unsaved-learning-item";
import {useDictionaryClient} from "../../client/learning-items/learning-items-client";

interface AddLearningItemComponentProps {
    onLearningItemAdded: (learningItem: LearningItem) => void
}

function AddLearningItemComponent(props: AddLearningItemComponentProps) {
    const [text, setText] = useState("");
    const [translation, setTranslation] = useState("");
    const [showAddLearningItemInput, setShowAddLearningItemInput] = useState(false);
    const currentLanguageContext = useCurrentLanguage();

    const client = useDictionaryClient();

    function onAddLearningItem() {
        console.log("Adding learningItem", currentLanguageContext, text, translation);
        client
            .addLearningItem(
                currentLanguageContext.currentLanguage!,
                new UnsavedLearningItem(text, translation)
            )
            .then((learningItem) => props.onLearningItemAdded(learningItem));
        setShowAddLearningItemInput(false);
    }

    function handleKeyDown(event: React.KeyboardEvent<HTMLInputElement>) {
        if (event.key === "Enter") {
            onAddLearningItem();
        } else if (event.key === "Escape") {
            setShowAddLearningItemInput(false);
        }
    }

    useEffect(() => {
        function handleEscapeKey(event: KeyboardEvent) {
            if (event.key === "Escape") {
                setShowAddLearningItemInput(false);
            }
        }

        window.addEventListener("keydown", handleEscapeKey);

        return () => {
            window.removeEventListener("keydown", handleEscapeKey);
        };
    }, []);

    if (!showAddLearningItemInput) {
        return <Button onClick={() => setShowAddLearningItemInput(true)} type="primary" style={{ marginBottom: 16 }}>
            Add learning item
        </Button>
    }

    return <div>
        <h1>Add learning item:</h1>
        <div>
            <label htmlFor="learningItem">Learning Item:</label>
            <input
                id="learningItem"
                type="text"
                value={text}
                onChange={(event) => setText(event.target.value)}
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
            <Button onClick={onAddLearningItem} type="primary" style={{ marginBottom: 16 }}>Add Learning Item</Button>
            <Button onClick={() => setShowAddLearningItemInput(false)} type="default" style={{ marginBottom: 16 }}>Cancel</Button>
        </div>
    </div>
}

export default AddLearningItemComponent;
