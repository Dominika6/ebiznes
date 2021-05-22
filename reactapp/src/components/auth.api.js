import API from "./API";

class AuthApi {
    async login(email, password) {
        const res = await API.post('/auth/signin', { email, password });
        return res.data.token;
    }

    async me(token) {
        const res = await API.get('/auth/me', { headers: { 'X-Auth-Token': token}});
        return res.data
    }

    async signUp(data) {
        const body = {
            firstName: data.firstName,
            surname: data.surname,
            email: data.email,
            password: data.password
        };
        const res = await API.post('/auth/signup', body);
        return res.data
    }

    async edit(data) {
        const body = {
            firstName: data.firstName,
            surname: data.surname
        };
        const res = await API.post('/user/edit', body);
        return res.data
    }
}

export const authApi = new AuthApi();
