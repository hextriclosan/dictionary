import React, {useContext, useEffect, useRef, useState} from "react";
import {Word} from "../../client/model/word";
import {useDictionaryClient} from "../../client/dictionary-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import AddWordComponent from "./AddWordComponent";
import * as Icon from 'react-bootstrap-icons';
import {Form, FormInstance, Input, InputRef, Table, Tag, Typography} from 'antd';
import {ColumnType} from "antd/es/table";
import "./WordsComponent.css";

const {Title} = Typography;

const EditableContext = React.createContext<FormInstance<any> | null>(null);

interface EditableRowProps {
    index: number;
}

const EditableRow: React.FC<EditableRowProps> = ({index, ...props}) => {
    const [form] = Form.useForm();
    return (
        <Form form={form} component={false}>
            <EditableContext.Provider value={form}>
                <tr {...props} />
            </EditableContext.Provider>
        </Form>
    );
};

interface EditableCellProps {
    title: React.ReactNode;
    editable: boolean;
    children: React.ReactNode;
    dataIndex: keyof Word;
    record: Word;
    handleSave: (record: Word) => void;
}

const EditableCell: React.FC<EditableCellProps> = ({
                                                       title,
                                                       editable,
                                                       children,
                                                       dataIndex,
                                                       record,
                                                       handleSave,
                                                       ...restProps
                                                   }) => {
    const [editing, setEditing] = useState(false);
    const inputRef = useRef<InputRef>(null);
    const form = useContext(EditableContext)!;

    useEffect(() => {
        if (editing) {
            inputRef.current!.focus();
        }
    }, [editing]);

    const toggleEdit = () => {
        setEditing(!editing);
        form.setFieldsValue({[dataIndex]: record[dataIndex]});
    };

    const save = async () => {
        try {
            const values = await form.validateFields();

            toggleEdit();
            handleSave({...record, ...values});
        } catch (errInfo) {
            console.log('Save failed:', errInfo);
        }
    };

    let childNode = children;

    if (editable) {
        childNode = editing ? (
            <Form.Item
                style={{margin: 0}}
                name={dataIndex}
                rules={[
                    {
                        required: true,
                        message: `${title} is required.`,
                    },
                ]}
            >
                <Input ref={inputRef} onPressEnter={save} onBlur={save}/>
            </Form.Item>
        ) : (
            <div className="editable-cell-value-wrap" style={{paddingRight: 24}} onClick={toggleEdit}>
                {children}
            </div>
        );
    }

    return <td {...restProps}>{childNode}</td>;
};

type EditableTableProps = Parameters<typeof Table>[0];
type ColumnTypes = Exclude<EditableTableProps['columns'], undefined>;

function WordsComponent() {
    const [words, setWords] = useState<Word[]>([]);
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

    function isWordEdited(word: Word) {
        return editedWord?.id === word.id;
    }

    function cancelEditWord(word: Word) {
        editedWord = undefined;
    }

    function renderGroups(wordToRender: Word) {
        return <>
            {wordToRender?.groupIds?.map((tag) => {
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
    }

    const defaultColumns: (ColumnType<Word> & { editable?: boolean; })[] = [
        {
            title: 'Text',
            dataIndex: 'wordText',
            key: 'text',
            editable: true
        },
        {
            title: 'Translation',
            dataIndex: 'translation',
            key: 'translation',
            editable: true
        },
        {
            title: 'Tags',
            key: 'groupIds',
            dataIndex: 'groupIds',
            render: (_, word: Word) => renderGroups(word),
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, word: Word) => (
                <button onClick={() => removeWord(word)}><Icon.Trash/></button>
            ),
        },
    ];

    const columns = defaultColumns.map((col) => {
        if (!col.editable) {
            return col;
        }
        return {
            ...col,
            onCell: (record: Word) => ({
                record,
                editable: col.editable,
                dataIndex: col.dataIndex,
                title: col.title,
                handleSave,
            }),
        };
    });

    const handleSave = (row: Word) => {
        const newData = [...words];
        const index = newData.findIndex((item) => row.id === item.id);
        const item = newData[index];
        newData.splice(index, 1, {
            ...item,
            ...row,
        });
        setWords(newData);
    };

    const components = {
        body: {
            row: EditableRow,
            cell: EditableCell,
        },
    };


    return (
        <div>
            <Title>Words</Title>
            <AddWordComponent onWordAdded={(word) => setWords([...words, word])}/>
            <Table dataSource={words}
                   columns={columns as ColumnTypes}
                   components={components}
                   rowClassName={() => 'editable-row'}
                   rowKey="id"
            />
        </div>
    );
}


export default WordsComponent;
