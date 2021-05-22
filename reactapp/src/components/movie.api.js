import API from "./API";
import {ratingApi} from "./rating.api";

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
            await this.addRatings(movies);
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

    async getForGenre(filmtypeId) {
        let movies = [];
        try {
            movies = await API.get(`/filmtypes/${filmtypeId}/movies`);
            movies = movies.data;
        } catch (e) {
            console.error(e);
        }
        try {
            await this.addRatings(movies);
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
            await this.addRatings(movies);
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
            await this.addRatings(movies);
            await this.addUserInfo(movies);
        } catch (e) {}
        return movies;
    }

    async getMovieGenre(movieId) {
        let genres = [];
        try {
            genres = await API.get(`/movies/${movieId}/filmtypes`);
            genres = genres.data
        } catch (e) {
            console.error(e);
        }

        return genres;
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

    async getMovieRatings(movieId) {
        let ratings = [];
        try {
            ratings = await API.get(`/movies/${movieId}/rates`);
            ratings = ratings.data
        } catch (e) {
            console.error(e);
        }

        return ratings;
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

    async addRatings(movies) {
        let ratings = [];
        try {
            ratings = await ratingApi.getAll();
        } catch (e) {
            console.error(e);
        }

        for (let movie of movies) {
            const movieRatings = ratings.filter(r => r.movie.id === movie.id);
            const sum = movieRatings.reduce((a, b) => +a + +b.value, 0);
            if (movieRatings.length > 0) {
                movie.rating = sum/movieRatings.length
            } else {
                movie.rating = 0
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
