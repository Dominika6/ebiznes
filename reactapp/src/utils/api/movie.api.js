import API from "./API";
import {rateApi} from "./rate.api";

class MovieApi {
    async getAll() {
        let movies = [];
        try {
            movies = await API.get('/movies');
            movies = movies.data;
        } catch (e) {
            console.error(e);
        }
        try {
            await this.addRates(movies);
            await this.addUserInfo(movies);
        } catch (e) {}
        return movies;
    }

    async getById(movieId) {
        try {
            const movie = await API.get(`/movies/${movieId}`);
            return movie.data
        } catch (e) {
            throw new Error('nie ma')
        }
    }

    async getForUser() {
        let movies = [];
        try {
            movies = await API.get('/user/movies');
            movies = movies.data;
        } catch (e) {
            console.error(e);
        }
        return movies;
    }

    async getForFilmtype(filmtypeId) {
        let movies = [];
        try {
            movies = await API.get(`/filmtypes/${filmtypeId}/movies`);
            movies = movies.data;
        } catch (e) {
            console.error(e);
        }
        try {
            await this.addRates(movies);
            await this.addUserInfo(movies);
        } catch (e) {}
        return movies;
    }

    async getForActor(actorId) {
        let movies = [];
        try {
            movies = await API.get(`/actors/${actorId}/movies`);
            movies = movies.data;
        } catch (e) {
            console.error(e);
        }

        try {
            await this.addRates(movies);
            await this.addUserInfo(movies);
        } catch (e) {}
        return movies;
    }

    async getForDirector(directorId) {
        let movies = [];
        try {
            movies = await API.get(`/directors/${directorId}/movies`);
            movies = movies.data;
        } catch (e) {
            console.error(e);
        }
        try {
            await this.addRates(movies);
            await this.addUserInfo(movies);
        } catch (e) {}
        return movies;
    }

    async getMovieFilmtype(movieId) {
        let filmtypes = [];
        try {
            filmtypes = await API.get(`/movies/${movieId}/filmtypes`);
            filmtypes = filmtypes.data
        } catch (e) {
            console.error(e);
        }

        return filmtypes;
    }

    async getMovieComments(movieId) {
        let comments = [];
        try {
            comments = await API.get(`/movies/${movieId}/comments`);
            comments = comments.data
        } catch (e) {
            console.error(e);
        }

        return comments;
    }

    async getMovieRates(movieId) {
        let rates = [];
        try {
            rates = await API.get(`/movies/${movieId}/rates`);
            rates = rates.data
        } catch (e) {
            console.error(e);
        }

        return rates;
    }

    async getMovieDirectors(movieId) {
        let directors = [];
        try {
            directors = await API.get(`/movies/${movieId}/directors`);
            directors = directors.data
        } catch (e) {
            console.error(e);
        }

        return directors;
    }

    async getMovieActors(movieId) {
        let actors = [];
        try {
            actors = await API.get(`/movies/${movieId}/actors`);
            actors = actors.data
        } catch (e) {
            console.error(e);
        }

        return actors;
    }

    async addRates(movies) {
        let rates = [];
        try {
            rates = await rateApi.getAll();
        } catch (e) {
            console.error(e);
        }

        for (let movie of movies) {
            const movieRates = rates.filter(r => r.movie.id === movie.id);
            const sum = movieRates.reduce((a, b) => +a + +b.value, 0);
            if (movieRates.length > 0) {
                movie.rate = sum/movieRates.length
            } else {
                movie.rate = 0
            }
        }
    }

    async addUserInfo(movies) {
        let userMovies = await API.get('/user/movies');
        const userMovieIds = userMovies.data.map(movie => movie.id);

        for (let movie of movies) {
            movie.isBought = !!userMovieIds.includes(movie.id);
        }
    }
}

export const movieApi = new MovieApi();
