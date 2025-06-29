import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './context/PrivateRoute';

import LoginPage from './views/pages/LoginPage';
import Dashboard from './views/pages/Dashboard';
// import UsersPage from './views/pages/UsersPage';
// import QuestsPage from './views/pages/QuestsPage';
// import DungeonsPage from './views/pages/DungeonsPage';
// import WorkoutsPage from './views/pages/WorkoutsPage';

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />

                    <Route element={<PrivateRoute />}>
                        <Route path="/dashboard" element={<Dashboard />} />
                        {/*<Route path="/users" element={<UsersPage />} />*/}
                        {/*<Route path="/quests" element={<QuestsPage />} />*/}
                        {/*<Route path="/dungeons" element={<DungeonsPage />} />*/}
                        {/*<Route path="/workouts" element={<WorkoutsPage />} />*/}
                    </Route>

                    <Route path="*" element={<LoginPage />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;
