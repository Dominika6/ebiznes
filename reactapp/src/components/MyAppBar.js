import AppBar from "@material-ui/core/AppBar/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import {Link, useHistory} from "react-router-dom";
import React, {useContext} from "react";
import {makeStyles} from "@material-ui/core/styles";
import {UserContext} from "./UserContext";
import AppBarBasket from "./AppBarBasket";


const useStyles = makeStyles((theme) => ({
    appBar: {
        zIndex: theme.zIndex.drawer + 1,
    },
    title: {
        flexGrow: 1
    }
}));


export default function MyAppBar() {
    const classes = useStyles();
    const {userCtx, setUserCtx} = useContext(UserContext);

    let history = useHistory();

    const logOutHandler = () => {
        setUserCtx({user: null, token: null});
        localStorage.removeItem('userCtx');
        history.push('/');
    };

    const UserInfoPanel = () => {
        if (userCtx.user) {
            return (
                <>
                    <Typography variant="button">{userCtx.user.firstName} {userCtx.user.surname}</Typography>
                    <AppBarBasket/>
                    <Button color="inherit" onClick={logOutHandler}>Wyloguj się</Button>
                </>
            )
        } else {
            return (
                <>
                    <AppBarBasket/>
                    <Button color="inherit" component={Link} to="/logowanie">Zaloguj się</Button>
                </>
            )
        }
    };

    return (
        <AppBar position="fixed" className={classes.appBar}>
            <Toolbar>
                <Typography className={classes.title} variant="h6" noWrap>Buy a Film</Typography>
                <UserInfoPanel/>
            </Toolbar>
        </AppBar>
    )
}
