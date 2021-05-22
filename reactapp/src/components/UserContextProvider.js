import React, {useState} from "react";
import {UserContext} from "./UserContext";

const ctxFromStorage = localStorage.getItem('userCtx');

const initialUserCtx = JSON.parse(ctxFromStorage) || {
    user:  null,
    token: null
};

export default function UserContextProvider({children}) {
    const [userCtx, setUserCtx] = useState(initialUserCtx);

    return (
        <UserContext.Provider value={{userCtx, setUserCtx}}>
            {children}
        </UserContext.Provider>
    )
}