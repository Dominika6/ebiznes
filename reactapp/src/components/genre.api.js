import API from "./API";

class GenreApi {
    async getAll() {
        let genres = [];
        try {
            genres = await API.get('/genres');
            genres = genres.data;
        } catch (e) {
            console.log(e);
        }
        return genres;
    }

}

export const genreApi = new GenreApi();