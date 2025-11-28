export const formatDuration = (seconds) => {
    if (!seconds) return 'Не указан';
    const minutes = Math.round(seconds / 60);
    return `${minutes} мин`;
};

export const formatIngredient = (ingredient) => {
    const {name, quantity, unit} = ingredient;
    let formatted = '';
    if (quantity && quantity !== '1') {
        formatted += `${quantity} `;
    }
    if (unit) {
        formatted += `${unit} `;
    }
    formatted += name;
    return formatted;
};