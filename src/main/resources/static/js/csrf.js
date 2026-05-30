function getCsrfToken() {
    let cookieValue = null;
    const name = 'XSRF-TOKEN';
    if (document.cookie && document.cookie !== '') {
        const cookies = document.cookie.split(';');
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            if (cookie.substring(0, name.length + 1) === (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}

const originalFetch = window.fetch;
window.fetch = function(...args) {
    const csrfToken = getCsrfToken();

    if (csrfToken && args[1] && args[1].method &&
        (args[1].method === 'POST' || args[1].method === 'PUT' || args[1].method === 'DELETE' || args[1].method === 'PATCH')) {

        if (!args[1].headers) {
            args[1].headers = {};
        }
        args[1].headers['X-XSRF-TOKEN'] = csrfToken;
    }

    return originalFetch.apply(this, args);
};