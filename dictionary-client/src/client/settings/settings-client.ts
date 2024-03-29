import {RestClient} from "../rest-client";
import {getToken} from "../../components/auth/authentication-helpers";
import {SupportedLanguagesSettings} from "./supported-languages-settings";

class SettingsDictionaryClient {
    private restClient: RestClient;

    constructor(restClient: RestClient) {
        this.restClient = restClient
    }

    async getSupportedLanguages(): Promise<SupportedLanguagesSettings> {
        const token = getToken();
        const supportedLanguages = await this.restClient.get(`${process.env.REACT_APP_DICTIONARY_SERVICE_API_HOST}/api/settings/languages`, token);
        console.log('Received response on getting supported languages', supportedLanguages)
        return supportedLanguages;
    }
}

export const settingsDictionaryClient = new SettingsDictionaryClient(new RestClient());
