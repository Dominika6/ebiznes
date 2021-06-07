import API from "./API";

class OrderApi {
    async getPayMethods() {
        let pays = [];
        try {
            pays = await API.get('/pays');
            pays = pays.data;
        } catch (e) {
            console.log(e);
        }
        return pays;
    }

    async makeOrder(pay, movies) {
        if (movies.length === 0) {
            throw new Error()
        }
        await API.post('/orders', { pay, movies })
    }

    async getUserOrders() {
        let orders = [];
        try {
            orders = await API.get('/user/orders');
            orders = orders.data;
        } catch (e) {
            console.log(e);
        }
        return orders;
    }
}

export const orderApi = new OrderApi();
