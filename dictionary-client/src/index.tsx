import ReactDOM from "react-dom/client";
import React from "react";
import {Route, Routes} from "react-router";
import {BrowserRouter} from "react-router-dom";
import App from "./components/app/App";
import reportWebVitals from "./reportWebVitals";
import Home from "./components/home/Home";
import Login from "./components/auth/login/LoginComponent";
import AuthorizationComponent from "./components/auth/login/AuthorizationComponent";
import ProfileComponent from "./components/profile/ProfileComponent";
import RequireAuth from "./components/auth/require-auth";
import {AuthProvider} from "./components/auth/authentication-helpers";
import {UserLanguagesComponent} from "./components/languages/UserLanguagesComponent";
import {CurrentLanguageProvider} from "./context/CurrentLanguageContext";
import {GroupsComponent} from "./components/groups/GroupsComponent";
import {SingleGroupComponent} from "./components/groups/SingleGroupComponent";
import LearningItemsComponent from "./components/learning-items/LearningItemsComponent";

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(
    <React.StrictMode>
        <BrowserRouter>
            <AuthProvider>
                <CurrentLanguageProvider>
                    <Routes>
                        <Route path="/" element={<App/>}>
                            <Route path="/" element={<Home/>}/>
                            <Route path="/login" element={<Login/>}/>
                            <Route path="/authorized" element={<AuthorizationComponent/>}/>
                            <Route path="/profile" element={
                                <RequireAuth>
                                    <ProfileComponent/>
                                </RequireAuth>
                            }/>
                            <Route path="/learning-items" element={
                                <RequireAuth>
                                    <LearningItemsComponent/>
                                </RequireAuth>
                            }/>
                            <Route path="/groups" element={
                                <RequireAuth>
                                    <GroupsComponent/>
                                </RequireAuth>
                            }/>
                            <Route path="/groups/:groupId" element={
                                <RequireAuth>
                                    <SingleGroupComponent/>
                                </RequireAuth>
                            }/>
                            <Route path="/languages" element={
                                <RequireAuth>
                                    <UserLanguagesComponent/>
                                </RequireAuth>
                            }/>
                            <Route
                                path="*"
                                element={
                                    <main style={{padding: "1rem"}}>
                                        <p>No such page exist</p>
                                    </main>
                                }
                            />
                        </Route>
                    </Routes>
                </CurrentLanguageProvider>
            </AuthProvider>
        </BrowserRouter>
    </React.StrictMode>
);

reportWebVitals();
