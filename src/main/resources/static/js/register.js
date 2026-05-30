const cityInput = document.getElementById('cityInput');
const suggestionsList = document.getElementById('suggestionsList');
let timeoutId = null;

async function fetchCitySuggestions(query) {
    if (query.length < 2) {
        suggestionsList.style.display = 'none';
        return;
    }

    const url = `https://gd.geobytes.com/AutoCompleteCity?callback=?&filter=RU&sort=size&q=${encodeURIComponent(query)}`;

    try {
        const response = await fetch(url);
        const text = await response.text();

        const match = text.match(/\[\".*?\"\]/);
        if (match) {
            const cities = JSON.parse(match[0]);
            displaySuggestions(cities);
        } else {
            suggestionsList.style.display = 'none';
        }
    } catch (error) {
        console.error('Ошибка Geobytes:', error);
        suggestionsList.style.display = 'none';
    }
}

function displaySuggestions(cities) {
    if (!cities || cities.length === 0) {
        suggestionsList.style.display = 'none';
        return;
    }

    suggestionsList.innerHTML = '';
    cities.forEach(city => {
        const cleanCity = city.replace(/^"|"$/g, '');
        const div = document.createElement('div');
        div.textContent = cleanCity;
        div.addEventListener('click', () => {
            cityInput.value = cleanCity;
            suggestionsList.style.display = 'none';
        });
        suggestionsList.appendChild(div);
    });
    suggestionsList.style.display = 'block';
}

cityInput.addEventListener('input', function() {
    clearTimeout(timeoutId);
    const query = this.value;
    timeoutId = setTimeout(() => fetchCitySuggestions(query), 300);
});

document.addEventListener('click', function(e) {
    if (e.target !== cityInput) {
        suggestionsList.style.display = 'none';
    }
});