import {RestClient} from "../rest-client";
import {UserLanguage} from "../languages/user-language";
import {GroupList} from "./group-list";
import {UnsavedGroup} from "./unsaved-group";
import {useMemo} from "react";
import {Group} from "./group";

export function useGroupsClient(): GroupsClient {
    return useMemo(() => new GroupsClient(new RestClient()), []);
}

class GroupsClient {

    private readonly restClient: RestClient;

    constructor(restClient: RestClient) {
        this.restClient = restClient;
    }

    async getGroups(userLanguage: UserLanguage): Promise<GroupList> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.get(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups`, token);
        console.log('Received response on getting user groups', userGroups)
        return userGroups;
    }

    async getGroup(userLanguage: UserLanguage, groupId: string): Promise<Group> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.get(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups/${groupId}`, token);
        console.log('Received response on getting user group', userGroups)
        return userGroups;
    }

    async createGroup(userLanguage: UserLanguage, unsavedGroup: UnsavedGroup): Promise<Group> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.post(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups`, token, unsavedGroup);
        console.log('Received response on adding user group', userGroups)
        return userGroups;
    }

    async deleteGroup(userLanguage: UserLanguage, group: Group): Promise<GroupList> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.delete<GroupList>(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups/${group.id}`, token);
        console.log('Received response on removing user group', userGroups)
        return userGroups;
    }

    async updateGroup(userLanguage: UserLanguage, group: Group): Promise<Group> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.patch(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups/${group.id}`, token, group);
        console.log('Received response on updating user group', userGroups)
        return userGroups;
    }

    async addLearningItemToGroup(userLanguage: UserLanguage, groupId: string, learningItemId: string): Promise<void> {
        const token = this.extractJwtToken();
        await this.restClient.put(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups/${groupId}/learning-items/${learningItemId}`, token);
        console.log(`Received response on adding learning item to user group [groupId=${groupId}, learningItemId=${learningItemId}]`, groupId, learningItemId);
    }

    async removeLearningItemFromGroup(userLanguage: UserLanguage, groupId: string, learningItemId: string): Promise<void> {
        const token = this.extractJwtToken();
        await this.restClient.delete(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups/${groupId}/learning-items/${learningItemId}`, token);
        console.log(`Received response on removing learning item from user group [groupId=${groupId}, learningItemId=${learningItemId}]`, groupId, learningItemId);
    }

    private extractJwtToken(): string {
        const token = localStorage.getItem('token')
        if (!token) {
            throw new Error('No token found')
        }
        return token
    }
}
