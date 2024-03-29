import {UserLanguage} from "../../client/languages/user-language"
import useCurrentLanguage from "../../context/CurrentLanguageContext"
import useAuth from "../auth/authentication-helpers"

export function CurrentLanguageSelectionComponent() {
    const currentLanguageContext = useCurrentLanguage()
    const auth = useAuth()

    if (!currentLanguageContext.currentLanguage || !auth.isAuthenticated()) {
        return <></>
    }

    const currentLanguage = currentLanguageContext.currentLanguage!

    const userLanguages = [
        currentLanguage,
        ...currentLanguageContext.allUserLanguages
            .filter(language => language.languageCode !== currentLanguage.languageCode)
    ]
    const languagesMapping = userLanguages.reduce(
        (acc: Record<string, UserLanguage>, lang: UserLanguage) => {
            acc[lang.languageCode] = lang
            return acc
        },
        {}
    )
    return (
        <select onChange={event => currentLanguageContext.updateCurrentLanguage(languagesMapping[event.target.value])}>
            {userLanguages.map((userLanguage) => (
                <option key={userLanguage.languageCode} value={userLanguage.languageCode}>
                    {userLanguage.languageName}
                </option>
            ))}
        </select>
    )
}
