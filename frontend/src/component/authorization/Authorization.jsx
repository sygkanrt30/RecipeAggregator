import React, {useState} from 'react';
import './Authorization.css';
import {useNavigate} from 'react-router-dom';
import {useAuth} from '../security/AuthContext';
import {getCsrfToken} from '../utils/CsrfUtils';

const Authorization = () => {
    const [username, setUsername] = useState('');
    const LOGIN_URL = 'http://localhost:8082/api/v1/auth/login';
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const {login} = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const csrfToken = await getCsrfToken();

            const response = await fetch(LOGIN_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken.token
                },
                credentials: 'include',
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            if (response.ok) {
                login(username);
                navigate("/recipe-aggregator");
            } else {
                const errorText = await response.text();
                setMessage('Ошибка входа: ' + errorText);
                console.log('Login error:', errorText);
            }
        } catch (error) {
            setMessage('Сетевая ошибка: ' + error.message);
            console.log('Network error:', error.message);
        }
    };

    const handleRegister = () => {
        navigate("/signup");
    };

    return (
        <div className="container">
            <h2>Вход</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Имя пользователя:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Пароль:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" className="button-submit">Войти</button>
                <button type="button" className="button-register" onClick={handleRegister}>Зарегистрироваться</button>
            </form>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default Authorization;