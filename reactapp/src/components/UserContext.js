import {createContext} from "react";

const UserContext = createContext({user: null, token: null});

export { UserContext }