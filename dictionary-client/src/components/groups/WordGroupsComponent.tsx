import {useGroupsClient} from "../../client/groups/groups-client";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import React, {useEffect, useState} from "react";
import {WordsGroup} from "../../client/groups/words-group";
import {UnsavedWordsGroup} from "../../client/groups/unsaved-words-group";

export function WordGroupsComponent() {
    const groupsClient = useGroupsClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;

    const [groups, setGroups] = useState<WordsGroup[]>([]);
    const [editingGroupId, setEditingGroupId] = useState<string>('');
    const [newGroupName, setNewGroupName] = useState<string>('');
    const [showAddGroup, setShowAddGroup] = useState<boolean>(false);

    const handleDeleteGroup = async (group: WordsGroup) => {
        if (!currentLanguage) return;
        try {
            await groupsClient.deleteGroup(currentLanguage, group);
            setGroups(prevGroups => prevGroups.filter(g => g.id !== group.id));
        } catch (error) {
            console.error(`Error deleting group with id ${group.id}:`, error);
        }
    };

    const handleStartEditGroup = (group: WordsGroup) => {
        setEditingGroupId(group.id);
        setNewGroupName(group.name);
    };

    const handleCancelEditGroup = () => {
        setEditingGroupId('');
        setNewGroupName('');
    };

    const handleSaveGroup = async (group: WordsGroup) => {
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
            setEditingGroupId('');
            setNewGroupName('');
        } catch (error) {
            console.error(`Error updating group with id ${group.id}:`, error);
        }
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewGroupName(event.target.value);
    };

    const isEditingGroup = (groupId: string) => editingGroupId === groupId;

    useEffect(() => {
        if (!currentLanguage) return;
        groupsClient.getGroups(currentLanguage)
            .then((aggregatedWordGroups) => {
                setGroups(aggregatedWordGroups.groups);
            });
    }, [currentLanguage, groupsClient]);

    const handleAddGroup = async () => {
        if (!currentLanguage || !newGroupName) {
            return;
        }

        try {
            const newGroup: UnsavedWordsGroup = {name: newGroupName};
            const createdGroup = await groupsClient.createGroup(currentLanguage, newGroup);
            setGroups(prevGroups => [...prevGroups, createdGroup]);
            setShowAddGroup(false);
            setNewGroupName('');
        } catch (error) {
            console.error(`Error creating new group:`, error);
        }
    };

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleAddGroup();
        } else if (event.key === 'Escape') {
            setShowAddGroup(false);
            setNewGroupName('');
        }
    };

    return (
        <div>
            <h1>Word Groups</h1>
            <div>
                <button onClick={() => setShowAddGroup(true)}>Add new group</button>
                {showAddGroup && (
                    <>
                        <input type="text" value={newGroupName} onChange={handleInputChange}
                               onKeyPress={handleKeyPress}/>
                        <button onClick={handleAddGroup}>Save</button>
                        <button onClick={() => setShowAddGroup(false)}>Cancel</button>
                    </>
                )}
            </div>
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
                                <button onClick={() => handleStartEditGroup(group)}>Edit</button>
                                <button onClick={() => handleDeleteGroup(group)}>Delete</button>
                            </>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
