import {useEffect, useState} from "react";
import {Word} from "../../client/model/word";
import {useDictionaryClient} from "../../client/dictionary-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import AddWordComponent from "./AddWordComponent";
import * as Icon from 'react-bootstrap-icons';

function WordsComponent() {
    const [words, setWords] = useState<Word[]>([]);
    const [editedWords, setEditedWords] = useState<Record<string, Word>>({});
    const dictionaryClient = useDictionaryClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;

    useEffect(() => {
        if (!currentLanguage) return;
        dictionaryClient.getWords(currentLanguage).then((userWords) => {
            setWords(userWords.words);
        });
    }, [currentLanguage, dictionaryClient]);

    if (!currentLanguage) {
        return (
            <div>
                <p>Please select a language</p>
            </div>
        );
    }

    async function removeWord(word: Word) {
        const userWords = await dictionaryClient.deleteWord(currentLanguage!, word);
        setWords(userWords.words);
    }

    async function updateWord(word: Word, field: keyof Word, newValue: string) {
        const updatedWord = {...word, [field]: newValue};
        const updated = await dictionaryClient.updateWord(currentLanguage!, updatedWord);
        setWords((prevWords) => prevWords.map((w) => (w.id === updated.id ? updated : w)));
        setEditedWords((prevEditedWords) => {
            const {[word.id]: removed, ...rest} = prevEditedWords;
            return rest;
        });
    }

    function editWord(word: Word) {
        setEditedWords((prevEditedWords) => ({...prevEditedWords, [word.id]: word}));
    }

    function isWordEdited(word: Word) {
        return editedWords[word.id] && editedWords[word.id].wordText !== word.wordText;
    }

    return (
        <div>
            <AddWordComponent onWordAdded={(word) => setWords([...words, word])}/>
            <h1>Words</h1>
            {words.map((word) => (
                <li key={word.id}>
                    {editedWords[word.id] ? (
                        <>
                            <input
                                type="text"
                                value={editedWords[word.id].wordText}
                                onChange={(event) => updateWord(word, "wordText", event.target.value)}
                            />
                            {" - "}
                            <input
                                type="text"
                                value={editedWords[word.id].translation}
                                onChange={(event) => updateWord(word, "translation", event.target.value)}
                            />
                            <button onClick={() => setEditedWords((prevEditedWords) => {
                                const {[word.id]: removed, ...rest} = prevEditedWords;
                                return rest;
                            })}>Cancel
                            </button>
                            <button disabled={!isWordEdited(word)}
                                    onClick={() => updateWord(editedWords[word.id], "wordText", editedWords[word.id].wordText)}>Save
                            </button>
                        </>
                    ) : (
                        <>
                            <span>{word.wordText} - {word.translation}</span>
                            <button onClick={() => editWord(word)}><Icon.Pencil/></button>
                            <button onClick={() => removeWord(word)}><Icon.Trash/></button>
                        </>
                    )}
                </li>
            ))}
        </div>
    );
}


export default WordsComponent;
