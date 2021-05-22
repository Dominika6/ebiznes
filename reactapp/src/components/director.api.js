import API from "./API";

class DirectorApi {
    async get(directorId) {
        let director = null;
        try {
            director = await API.get(`/directors/${directorId}`);
            director = director.data;
        } catch (e) {
            console.log(e);
        }
        return director;
    }
}

export const directorApi = new DirectorApi();
