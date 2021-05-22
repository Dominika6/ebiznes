import React, {useContext} from "react";
import {UserContext} from "./UserContext";
import Paper from "@material-ui/core/Paper";
import Box from "@material-ui/core/Box";
import {useHistory} from "react-router-dom";

export default function UserInfo() {
    const {userCtx} = useContext(UserContext);
    let history = useHistory();

    if (!userCtx.user) {
        history.push('/')
    } else {
        return (
            <>
                <h3>Informacje o użytkowniku</h3>
                <Paper>
                    <Box px={2} py={1}>
                        <p>Imię: <Box component="span" ml={1} fontWeight="fontWeightBold">{userCtx.user.firstName || ''}</Box></p>
                        <p>Nazwisko: <Box component="span" ml={1} fontWeight="fontWeightBold">{userCtx.user.surname || ''}</Box></p>
                        <p>E-mail: <Box component="span" ml={1} fontWeight="fontWeightBold">{userCtx.user.email || ''}</Box></p>
                    </Box>
                </Paper>
            </>
        )
    }

}
