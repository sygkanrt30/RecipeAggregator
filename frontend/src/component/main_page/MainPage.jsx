import React, {useState} from 'react';
import './MainPage.css';
import {useNavigate} from 'react-router-dom';
import {useAuth} from '../security/AuthContext';
import {getCsrfToken} from '../utils/CsrfUtils';
import {formatDuration, formatIngredient} from "../utils/RecipeFormatUtils";

const MainPage = () => {
    const [searchType, setSearchType] = useState('name');
    const [name, setName] = useState('');
    const [ingredients, setIngredients] = useState('');
    const [searchRequest, setSearchRequest] = useState({
        name: '',
        ingredientNames: new Set(),
        cookingTime: 0,
        cookingTimeOperator: 'EQ',
        totalTime: 0,
        totalTimeOperator: 'EQ',
        preparationTime: 0,
        preparationTimeOperator: 'EQ',
        servings: 0,
        servingsOperator: 'EQ'
    });
    const [recipes, setRecipes] = useState([]);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const {username, logout} = useAuth();

    const LOGOUT_URL = 'http://localhost:8082/logout';
    const SEARCH_BY_NAME_URL = 'http://localhost:8082/api/v1/search/name';
    const SEARCH_BY_INGREDIENTS_URL = 'http://localhost:8082/api/v1/search/ingredients';
    const SEARCH_WITH_FILTERING_URL = 'http://localhost:8082/api/v1/search/with-filtering';
    const ADD_TO_FAVORITES_URL = 'http://localhost:8082/api/v1/account/favorite';

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

    const handleSearch = async (e) => {
        e.preventDefault();
        setMessage('');
        setRecipes([]);

        try {
            const csrfToken = await getCsrfToken();

            let url = '';
            let options = {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken.token
                }
            };

            switch (searchType) {
                case 'name':
                    url = `${SEARCH_BY_NAME_URL}/${encodeURIComponent(name)}`;
                    break;
                case 'ingredients':
                    const ingredientSet = new Set(ingredients.split(',').map(i => i.trim()).filter(i => i));
                    url = SEARCH_BY_INGREDIENTS_URL;
                    options.method = 'POST';
                    options.body = JSON.stringify(Array.from(ingredientSet));
                    break;
                case 'filtering':
                    const request = {
                        ...searchRequest,
                        ingredientNames: Array.from(searchRequest.ingredientNames)
                    };
                    url = SEARCH_WITH_FILTERING_URL;
                    options.method = 'POST';
                    options.body = JSON.stringify(request);
                    break;
                default:
                    return;
            }

            const response = await fetch(url, options);

            if (response.ok) {
                const result = await response.json();
                setRecipes(result);
                if (result.length === 0) {
                    setMessage('Рецепты не найдены');
                }
            } else {
                setMessage('Ошибка при поиске рецептов');
            }
        } catch (error) {
            setMessage('Сетевая ошибка: ' + error.message);
        }
    };

    const handleIngredientChange = (e) => {
        const ingredientsArray = e.target.value.split(',').map(i => i.trim()).filter(i => i);
        setSearchRequest(prev => ({
            ...prev,
            ingredientNames: new Set(ingredientsArray)
        }));
    };

    const handleFavorites = () => {
        navigate("/favorites");
    };

    const handleAddToFavorites = async (recipeName) => {
        try {
            const csrfToken = await getCsrfToken();

            const response = await fetch(`${ADD_TO_FAVORITES_URL}?recipe_name=${encodeURIComponent(recipeName)}`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'X-XSRF-TOKEN': csrfToken.token
                }
            });

            if (response.ok) {
                setMessage(`Рецепт "${recipeName}" добавлен в избранное!`);
            } else {
                setMessage('Ошибка при добавлении рецепта в избранное');
            }
        } catch (error) {
            setMessage('Сетевая ошибка: ' + error.message);
        }
    };

    return (
        <div className="main-container">
            <div className="header">
                <h1>Агрегатор рецептов</h1>
                <div className="user-panel">
                    <span className="username">Добро пожаловать, {username}!</span>
                    <button className="button-favorites" onClick={handleFavorites}>
                        Избранное
                    </button>
                    <button className="button-logout" onClick={handleLogout}>
                        Выйти
                    </button>
                </div>
            </div>

            <div className="search-container">
                <h2>Поиск рецептов</h2>

                <div className="search-type-selector">
                    <button
                        className={`type-button ${searchType === 'name' ? 'active' : ''}`}
                        onClick={() => setSearchType('name')}
                    >
                        По названию
                    </button>
                    <button
                        className={`type-button ${searchType === 'ingredients' ? 'active' : ''}`}
                        onClick={() => setSearchType('ingredients')}
                    >
                        По ингредиентам
                    </button>
                    <button
                        className={`type-button ${searchType === 'filtering' ? 'active' : ''}`}
                        onClick={() => setSearchType('filtering')}
                    >
                        Расширенный поиск
                    </button>
                </div>

                <form onSubmit={handleSearch} className="search-form">
                    {searchType === 'name' && (
                        <div className="form-group">
                            <label>Название рецепта:</label>
                            <input
                                type="text"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder="Введите название рецепта"
                                required
                            />
                        </div>
                    )}

                    {searchType === 'ingredients' && (
                        <div className="form-group">
                            <label>Ингредиенты (через запятую):</label>
                            <input
                                type="text"
                                value={ingredients}
                                onChange={(e) => setIngredients(e.target.value)}
                                placeholder="яйца, мука, сахар"
                                required
                            />
                        </div>
                    )}

                    {searchType === 'filtering' && (
                        <div className="filter-form">
                            <div className="form-group">
                                <label>Название:</label>
                                <input
                                    type="text"
                                    value={searchRequest.name}
                                    onChange={(e) => setSearchRequest(prev => ({...prev, name: e.target.value}))}
                                    placeholder="Название рецепта на английском"
                                />
                            </div>

                            <div className="form-group">
                                <label>Ингредиенты (через запятую):</label>
                                <input
                                    type="text"
                                    onChange={handleIngredientChange}
                                    placeholder="яйца, мука, сахар"
                                />
                            </div>

                            <div className="filter-row">
                                <div className="filter-group">
                                    <label>Время готовки:</label>
                                    <div className="filter-controls">
                                        <select
                                            value={searchRequest.cookingTimeOperator}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                cookingTimeOperator: e.target.value
                                            }))}
                                        >
                                            <option value="EQ">=</option>
                                            <option value="NEQ">≠</option>
                                            <option value="GT">{">"}</option>
                                            <option value="LT">{"<"}</option>
                                            <option value="GTE">≥</option>
                                            <option value="LTE">≤</option>
                                        </select>
                                        <input
                                            type="number"
                                            value={searchRequest.cookingTime}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                cookingTime: parseInt(e.target.value) || 0
                                            }))}
                                            placeholder="Минуты"
                                            className="small-input"
                                        />
                                    </div>
                                </div>

                                <div className="filter-group">
                                    <label>Общее время:</label>
                                    <div className="filter-controls">
                                        <select
                                            value={searchRequest.totalTimeOperator}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                totalTimeOperator: e.target.value
                                            }))}
                                        >
                                            <option value="EQ">=</option>
                                            <option value="NEQ">≠</option>
                                            <option value="GT">{">"}</option>
                                            <option value="LT">{"<"}</option>
                                            <option value="GTE">≥</option>
                                            <option value="LTE">≤</option>
                                        </select>
                                        <input
                                            type="number"
                                            value={searchRequest.totalTime}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                totalTime: parseInt(e.target.value) || 0
                                            }))}
                                            placeholder="Минуты"
                                            className="small-input"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="filter-row">
                                <div className="filter-group">
                                    <label>Время подготовки:</label>
                                    <div className="filter-controls">
                                        <select
                                            value={searchRequest.preparationTimeOperator}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                preparationTimeOperator: e.target.value
                                            }))}
                                        >
                                            <option value="EQ">=</option>
                                            <option value="NEQ">≠</option>
                                            <option value="GT">{">"}</option>
                                            <option value="LT">{"<"}</option>
                                            <option value="GTE">≥</option>
                                            <option value="LTE">≤</option>
                                        </select>
                                        <input
                                            type="number"
                                            value={searchRequest.preparationTime}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                preparationTime: parseInt(e.target.value) || 0
                                            }))}
                                            placeholder="Минуты"
                                            className="small-input"
                                        />
                                    </div>
                                </div>

                                <div className="filter-group">
                                    <label>Порции:</label>
                                    <div className="filter-controls">
                                        <select
                                            value={searchRequest.servingsOperator}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                servingsOperator: e.target.value
                                            }))}
                                        >
                                            <option value="EQ">=</option>
                                            <option value="NEQ">≠</option>
                                            <option value="GT">{">"}</option>
                                            <option value="LT">{"<"}</option>
                                            <option value="GTE">≥</option>
                                            <option value="LTE">≤</option>
                                        </select>
                                        <input
                                            type="number"
                                            value={searchRequest.servings}
                                            onChange={(e) => setSearchRequest(prev => ({
                                                ...prev,
                                                servings: parseInt(e.target.value) || 0
                                            }))}
                                            placeholder="Количество"
                                            className="small-input"
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    <button type="submit" className="button-search">Найти рецепты</button>
                </form>

                {message && <p className="message">{message}</p>}
            </div>

            {recipes.length > 0 && (
                <div className="results-container">
                    <h3>Найдено рецептов: {recipes.length}</h3>
                    <div className="recipes-grid">
                        {recipes.map((recipe) => (
                            <div key={recipe.id} className="recipe-card">
                                <div className="recipe-header">
                                    <h4>{recipe.name}</h4>
                                    <button
                                        className="button-add-favorite"
                                        onClick={() => handleAddToFavorites(recipe.name)}
                                    >
                                        В избранное
                                    </button>
                                </div>

                                {recipe.description && (
                                    <p className="recipe-description">{recipe.description}</p>
                                )}

                                <div className="recipe-times">
                                    <div className="time-item">
                                        <span className="time-label">Подготовка:</span>
                                        <span className="time-value">{formatDuration(recipe.timeForPreparing)}</span>
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
                </div>
            )}
        </div>
    );
};

export default MainPage;