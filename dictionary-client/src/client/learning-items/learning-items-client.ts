import jwtDecode, {JwtPayload} from "jwt-decode";
import {useMemo} from "react";
import {RestClient} from "../rest-client";
import {UserLanguage} from "../languages/user-language";
import {UnsavedLearningItem} from "../model/unsaved-learning-item";
import {LearningItem} from "../model/learning-item";
import {LearningItemList} from "../model/learning-item-list";
import {getToken} from "../../components/auth/authentication-helpers";
import {UserDetails} from "../model/user-details";

export function useDictionaryClient() {
    return useMemo(() => new LearningItemsClient(new RestClient()), [])
}

class LearningItemsClient {
    private restClient: RestClient
    private readonly _dictionaryHost: string;

    constructor(restClient: RestClient) {
        this.restClient = restClient
        this._dictionaryHost = process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST!;
    }


    async addLearningItem(userLanguage: UserLanguage, learningItem: UnsavedLearningItem): Promise<LearningItem> {
        const token = this.extractJwtToken();
        const addedLearningItem = await this.restClient.post(`${this._dictionaryHost}/api/languages/${userLanguage.languageCode}/learning-items`,
            token, learningItem)
        console.log('Received response on adding the learningItem', addedLearningItem)
        return addedLearningItem
    }

    async updateLearningItem(userLanguage: UserLanguage, learningItem: LearningItem): Promise<LearningItem> {
        const token = this.extractJwtToken();
        const editedLearningItem = await this.restClient.patch(`${this._dictionaryHost}/api/languages/${userLanguage.languageCode}/learning-items/${learningItem.id}`,
            token, learningItem)
        console.log('Received response on editing the learningItem', editedLearningItem)
        return editedLearningItem
    }

    async getLearningItems(userLanguage: UserLanguage): Promise<LearningItemList> {
        const token = getToken();
        const url = `${this._dictionaryHost}/api/languages/${userLanguage.languageCode}/learning-items`
        console.debug('Getting user learningItems', url)
        const userLearningItems = await this.restClient.get(url, token)
        console.log('Received response on user learningItems', userLearningItems)
        return userLearningItems
    }

    async getLearningItem(userLanguage: UserLanguage, learningItemId: string): Promise<LearningItem> {
        const token = getToken();
        const url = `${this._dictionaryHost}/api/languages/${userLanguage.languageCode}/learning-items/${learningItemId}`
        console.debug('Getting user learningItem', url)
        const learningItem = await this.restClient.get(url, token)
        console.log('Received response on user learningItem', learningItem)
        return learningItem
    }

    async deleteLearningItem(userLanguage: UserLanguage, learningItem: LearningItem): Promise<LearningItemList> {
        const token = this.extractJwtToken();
        const url = `${this._dictionaryHost}/api/languages/${userLanguage.languageCode}/learning-items/${learningItem.id}`
        console.debug('Deleting learningItem', url)
        const userLearningItems = await this.restClient.delete<LearningItemList>(url, token)
        console.log('Received response on deleting the learningItem', userLearningItems)
        return userLearningItems
    }

    getUserDetails(): UserDetails {
        const token = getToken();
        const decodedToken = jwtDecode<JwtPayload>(token)
        const userId = decodedToken.sub
        if (!userId) {
            throw new Error('No user id found in token')
        }
        return {userId: userId}
    }

    private extractJwtToken(): string {
        const token = localStorage.getItem('token')
        if (!token) {
            throw new Error('No token found')
        }
        return token
    }
}
