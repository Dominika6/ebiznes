import API from "./API";

class RateApi {
    async getAll() {
        let rates = [];
        try {
            rates = await API.get('/rates');
            rates = rates.data;
        } catch (e) {
            console.log(e);
        }
        return rates;
    }
}

export const rateApi = new RateApi();