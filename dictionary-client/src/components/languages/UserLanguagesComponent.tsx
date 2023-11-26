import {settingsDictionaryClient} from "../../client/settings/settings-client";
import {useEffect, useState} from "react";
import {SupportedLanguage} from "../../client/settings/supported-language";
import {UserLanguage} from "../../client/languages/user-language";
import {useLanguagesClient} from "../../client/languages/languages-client";
import {LanguageSelectorComponent} from "./LanguageSelectorComponent";
import useCurrentLanguage from "../../context/CurrentLanguageContext";
import {DeleteOutlined} from "@ant-design/icons";

export function UserLanguagesComponent() {
    const languagesClient = useLanguagesClient();
    const currentLanguageContext = useCurrentLanguage();

    const [supportedLanguages, setSupportedLanguages] = useState<SupportedLanguage[]>([])
    const [languageSelectorHidden, setLanguageSelectorHidden] = useState(true)

    useEffect(() => {
        settingsDictionaryClient.getSupportedLanguages()
            .then(supportedLanguages => setSupportedLanguages(supportedLanguages.supportedLanguages))
    }, []);

    async function removeLanguage(userLanguage: UserLanguage) {
        const aggregatedUserLanguages = await languagesClient.removeLanguage(userLanguage.languageCode)
        currentLanguageContext.updateAllUserLanguages(aggregatedUserLanguages.languages)
    }

    async function addLanguage(selectedLanguage: SupportedLanguage) {
        const aggregatedUserLanguages = await languagesClient.addLanguage(selectedLanguage.languageCode)
        currentLanguageContext.updateAllUserLanguages(aggregatedUserLanguages.languages)
        setLanguageSelectorHidden(true)
    }

    return (
        <>
            <h2>Supported Languages:</h2>
            <ul>
                {supportedLanguages.map((supportedLanguage) => (
                    <li key={supportedLanguage.languageCode}>{supportedLanguage.languageName}</li>
                ))}
            </ul>

            <h2>User Languages:</h2>
            <ul>
                {currentLanguageContext.allUserLanguages.map((userLanguage) => (
                    <li key={userLanguage.languageCode}>
                        <span>{userLanguage.languageName}</span>
                        <button onClick={() => removeLanguage(userLanguage)}>
                            <DeleteOutlined />
                        </button>
                    </li>
                ))}
            </ul>
            {!currentLanguageContext.allUserLanguages.length && <div>You don't have any languages configured</div>}
            {languageSelectorHidden ? (
                <button onClick={() => setLanguageSelectorHidden(!languageSelectorHidden)}>
                    Add Language
                </button>
            ) : (
                <>
                    <button onClick={() => setLanguageSelectorHidden(!languageSelectorHidden)}>
                        Cancel adding new language
                    </button>
                    <LanguageSelectorComponent
                        languages={supportedLanguages}
                        onSelected={(selectedLanguage) => addLanguage(selectedLanguage)}
                    />
                </>
            )}
        </>
    )
}
