import {useEffect, useState} from "react";
import {Word} from "../../client/model/word";
import {useDictionaryClient} from "../../client/dictionary-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import AddWordComponent from "./AddWordComponent";
import * as Icon from 'react-bootstrap-icons';
import {Avatar, List, Table, Tag, Typography} from 'antd';
import {ColumnsType} from "antd/es/table";

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

    const columns: ColumnsType<Word> = [
        {
            title: 'Text',
            dataIndex: 'wordText',
            key: 'text',
            render: (_, word) => (
                <Text editable={{
                    onStart: () => editWord(word),
                    onChange: (text) => updateWord(word, "wordText", text),
                    onEnd: () => saveWord(word),
                    onCancel: () => cancelEditWord(word),
                }}>{word.wordText}</Text>),
        },
        {
            title: 'Translation',
            dataIndex: 'translation',
            key: 'translation',
            render: (_, word) => (
                <Text editable={{
                    onStart: () => editWord(word),
                    onChange: (text) => updateWord(word, "translation", text),
                    onEnd: () => saveWord(word),
                    onCancel: () => cancelEditWord(word),
                }}>{word.wordText}</Text>),
        },
        {
            title: 'Tags',
            key: 'tags',
            dataIndex: 'tags',
            render: (_, {groupIds}) => (
                <>
                    {groupIds?.map((tag) => {
                        let color = tag.length > 5 ? 'geekblue' : 'green';
                        if (tag === 'loser') {
                            color = 'volcano';
                        }
                        return (
                            <Tag color={color} key={tag}>
                                {tag.toUpperCase()}
                            </Tag>
                        );
                    })}
                </>
            ),
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                <button onClick={() => removeWord(record)}><Icon.Trash/></button>
            ),
        },
    ];


    return (
        <div>
            <Title>Words</Title>
            <AddWordComponent onWordAdded={(word) => setWords([...words, word])}/>
            <Table dataSource={words} columns={columns}/>
        </div>
    );
}


export default WordsComponent;
