const movieSearch = document.getElementById('movieSearch');
const movieSuggestions = document.getElementById('movieSuggestions');
let movieTimeout = null;

async function searchMovies(query) {
    if (query.length < 2) {
        movieSuggestions.style.display = 'none';
        return;
    }
    try {
        const response = await fetch(`/api/movies/search?query=${encodeURIComponent(query)}`);
        const movies = await response.json();
        displayMovieSuggestions(movies);
    } catch (error) {
        console.error('Ошибка поиска фильмов:', error);
        movieSuggestions.style.display = 'none';
    }
}

function displayMovieSuggestions(movies) {
    if (!movies || movies.length === 0) {
        movieSuggestions.style.display = 'none';
        return;
    }
    movieSuggestions.innerHTML = '';
    movies.forEach(movie => {
        const div = document.createElement('div');
        div.innerHTML = `
            <div class="d-flex align-items-center">
                <img src="${movie.poster}" style="width: 40px; height: 60px; object-fit: cover; margin-right: 10px;" onerror="this.src='/images/no-poster.png'">
                <div>
                    <strong>${movie.title}</strong><br>
                    <span class="text-muted">${movie.year}</span>
                </div>
            </div>
        `;
        div.style.cursor = 'pointer';
        div.style.padding = '8px';
        div.style.borderBottom = '1px solid #eee';
        div.addEventListener('click', () => selectMovie(movie));
        movieSuggestions.appendChild(div);
    });
    movieSuggestions.style.display = 'block';
}

function selectMovie(movie) {
    console.log('Выбран фильм:', movie);

    document.getElementById('favoriteMovieId').value = movie.id;
    document.getElementById('favoriteMovieTitle').value = movie.title;
    document.getElementById('favoriteMovieYear').value = movie.year;
    document.getElementById('favoriteMoviePoster').value = movie.poster;

    const selectedDiv = document.getElementById('selectedMovie');
    selectedDiv.innerHTML = `
        <div class="border rounded p-2 d-flex align-items-center">
            <img src="${movie.poster}" style="width: 50px; height: 70px; object-fit: cover; margin-right: 10px;" onerror="this.src='/images/no-poster.png'">
            <div>
                <strong>${movie.title}</strong>
                <span class="text-muted">${movie.year}</span>
            </div>
            <button type="button" id="clearMovie" class="btn btn-sm btn-danger ms-auto">🗑️ Удалить</button>
        </div>
    `;

    document.getElementById('clearMovie').addEventListener('click', clearMovie);

    movieSearch.value = '';
    movieSuggestions.style.display = 'none';
}

function clearMovie() {
    document.getElementById('favoriteMovieId').value = '';
    document.getElementById('favoriteMovieTitle').value = '';
    document.getElementById('favoriteMovieYear').value = '';
    document.getElementById('favoriteMoviePoster').value = '';
    document.getElementById('selectedMovie').innerHTML = '';
}

movieSearch.addEventListener('input', function() {
    clearTimeout(movieTimeout);
    const query = this.value.trim();
    movieTimeout = setTimeout(() => searchMovies(query), 500);
});

document.addEventListener('click', function(e) {
    if (e.target !== movieSearch && !movieSuggestions.contains(e.target)) {
        movieSuggestions.style.display = 'none';
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const movieId = document.getElementById('favoriteMovieId').value;
    if (movieId) {
        const selectedDiv = document.getElementById('selectedMovie');
        const title = document.getElementById('favoriteMovieTitle').value;
        const year = document.getElementById('favoriteMovieYear').value;
        const poster = document.getElementById('favoriteMoviePoster').value;
        selectedDiv.innerHTML = `
            <div class="border rounded p-2 d-flex align-items-center">
                <img src="${poster}" style="width: 50px; height: 70px; object-fit: cover; margin-right: 10px;" onerror="this.src='/images/no-poster.png'">
                <div>
                    <strong>${title}</strong>
                    <span class="text-muted">${year}</span>
                </div>
                <button type="button" id="clearMovie" class="btn btn-sm btn-danger ms-auto">🗑️ Удалить</button>
            </div>
        `;
        document.getElementById('clearMovie').addEventListener('click', clearMovie);
    }
});