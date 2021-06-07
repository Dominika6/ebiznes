import React, {useContext, useState} from 'react';
import Grid from "@material-ui/core/Grid";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import Button from "@material-ui/core/Button";
import CardHeader from "@material-ui/core/CardHeader";
import makeStyles from "@material-ui/core/styles/makeStyles";
import TextField from "@material-ui/core/TextField";
import {Link, useHistory} from "react-router-dom";
import {UserContext} from "../UserContext";
import {authApi} from "../../utils/api/auth.api";
import Snackbar from "@material-ui/core/Snackbar";
import Alert from "@material-ui/lab/Alert";
import Box from "@material-ui/core/Box";
import Divider from "@material-ui/core/Divider";
import GoogleIcon from "./GoogleIcon";
import {createMuiTheme, ThemeProvider } from "@material-ui/core";
import * as colors from "@material-ui/core/colors";
import * as URL from "url";
import FacebookIcon from "./FacebookIcon";

const useStyles = makeStyles(theme => ({
    card: {
        backgroundColor: 'transparent'
    },
    cardHeader: {
        // textAlign: 'center',
        paddingTop: '16px',
        backgroundColor: 'transparent',
        color: theme.palette.secondary.main
    },
    cardContent: {
        paddingBlockStart: '32px',
        paddingBlockEnd: '32px',
        backgroundColor: theme.palette.text.primary
    },
    cardActions: {
        paddingBlockStart: '16px',
        backgroundColor: 'transparent'
    },
    form: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    formField: {
        marginBlockEnd: '24px'
    },
    formFieldText: {
        color: theme.palette.text.secondary
    },
    smallButton: {
        color: '#ccc'
    }
}));


const googleTheme = createMuiTheme({
    palette: {
        primary: { main: colors.common.black, contrastText: colors.common.white },
    }
});

const facebookTheme = createMuiTheme({
    palette: {
        primary: { main: colors.common.black, contrastText: colors.common.white },
    }
});

export default function Login() {
    const classes = useStyles();
    let history = useHistory();

    const {setUserCtx} = useContext(UserContext);
    const [email, setEmail] = useState(null);
    const [password, setPassword] = useState(null);

    const [error, setError] = useState(null);

    const loginHandler = async () => {
        try {
            const token = await authApi.login(email, password);
            await tokenHandler(token)
        } catch (e) {
            setError('Błąd podczas logowania, spróbuj jeszcze raz');
        }
    };

    const tokenHandler = async (token) => {
        try {
            const userMe = await authApi.me(token);
            const ctx = { user: userMe, token: token };
            localStorage.setItem('userCtx', JSON.stringify(ctx));
            setUserCtx(ctx);
            history.push('/');
        } catch (e) {
            setError('Błąd podczas logowania, spróbuj jeszcze raz');
        }
    };

    const googleLoginHandler = async () => {
        const strWindowFeatures = 'toolbar=no, menubar=no, width=600, height=700, top=100, left=200';
        const popup = window.open("http://localhost:9000/api/auth/social/google", "E-biznes OAuth", strWindowFeatures);

        const messagesInterval = setInterval(() => {
            popup.postMessage("token request", "http://localhost:3000/oauth/google");
        }, 1000);

        window.addEventListener('message', async (event) => {
            if (typeof event.data === 'string' || event.data instanceof String) {
                if (event.data.startsWith('token')) {
                    clearInterval(messagesInterval);
                    const token = event.data.split('___')[1]
                    await tokenHandler(token);
                    popup.close()
                }
            }
        }, false);
    };

     const facebookLoginHandler = async () => {
            const strWindowFeatures = 'toolbar=no, menubar=no, width=600, height=700, top=100, left=200';
            const popup = window.open("http://localhost:9000/api/auth/social/facebook", "E-biznes OAuth", strWindowFeatures);

            const messagesInterval = setInterval(() => {
                popup.postMessage("token request", "http://localhost:3000/oauth/facebook");
            }, 1000);

            window.addEventListener('message', async (event) => {
                if (typeof event.data === 'string' || event.data instanceof String) {
                    if (event.data.startsWith('token')) {
                        clearInterval(messagesInterval);
                        const token = event.data.split('___')[1]
                        await tokenHandler(token);
                        popup.close()
                    }
                }
            }, false);
        };

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setError(null);
    };

    return (
        <Grid item xs={3}>
            <Snackbar open={error} autoHideDuration={4000} onClose={handleClose}>
                <Alert onClose={handleClose} severity="error">
                    {error}
                </Alert>
            </Snackbar>

            <Card className={classes.card} elevation={0}>
                <CardHeader className={classes.cardHeader} title="Logowanie"/>
                <CardContent className={classes.cardContent}>
                    <ThemeProvider theme={googleTheme}>
                        <Button onClick={googleLoginHandler} size="large" startIcon={<GoogleIcon />} variant="outlined" fullWidth color="primary">
                            Log in with Google
                        </Button>
                    </ThemeProvider>
                    <br/><br/>
                    <ThemeProvider theme={facebookTheme}>
                        <Button onClick={facebookLoginHandler} size="large" startIcon={<FacebookIcon />} variant="outlined" fullWidth color="primary">
                            Log in with Facebook
                        </Button>
                    </ThemeProvider>
                    <Box py={4}>
                        <Divider/>
                    </Box>
                    <form className={classes.form}>
                        <TextField
                            InputProps={{
                                className: classes.formFieldText
                            }}
                            className={classes.formField}
                            fullWidth
                            required
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            id="email"
                            label="Adres e-mail"
                        />

                        <TextField
                            InputProps={{
                                className: classes.formFieldText
                            }}
                            className={classes.formField}
                            fullWidth
                            required
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            id="password"
                            label="Hasło"
                            type="password"
                        />

                    </form>
                </CardContent>
                <CardActions className={classes.cardActions}>
                    <Grid
                        justify="space-between"
                        container
                    >
                        <Grid item><Button onClick={loginHandler} variant="outlined" color="secondary">Zaloguj się</Button></Grid>
                        <Grid item><Button className={classes.smallButton} component={Link} to="/rejestracja">Przejdź do rejestracji</Button></Grid>
                    </Grid>
                </CardActions>
            </Card>
        </Grid>
    );
}