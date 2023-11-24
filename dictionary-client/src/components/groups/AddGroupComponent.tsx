import React, {useState} from "react";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {useGroupsClient} from "../../client/groups/groups-client";
import {Group} from "../../client/groups/group";

interface AddGroupComponentProps {
    onGroupAdded: (group: Group) => void
}

export function AddGroupComponent(props: AddGroupComponentProps) {
    const groupsClient = useGroupsClient();
    const currentLanguageContext = useCurrentLanguage();
    const currentLanguage = currentLanguageContext.currentLanguage;

    const [newGroupName, setNewGroupName] = useState("");
    const [showAddGroup, setShowAddGroup] = useState(false);

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewGroupName(event.target.value);
    };

    function handleKeyDown(event: React.KeyboardEvent<HTMLInputElement>) {
        if (event.key === "Enter") {
            handleAddGroup();
        } else if (event.key === "Escape") {
            clearGroupNameInput();
        }
    }

    function clearGroupNameInput() {
        setNewGroupName("");
        setShowAddGroup(false);
    }

    function handleAddGroup() {
        if (!newGroupName || !currentLanguage) {
            return;
        }

        groupsClient.createGroup(currentLanguage, {name: newGroupName})
            .then(newGroup => {
                props.onGroupAdded(newGroup);
                clearGroupNameInput();
            }).catch(error => console.error(`Error creating group with name ${newGroupName}:`, error));
    }

    return (
        <div>
            <button onClick={() => setShowAddGroup(!showAddGroup)}>Add Group</button>
            {showAddGroup && (
                <div>
                    <input type="text" value={newGroupName} onChange={handleInputChange} onKeyDown={handleKeyDown}/>
                    <button onClick={handleAddGroup}>Add</button>
                    <button onClick={clearGroupNameInput}>Cancel</button>
                </div>
            )}
        </div>
    );
}
