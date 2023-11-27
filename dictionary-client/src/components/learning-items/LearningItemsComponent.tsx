import React, {useContext, useEffect, useRef, useState} from "react";
import {LearningItem} from "../../client/model/learning-item";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import AddLearningItemComponent from "./AddLearningItemComponent";
import {Button, Form, FormInstance, Input, InputRef, Table, Tag, Typography} from 'antd';
import {ColumnType} from "antd/es/table";
import "./LearningItemsComponent.css";
import {DeleteOutlined} from "@ant-design/icons";
import {useDictionaryClient} from "../../client/learning-items/learning-items-client";

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
    dataIndex: keyof LearningItem;
    record: LearningItem;
    handleSave: (record: LearningItem) => void;
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

function LearningItemsComponent() {
    const [learningItems, setLearningItems] = useState<LearningItem[]>([]);
    const dictionaryClient = useDictionaryClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;

    useEffect(() => {
        if (!currentLanguage) return;
        dictionaryClient.getLearningItems(currentLanguage)
            .then((learningItemList) => {
                setLearningItems(learningItemList.learningItems);
            });
    }, [currentLanguage, dictionaryClient]);

    if (!currentLanguage) {
        return (
            <div>
                <p>Please select a language</p>
            </div>
        );
    }

    async function removeLearningItem(learningItem: LearningItem) {
        const learningItemList = await dictionaryClient.deleteLearningItem(currentLanguage!, learningItem);
        setLearningItems(learningItemList.learningItems);
    }

    function renderGroups(learningItemToRender: LearningItem) {
        return <>
            {learningItemToRender?.groupIds?.map((tag) => {
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

    const defaultColumns: (ColumnType<LearningItem> & { editable?: boolean; })[] = [
        {
            title: 'Text',
            dataIndex: 'text',
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
            render: (_, learningItem: LearningItem) => renderGroups(learningItem),
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, learningItem: LearningItem) => (
                <Button onClick={() => removeLearningItem(learningItem)}><DeleteOutlined /></Button>
            ),
        },
    ];

    const columns = defaultColumns.map((col) => {
        if (!col.editable) {
            return col;
        }
        return {
            ...col,
            onCell: (record: LearningItem) => ({
                record,
                editable: col.editable,
                dataIndex: col.dataIndex,
                title: col.title,
                handleSave,
            }),
        };
    });

    const handleSave = (row: LearningItem) => {
        const newData = [...learningItems];
        const index = newData.findIndex((item) => row.id === item.id);
        const item = newData[index];
        newData.splice(index, 1, {
            ...item,
            ...row,
        });
        setLearningItems(newData);
    };

    const components = {
        body: {
            row: EditableRow,
            cell: EditableCell,
        },
    };


    return (
        <div>
            <Title>Learning Items</Title>
            <AddLearningItemComponent onLearningItemAdded={(learningItem) => setLearningItems([...learningItems, learningItem])}/>
            <Table dataSource={learningItems}
                   columns={columns as ColumnTypes}
                   components={components}
                   rowClassName={() => 'editable-row'}
                   rowKey="id"
            />
        </div>
    );
}


export default LearningItemsComponent;
