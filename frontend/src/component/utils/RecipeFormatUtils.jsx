export const formatDuration = (seconds) => {
    if (!seconds) return 'Not specified';
    const minutes = Math.round(seconds / 60);
    return `${minutes} min`;
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