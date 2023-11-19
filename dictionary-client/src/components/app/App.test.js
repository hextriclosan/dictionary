import {render, screen} from '@testing-library/react';
import App from './App';
import {BrowserRouter} from "react-router-dom";
import {AuthProvider} from "../auth/authentication-helpers";

test('renders dictionary header', () => {
    render(
        <BrowserRouter>
            <AuthProvider>
                <App/>
            </AuthProvider>
        </BrowserRouter>
    );
    const headerElement = screen.getByText(/Dictionary/i);
    expect(headerElement).toBeInTheDocument();
});
