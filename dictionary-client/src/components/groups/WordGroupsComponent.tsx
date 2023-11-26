import {useGroupsClient} from "../../client/groups/groups-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import React, {useEffect, useState} from "react";
import {Group} from "../../client/groups/group";
import {AddGroupComponent} from "./AddGroupComponent";
import * as Icon from 'react-bootstrap-icons';
import {useNavigate} from "react-router";
import { Typography } from 'antd';

const { Title } = Typography;

export function WordGroupsComponent() {
    const navigate = useNavigate();
    const groupsClient = useGroupsClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;

    const [groups, setGroups] = useState<Group[]>([]);
    const [editingGroupId, setEditingGroupId] = useState<string>("");
    const [newGroupName, setNewGroupName] = useState<string>("");

    async function handleDeleteGroup(group: Group) {
        if (!currentLanguage) return;
        try {
            await groupsClient.deleteGroup(currentLanguage, group);
            setGroups(prevGroups => prevGroups.filter(g => g.id !== group.id));
        } catch (error) {
            console.error(`Error deleting group with id ${group.id}:`, error);
        }
    }

    function handleStartEditGroup(group: Group) {
        setEditingGroupId(group.id);
        setNewGroupName(group.name);
    }

    function handleCancelEditGroup() {
        setEditingGroupId("");
        setNewGroupName("");
    }

    async function handleSaveGroup(group: Group) {
        if (!newGroupName || !currentLanguage) {
            return;
        }

        try {
            const updatedGroup = {...group, name: newGroupName};
            await groupsClient.updateGroup(currentLanguage, updatedGroup);
            setGroups(prevGroups => prevGroups.map(g => {
                if (g.id === updatedGroup.id) {
                    return updatedGroup;
                }
                return g;
            }));
            setEditingGroupId("");
            setNewGroupName("");
        } catch (error) {
            console.error(`Error updating group with id ${group.id}:`, error);
        }
    }

    function handleInputChange(event: React.ChangeEvent<HTMLInputElement>) {
        setNewGroupName(event.target.value);
    }

    function isEditingGroup(groupId: string) {
        return editingGroupId === groupId;
    }

    useEffect(() => {
        if (!currentLanguage) return;
        groupsClient.getGroups(currentLanguage)
            .then((aggregatedWordGroups) => {
                setGroups(aggregatedWordGroups.groups);
            });
    }, [currentLanguage, groupsClient]);

    return (
        <div>
            <Title>Word Groups</Title>
            <AddGroupComponent onGroupAdded={createdGroup => setGroups([...groups, createdGroup])}/>
            <div>
                {groups.map(group => (
                    <div key={group.id}>
                        {isEditingGroup(group.id) ? (
                            <>
                                <input type="text" value={newGroupName} onChange={handleInputChange}/>
                                <button onClick={() => handleSaveGroup(group)}>Save</button>
                                <button onClick={handleCancelEditGroup}>Cancel</button>
                            </>
                        ) : (
                            <>
                                <span>{group.name}</span>
                                <button onClick={() => navigate(`/groups/${group.id}`)}><Icon.DoorOpen/></button>
                                <button onClick={() => handleStartEditGroup(group)}><Icon.Pencil/></button>
                                <button onClick={() => handleDeleteGroup(group)}><Icon.Trash/></button>
                            </>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
