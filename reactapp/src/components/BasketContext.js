import {createContext} from "react";

const BasketContext = createContext({ movies: [], payment: null });

export { BasketContext }