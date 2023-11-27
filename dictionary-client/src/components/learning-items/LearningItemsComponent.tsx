import React, {useContext, useEffect, useRef, useState} from "react";
import {LearningItem} from "../../client/model/learning-item";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import AddLearningItemComponent from "./AddLearningItemComponent";
import {Button, Form, FormInstance, Input, InputRef, List, Space, Table, Typography} from 'antd';
import {ColumnType} from "antd/es/table";
import "./LearningItemsComponent.css";
import {DeleteOutlined, MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import {useDictionaryClient} from "../../client/learning-items/learning-items-client";

const {Title, Text} = Typography;

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
            inputRef.current?.focus();
        }
    }, [editing]);

    const toggleEdit = () => {
        setEditing(!editing);
        form.setFieldsValue({[dataIndex]: record[dataIndex]});
        console.log("Toggling edit", {[dataIndex]: record[dataIndex]})
    };

    const save = async () => {
        try {
            const values = (await form.validateFields()) as { [key: string]: string };
            console.log("Saving", values, record);
            toggleEdit();
            handleSave({...record, ...values});

        } catch (errInfo) {
            console.log('Save failed:', errInfo);
        }
    };

    let childNode = children;

    if (editable) {
        if (!editing) {
            childNode = (
                <div className="editable-cell-value-wrap" style={{paddingRight: 24}} onClick={toggleEdit}>
                    {children}
                </div>
            )
        } else if (dataIndex === 'definitions') {
            childNode = (
                <Form.List name="definitions"
                           rules={[
                               {
                                   validator: async (_, definitions) => {
                                       definitions.forEach((definition: any) => {
                                           if (!definition.definition && !definition.translation) {
                                               return Promise.reject(new Error('Learning item should contain either definition or translation'));
                                           }
                                       });
                                   },
                                   message: 'Learning item should contain at least definition or translation'
                               },
                           ]}
                >
                    {(fields, {add, remove}, {errors}) => (
                        <>
                            {fields.map(({key, name, ...restField}) => (
                                <Space key={key} style={{display: 'flex', marginBottom: 8}} align="baseline">
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'definition']}
                                    >
                                        <Input placeholder="Define item"/>
                                    </Form.Item>
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'translation']}
                                    >
                                        <Input placeholder="Translation"/>
                                    </Form.Item>
                                    <Form.Item
                                        {...restField}
                                        name={[name, 'comment']}
                                    >
                                        <Input placeholder="Definition comment"/>
                                    </Form.Item>
                                    {fields.length > 1 ? (
                                        <MinusCircleOutlined
                                            onClick={() => remove(name)}
                                        />
                                    ) : null}
                                </Space>
                            ))}
                            <Form.Item>
                                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                                    Add field
                                </Button>
                            </Form.Item>
                            <Form.ErrorList errors={errors}/>
                            <Button type="primary" onClick={save}>
                                Save
                            </Button>
                            <Button onClick={() => toggleEdit()} type="default">Cancel</Button>
                        </>
                    )}
                </Form.List>
            );
        } else {
            childNode = (
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
            );
        }
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
        return <>{learningItemToRender.groupIds?.length}</>
        // return <>
        //     {learningItemToRender?.groupIds?.map((tag) => {
        //         let color = tag.length > 5 ? 'geekblue' : 'green';
        //         if (tag === 'loser') {
        //             color = 'volcano';
        //         }
        //         return (
        //             <Tag color={color} key={tag}>
        //                 {tag.toUpperCase()}
        //             </Tag>
        //         );
        //     })}
        // </>
    }

    const defaultColumns: (ColumnType<LearningItem> & { editable?: boolean; })[] = [
        {
            title: 'Text',
            dataIndex: 'text',
            key: 'text',
            editable: true,
            render: (_, learningItem: LearningItem) => {
                return <Title level={5} style={{marginBottom: 0, marginTop: 0}}>{learningItem.text}</Title>
            },
        },
        {
            title: 'Definition',
            dataIndex: 'definitions',
            key: 'translation',
            editable: true,
            render: (_, learningItem: LearningItem) => {
                const translation = learningItem?.definitions?.map((definition) => definition.translation)?.join(", ") ?? ""
                // return translation;
                return <List
                    itemLayout="horizontal"
                    dataSource={learningItem.definitions}
                    renderItem={(item, index) => (
                        <List.Item>
                            <List.Item.Meta
                                title={item.definition ?? item.translation}
                                description={item.comment}
                            >
                            </List.Item.Meta>
                            <div hidden={!item.definition}>{item.translation}</div>
                        </List.Item>
                    )}
                />
            },
        },
        {
            title: 'Groups',
            key: 'groupIds',
            dataIndex: 'groupIds',
            render: (_, learningItem: LearningItem) => renderGroups(learningItem),
        },
        {
            title: 'Action',
            key: 'action',
            width: 50,
            render: (_, learningItem: LearningItem) => (
                <Button onClick={() => removeLearningItem(learningItem)}><DeleteOutlined/></Button>
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

    const handleSave = async (learningItem: LearningItem) => {
        console.log("Saving", learningItem);
        const newData = [...learningItems];
        const index = newData.findIndex((item) => learningItem.id === item.id);
        const item = newData[index];
        newData.splice(index, 1, {
            ...item,
            ...learningItem,
        });
        setLearningItems(newData);
        await dictionaryClient.updateLearningItem(currentLanguage!, learningItem);
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
            <AddLearningItemComponent
                onLearningItemAdded={(learningItem) => setLearningItems([...learningItems, learningItem])}/>
            <Table dataSource={learningItems}
                   columns={columns as ColumnTypes}
                   components={components}
                   rowClassName={() => 'editable-row'}
                   rowKey="id"
                   size="small"
                   expandable={{
                       expandedRowRender: (record) => <p style={{margin: 0}}>{record.comment}</p>,
                       rowExpandable: (record) => record.comment,
                   }}
            />
        </div>
    );
}


export default LearningItemsComponent;
