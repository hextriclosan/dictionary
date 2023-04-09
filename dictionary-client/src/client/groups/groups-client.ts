import {RestClient} from "../rest-client";
import {UserLanguage} from "../languages/user-language";
import {AggregatedWordGroups} from "./aggregated-word-groups";
import {UnsavedWordsGroup} from "./unsaved-words-group";
import {useMemo} from "react";
import {WordsGroup} from "./words-group";

export function useGroupsClient(): GroupsClient {
    return useMemo(() => new GroupsClient(new RestClient()), []);
}

class GroupsClient {

    private readonly restClient: RestClient;

    constructor(restClient: RestClient) {
        this.restClient = restClient;
    }

    async getGroups(userLanguage: UserLanguage): Promise<AggregatedWordGroups> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.get(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups`, token);
        console.log('Received response on getting user groups', userGroups)
        return userGroups;
    }

    async createGroup(userLanguage: UserLanguage, unsavedWordsGroup: UnsavedWordsGroup): Promise<WordsGroup> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.post(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups`, token, unsavedWordsGroup);
        console.log('Received response on adding user group', userGroups)
        return userGroups;
    }

    async deleteGroup(userLanguage: UserLanguage, wordsGroup: WordsGroup): Promise<AggregatedWordGroups> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.delete(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups/${wordsGroup.id}`, token);
        console.log('Received response on removing user group', userGroups)
        return userGroups;
    }

    async updateGroup(userLanguage: UserLanguage, wordsGroup: WordsGroup): Promise<WordsGroup> {
        const token = this.extractJwtToken();
        const userGroups = await this.restClient.patch(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/languages/${userLanguage.languageCode}/groups/${wordsGroup.id}`, token, wordsGroup);
        console.log('Received response on updating user group', userGroups)
        return userGroups;
    }

    private extractJwtToken(): string {
        const token = localStorage.getItem('token')
        if (!token) {
            throw new Error('No token found')
        }
        return token
    }
}
