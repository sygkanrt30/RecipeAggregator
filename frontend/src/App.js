import Register from "./component/register/Register";
import Authorization from "./component/authorization/Authorization"
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import {AuthProvider} from './component/security/AuthContext';
import MainPage from "./component/main_page/MainPage";
import ProtectedRoute from "./component/security/ProtectedRoute";
import FavoritesPage from "./component/favorites_page/FavoritesPage";


function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<Navigate to="/login" replace/>}/>
                    <Route path="/login" element={<Authorization/>}/>
                    <Route path="/signup" element={<Register/>}/>
                    <Route path="/recipe-aggregator" element={<ProtectedRoute><MainPage/></ProtectedRoute>}/>
                    <Route path="/favorites" element={<ProtectedRoute><FavoritesPage/></ProtectedRoute>}/>
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;