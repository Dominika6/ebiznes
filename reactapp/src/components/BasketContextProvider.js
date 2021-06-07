import React, {useState} from "react";
import {BasketContext} from "./BasketContext";
import * as _ from 'lodash';

const initialBasketCtx = JSON.parse(localStorage.getItem('basketCtx')) || {
    movies: [],
    pay: "1"
};

export default function BasketContextProvider({children}) {
    const [basket, setBasket] = useState(initialBasketCtx);

    const addMovieToBasket = (movieId) => {
        if (!basket.movies.includes(movieId)) {
            const val = {
                ...basket,
                movies: [...basket.movies, movieId]
            };
            setBasket(val);
            setLocalStorage(val);
        }
    };

    const removeMovieFromBasket = (movieId) => {
        if (basket.movies.includes(movieId)) {
            const val = {
                ...basket,
                movies: [..._.pull(basket.movies, movieId)],
            };
            setBasket(val);
            setLocalStorage(val);
        }
    };

    const removeAllMoviesFromBasket = () => {
        const val = {
            pay: '1',
            movies: []
        };
        setBasket(val);
        setLocalStorage(val);
    };

    const setPay = (payId) => {
        const val = {
            ...basket,
            pay: payId
        };
        setBasket(val);
        setLocalStorage(val);
    };

    const isMovieAlreadyAdded = (movieId) => {
        return basket.movies.includes(movieId)
    };

    const setLocalStorage = (val) => {
        localStorage.setItem('basketCtx', JSON.stringify(val))
    };

    const getBasketMovies = basket.movies;
    const getBasketPay = basket.pay;

    const value = {
        getBasketMovies,
        getBasketPay,
        addMovieToBasket,
        removeMovieFromBasket,
        setPay,
        isMovieAlreadyAdded,
        removeAllMoviesFromBasket
    };

    return (
        <BasketContext.Provider value={value}>
            {children}
        </BasketContext.Provider>
    )
}