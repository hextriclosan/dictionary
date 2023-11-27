import React, {useEffect, useState} from "react";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {LearningItem} from "../../client/model/learning-item";
import {Button, Form, Input, Space} from "antd";
import {useDictionaryClient} from "../../client/learning-items/learning-items-client";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import {UnsavedLearningItem} from "../../client/model/unsaved-learning-item";

interface AddLearningItemComponentProps {
    onLearningItemAdded: (learningItem: LearningItem) => void
}

function AddLearningItemComponent(props: AddLearningItemComponentProps) {
    const [showAddLearningItemInput, setShowAddLearningItemInput] = useState(false);
    const currentLanguageContext = useCurrentLanguage();
    const client = useDictionaryClient();

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
        return <Button onClick={() => setShowAddLearningItemInput(true)} type="primary" style={{marginBottom: 16}}>
            Add learning item
        </Button>
    }

    const onFinish = async (unsavedLearningItem: UnsavedLearningItem) => {
        console.log("Adding learningItem from form", unsavedLearningItem, currentLanguageContext.currentLanguage);
        const learningItem = await client
            .addLearningItem(currentLanguageContext.currentLanguage!, unsavedLearningItem);
        props.onLearningItemAdded(learningItem);
        setShowAddLearningItemInput(false);
    };

    return <div>
        <h1>Add learning item:</h1>

        <Form
            name="learning-item"
            onFinish={onFinish}
            style={{maxWidth: 600}}
            autoComplete="off"
            initialValues={{
                definitions: [
                    {
                        definition: "",
                        translation: "",
                        comment: ""
                    }
                ]
            }}
        >
            <Form.Item
                label="Text"
                name="text"
                required={true}
            >
                <Input placeholder="Learning Item Text"/>
            </Form.Item>
            <Form.Item
                label="Comment"
                name="comment"
            >
                <Input placeholder="Learning Item Comment"/>
            </Form.Item>
            <Form.Item
                label="Image URL"
                name="imageUrl"
                rules={[{type: 'url', message: 'Please enter a valid URL'}]}
            >
                <Input placeholder="http://example.com/image.png"/>
            </Form.Item>
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
                               message: 'Learning item should contain either definition or translation'
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
                                        // className="dynamic-delete-button"
                                        onClick={() => remove(name)}
                                    />
                                ) : null}
                                {/*<MinusCircleOutlined onClick={() => remove(name)}/>*/}
                            </Space>
                        ))}
                        <Form.Item>
                            <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                                Add field
                            </Button>
                        </Form.Item>
                        <Form.ErrorList errors={errors}/>
                    </>
                )}
            </Form.List>
            <Form.Item>
                <Space>
                    <Button type="primary" htmlType="submit">
                        Save
                    </Button>
                    <Button onClick={() => setShowAddLearningItemInput(false)} type="default">Cancel</Button>
                </Space>
            </Form.Item>
        </Form>
    </div>
}

export default AddLearningItemComponent;
