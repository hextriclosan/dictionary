import {useEffect, useState} from "react";
import {Word} from "../../client/model/word";
import {useDictionaryClient} from "../../client/dictionary-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import AddWordComponent from "./AddWordComponent";
import * as Icon from 'react-bootstrap-icons';
import {Avatar, List, Typography} from 'antd';

const {Title, Text, Paragraph} = Typography;

function WordsComponent() {
    const [words, setWords] = useState<Word[]>([]);
    // const editedWords: Map<string, Word> = new Map<string, Word>();
    let editedWord: Word | undefined = undefined;
    const dictionaryClient = useDictionaryClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;

    useEffect(() => {
        if (!currentLanguage) return;
        dictionaryClient.getWords(currentLanguage)
            .then((userWords) => {
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

    function updateWord(word: Word, field: keyof Word, newValue: string) {
        const updatedWord: Word = {...word, [field]: newValue};
        console.log("Updating word", updatedWord);
        editedWord = updatedWord;
        // editedWords.set(word.id, updatedWord);
        // setEditedWords((prevEditedWords) => ({...prevEditedWords, [word.id]: updatedWord}));
        console.log("Edited words", editedWord);
    }

    async function saveWord(word: Word) {
        console.log("Saving word", editedWord, word);
        if (!editedWord) return;
        if (!isWordEdited(word)) {
            cancelEditWord(word);
            return;
        }
        const updated = await dictionaryClient.updateWord(currentLanguage!, editedWord);
        setWords((prevWords) => prevWords.map((w) => (w.id === updated.id ? updated : w)));
        editedWord = undefined;
    }

    function editWord(word: Word) {
        console.log("Editing word", word);
        editedWord = word;
        // setEditedWords((prevEditedWords) => ({...prevEditedWords, [word.id]: word}));
    }

    function isWordEdited(word: Word) {
        return editedWord?.id === word.id;
    }

    function cancelEditWord(word: Word) {
        editedWord = undefined;
    }

    return (
        <div>
            <Title>Words</Title>
            <AddWordComponent onWordAdded={(word) => setWords([...words, word])}/>
            <List dataSource={words}
                  renderItem={word => (
                      <List.Item>
                          <List.Item.Meta
                              avatar={<Avatar src={"https://randomuser.me/api/portraits/thumb/men/75.jpg"} />}
                              title={<Text editable={{
                                  onStart: () => editWord(word),
                                  onChange: (text) => updateWord(word, "wordText", text),
                                  onEnd: () => saveWord(word),
                                  onCancel: () => cancelEditWord(word),
                              }}>{word.wordText}</Text>}
                          />

                          <Text editable={{
                              onStart: () => editWord(word),
                              onChange: (text) => updateWord(word, "translation", text),
                              onEnd: () => saveWord(word),
                              onCancel: () => cancelEditWord(word),
                          }}>{word.translation}</Text>
                          <div>
                              <button onClick={() => removeWord(word)}><Icon.Trash/></button>
                          </div>
                      </List.Item>
                  )}
            />

        </div>
    );
}


export default WordsComponent;
