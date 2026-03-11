import React, {useEffect, useState} from 'react';
import './FavoritesPage.css';
import {useNavigate} from 'react-router-dom';
import {useAuth} from '../security/AuthContext';
import {getCsrfToken} from '../utils/CsrfUtils';
import {formatDuration, formatIngredient} from "../utils/RecipeFormatUtils";

const FavoritesPage = () => {
    const [favoriteRecipes, setFavoriteRecipes] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const {username, logout} = useAuth();

    const FAVORITES_URL = 'http://localhost:8082/api/v1/account/favorite';
    const LOGOUT_URL = 'http://localhost:8082/api/v1/logout';
    const PAGE_SIZE = 15;

    useEffect(() => {
        loadFavorites();
    }, [currentPage]);

    const loadFavorites = async () => {
        try {
            setLoading(true);
            const csrfToken = await getCsrfToken();

            const response = await fetch(
                `${FAVORITES_URL}?page=${currentPage}&size=${PAGE_SIZE}`, {
                    method: 'GET',
                    credentials: 'include',
                    headers: {
                        'X-XSRF-TOKEN': csrfToken.token
                    }
                });

            if (response.ok) {
                const recipes = await response.json();
                setFavoriteRecipes(recipes);
                setTotalPages(Math.ceil(recipes.length / PAGE_SIZE));
            } else {
                setMessage('Ошибка загрузки избранного');
            }
        } catch (error) {
            setMessage('Сетевая ошибка: ' + error.message);
        } finally {
            setLoading(false);
        }
    };

    const handleRemoveFromFavorites = async (recipeName) => {
        try {
            const csrfToken = await getCsrfToken();

            const response = await fetch(
                `${FAVORITES_URL}?recipe_name=${encodeURIComponent(recipeName)}`, {
                    method: 'DELETE',
                    credentials: 'include',
                    headers: {
                        'X-XSRF-TOKEN': csrfToken.token
                    }
                });

            if (response.ok) {
                setMessage(`Рецепт "${recipeName}" удален из избранного`);
                loadFavorites();
                setTimeout(() => setMessage(''), 3000);
            } else {
                setMessage('Ошибка при удалении рецепта из избранного');
            }
        } catch (error) {
            setMessage('Сетевая ошибка: ' + error.message);
        }
    };

    const handleLogout = async () => {
        try {
            const csrfToken = await getCsrfToken();
            await fetch(LOGOUT_URL, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'X-XSRF-TOKEN': csrfToken.token
                }
            });
            logout();
            navigate('/login');
        } catch (error) {
            console.error('Ошибка выхода:', error);
        }
    };

    const handleBackToSearch = () => {
        navigate("/recipe-aggregator");
    };

    const handlePageChange = (newPage) => {
        setCurrentPage(newPage);
    };

    return (
        <div className="favorites-container">
            <div className="header">
                <h1>Избранные рецепты</h1>
                <div className="user-panel">
                    <span className="username">Добро пожаловать, {username}!</span>
                    <button className="button-back" onClick={handleBackToSearch}>
                        Назад к поиску
                    </button>
                    <button className="button-logout" onClick={handleLogout}>
                        Выйти
                    </button>
                </div>
            </div>

            <div className="favorites-content">
                <h2>Мои избранные рецепты</h2>

                {message && <p className="message">{message}</p>}

                {loading ? (
                    <div className="loading">Загрузка избранного...</div>
                ) : favoriteRecipes.length === 0 ? (
                    <div className="no-favorites">
                        <p>У вас пока нет избранных рецептов.</p>
                        <button className="button-back" onClick={handleBackToSearch}>
                            Найти рецепты
                        </button>
                    </div>
                ) : (
                    <>
                        <div className="recipes-grid">
                            {favoriteRecipes.map((recipe) => (
                                <div key={recipe.id} className="recipe-card">
                                    <div className="recipe-header">
                                        <h4>{recipe.name}</h4>
                                        <button
                                            className="button-remove-favorite"
                                            onClick={() => handleRemoveFromFavorites(recipe.name)}
                                        >
                                            Удалить
                                        </button>
                                    </div>

                                    {recipe.description && (
                                        <p className="recipe-description">{recipe.description}</p>
                                    )}

                                    <div className="recipe-times">
                                        <div className="time-item">
                                            <span className="time-label">Подготовка:</span>
                                            <span
                                                className="time-value">{formatDuration(recipe.timeForPreparing)}</span>
                                        </div>
                                        <div className="time-item">
                                            <span className="time-label">Готовка:</span>
                                            <span className="time-value">{formatDuration(recipe.timeForCooking)}</span>
                                        </div>
                                        <div className="time-item total">
                                            <span className="time-label">Общее время:</span>
                                            <span className="time-value">{formatDuration(recipe.totalTime)}</span>
                                        </div>
                                    </div>

                                    {recipe.servings > 0 && (
                                        <div className="recipe-servings">
                                            <strong>Порции:</strong> {recipe.servings}
                                        </div>
                                    )}

                                    {recipe.ingredients && recipe.ingredients.length > 0 && (
                                        <div className="recipe-ingredients">
                                            <strong>Ингредиенты:</strong>
                                            <ul>
                                                {recipe.ingredients.map((ingredient, index) => (
                                                    <li key={index}>{formatIngredient(ingredient)}</li>
                                                ))}
                                            </ul>
                                        </div>
                                    )}

                                    {recipe.direction && (
                                        <div className="recipe-direction">
                                            <strong>Инструкция:</strong>
                                            <p>{recipe.direction}</p>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>

                        {totalPages > 1 && (
                            <div className="pagination">
                                <button
                                    className="pagination-button"
                                    disabled={currentPage === 0}
                                    onClick={() => handlePageChange(currentPage - 1)}
                                >
                                    Назад
                                </button>

                                <span className="pagination-info">
                                    Страница {currentPage + 1} из {totalPages}
                                </span>

                                <button
                                    className="pagination-button"
                                    disabled={currentPage >= totalPages - 1}
                                    onClick={() => handlePageChange(currentPage + 1)}
                                >
                                    Вперед
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
};

export default FavoritesPage;