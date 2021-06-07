import axios from "axios";

const API =  axios.create({
    baseURL: "http://localhost:9000/api/",
    responseType: "json",
});

API.interceptors.request.use(function (config) {
    const dataFromLocalStorage = localStorage.getItem('userCtx');
    const data = JSON.parse(dataFromLocalStorage);

    if (data && data.user && data.token) {
        config.headers.common['X-Auth-Token'] = data.token
    }

    return config;
});


export default API