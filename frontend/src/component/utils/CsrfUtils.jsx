export const getCsrfToken = async () => {
    try {
        const response = await fetch('http://localhost:8082/csrf', {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            return await response.json();
        }
        throw new Error('Failed to get CSRF token');
    } catch (error) {
        console.error('CSRF token error:', error);
        throw error;
    }
};