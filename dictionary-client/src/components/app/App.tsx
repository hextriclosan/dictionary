import {Link, Outlet} from "react-router-dom";
import {useNavigate} from "react-router";
import useAuth from "../auth/authentication-helpers";
import {CurrentLanguageSelectionComponent} from "../languages/CurrentLanguageSelectionComponent";
import routing from "../../constants/routing";
import {ConfigProvider} from "antd";
import { Typography } from 'antd';

const { Title } = Typography;

function App() {
    const navigate = useNavigate();
    const auth = useAuth();

    if (!auth.isAuthenticated()) {
        return (
            <div className="app">
                <h1>Dictionary</h1>
                <nav style={{
                    borderBottom: "solid 1px",
                    paddingBottom: "1rem",
                }}>
                    <Link to={routing.HOME}>Home</Link> | {" "}
                    <Link to="/about">About</Link> | {" "}
                    <Link to={routing.LOGIN}>Login</Link>
                </nav>
                <Outlet/>
            </div>
        );
    }

    return (
        <ConfigProvider>
            <div className="app">
                <Title>Dictionary</Title>
                <nav style={{
                    borderBottom: "solid 1px",
                    paddingBottom: "1rem",
                }}>
                    <Link to={routing.HOME}>Home</Link> | {" "}
                    <Link to="/learning-items">Learning Items</Link> | {" "}
                    <Link to="/groups">Groups</Link> | {" "}
                    <Link to="/languages">Languages</Link> | {" "}
                    <Link to="/profile">Profile</Link> | {" "}
                    <Link to="/about">About</Link> | {" "}
                    <Link to="/" onClick={() => auth.signout(() => navigate("/"))}>Logout</Link> | {" "}
                    <CurrentLanguageSelectionComponent/>
                </nav>
                <Outlet/>
            </div>
        </ConfigProvider>
    );
}

export default App;
