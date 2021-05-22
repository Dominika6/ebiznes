import API from "./API";

class RatingApi {
    async getAll() {
        let ratings = [];
        try {
            ratings = await API.get('/rates');
            ratings = ratings.data;
        } catch (e) {
            console.log(e);
        }
        return ratings;
    }
}

export const ratingApi = new RatingApi();