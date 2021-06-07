import API from "./API";

class FilmtypeApi {
    async getAll() {
        let filmtypes = [];
        try {
            filmtypes = await API.get('/filmtypes');
            filmtypes = filmtypes.data;
        } catch (e) {
            console.log(e);
        }
        return filmtypes;
    }

}

export const filmtypeApi = new FilmtypeApi();