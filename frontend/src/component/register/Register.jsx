import React, {useState} from 'react';
import './Register.css';
import {useNavigate} from 'react-router-dom';
import {useAuth} from "../security/AuthContext";
import {getCsrfToken} from '../utils/CsrfUtils';

const Register = () => {
    const [username, setUsername] = useState('');
    const REGISTER_URL = 'http://localhost:8082/api/v1/auth/reg';
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const {login} = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const userData = {
            username,
            email,
            password,
        };

        try {
            const csrfToken = await getCsrfToken();

            const response = await fetch(REGISTER_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken.token
                },
                credentials: 'include',
                body: JSON.stringify(userData),
            });

            if (response.ok) {
                login(username);
                navigate("/recipe-aggregator");
            } else {
                const errorData = await response.text();
                setMessage('Ошибка регистрации: ' + errorData);
                console.log('Registration error:', errorData);
            }
        } catch (error) {
            setMessage('Сетевая ошибка: ' + error.message);
            console.log('Network error:', error.message);
        }
    };

    const handleAuth = () => {
        navigate("/login");
    };

    return (
        <div className="container">
            <h2>Регистрация</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Имя пользователя:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
                <div>
                    <label>Пароль:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <button type="submit" className="button-submit">Зарегистрироваться</button>
                <button type="button" className="button-enter" onClick={handleAuth}>Войти</button>
            </form>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default Register;