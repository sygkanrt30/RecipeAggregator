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
                // Предполагаем, что бэкенд возвращает информацию о пагинации
                // Если нет, можно рассчитать на фронтенде
                setTotalPages(Math.ceil(recipes.length / PAGE_SIZE));
            } else {
                setMessage('Error loading favorites');
            }
        } catch (error) {
            setMessage('Network error: ' + error.message);
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
                setMessage(`Recipe "${recipeName}" removed from favorites`);
                loadFavorites();
                setTimeout(() => setMessage(''), 3000);
            } else {
                setMessage('Error removing recipe from favorites');
            }
        } catch (error) {
            setMessage('Network error: ' + error.message);
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
            console.error('Logout error:', error);
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
            {/* Header */}
            <div className="header">
                <h1>Favorite Recipes</h1>
                <div className="user-panel">
                    <span className="username">Welcome, {username}!</span>
                    <button className="button-back" onClick={handleBackToSearch}>
                        Back to Search
                    </button>
                    <button className="button-logout" onClick={handleLogout}>
                        Logout
                    </button>
                </div>
            </div>

            {/* Main Content */}
            <div className="favorites-content">
                <h2>My Favorite Recipes</h2>

                {message && <p className="message">{message}</p>}

                {loading ? (
                    <div className="loading">Loading favorites...</div>
                ) : favoriteRecipes.length === 0 ? (
                    <div className="no-favorites">
                        <p>You don't have any favorite recipes yet.</p>
                        <button className="button-back" onClick={handleBackToSearch}>
                            Find Recipes
                        </button>
                    </div>
                ) : (
                    <>
                        {/* Recipes Grid */}
                        <div className="recipes-grid">
                            {favoriteRecipes.map((recipe) => (
                                <div key={recipe.id} className="recipe-card">
                                    <div className="recipe-header">
                                        <h4>{recipe.name}</h4>
                                        <button
                                            className="button-remove-favorite"
                                            onClick={() => handleRemoveFromFavorites(recipe.name)}
                                        >
                                            Remove
                                        </button>
                                    </div>

                                    {recipe.description && (
                                        <p className="recipe-description">{recipe.description}</p>
                                    )}

                                    <div className="recipe-times">
                                        <div className="time-item">
                                            <span className="time-label">Preparation:</span>
                                            <span
                                                className="time-value">{formatDuration(recipe.timeForPreparing)}</span>
                                        </div>
                                        <div className="time-item">
                                            <span className="time-label">Cooking:</span>
                                            <span className="time-value">{formatDuration(recipe.timeForCooking)}</span>
                                        </div>
                                        <div className="time-item">
                                            <span className="time-label">Additional:</span>
                                            <span className="time-value">{formatDuration(recipe.additionalTime)}</span>
                                        </div>
                                        <div className="time-item total">
                                            <span className="time-label">Total time:</span>
                                            <span className="time-value">{formatDuration(recipe.totalTime)}</span>
                                        </div>
                                    </div>

                                    {recipe.servings > 0 && (
                                        <div className="recipe-servings">
                                            <strong>Servings:</strong> {recipe.servings}
                                        </div>
                                    )}

                                    {recipe.ingredients && recipe.ingredients.length > 0 && (
                                        <div className="recipe-ingredients">
                                            <strong>Ingredients:</strong>
                                            <ul>
                                                {recipe.ingredients.map((ingredient, index) => (
                                                    <li key={index}>{formatIngredient(ingredient)}</li>
                                                ))}
                                            </ul>
                                        </div>
                                    )}

                                    {recipe.direction && (
                                        <div className="recipe-direction">
                                            <strong>Directions:</strong>
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
                                    Previous
                                </button>

                                <span className="pagination-info">
                                    Page {currentPage + 1} of {totalPages}
                                </span>

                                <button
                                    className="pagination-button"
                                    disabled={currentPage >= totalPages - 1}
                                    onClick={() => handlePageChange(currentPage + 1)}
                                >
                                    Next
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