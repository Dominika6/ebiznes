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
import Alert from "@material-ui/lab/Alert/Alert";
import Snackbar from "@material-ui/core/Snackbar/Snackbar";

const useStyles = makeStyles(theme => ({card: {
        backgroundColor: 'transparent'
    },
    cardHeader: {
        paddingTop: '16px',
        backgroundColor: 'transparent',
        color: theme.palette.primary.main
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

export default function Registration() {
    const classes = useStyles();
    let history = useHistory();
    const {setUserCtx} = useContext(UserContext);

    const [formData, setFormData] = useState({
        firstName: null,
        surname: null,
        email: null,
        password: null
    });
    const [error, setError] = useState(null);

    const registrationHandler = async () => {
        try {
            const registrationRes = await authApi.registration(formData);
            const token = registrationRes.token;
            const userMe = await authApi.me(token);
            const ctx = { user: userMe, token: token };
            localStorage.setItem('userCtx', JSON.stringify(ctx));
            setUserCtx(ctx);
            history.push('/');
        } catch (e) {
            setError('Błąd podczas rejestracji, spróbuj jeszcze raz');
        }
    };

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setError(null);
    };

    const onChangeHandler = (event) => {
        const target = event.target;

        setFormData({
            ...formData,
            [target.name]: target.value
        });
    };

    return (
        <Grid item xs={3}>
            <Snackbar open={error} autoHideDuration={4000} onClose={handleClose}>
                <Alert onClose={handleClose} severity="error">
                    {error}
                </Alert>
            </Snackbar>

            <Card className={classes.card} elevation={0}>
                <CardHeader className={classes.cardHeader} title="Rejestracja"/>
                <CardContent className={classes.cardContent}>
                    <form className={classes.form}>
                        <TextField
                            InputProps={{
                                className: classes.formFieldText
                            }}
                            className={classes.formField}
                            fullWidth
                            required
                            value={formData.firstName}
                            onChange={onChangeHandler}
                            name="firstName"
                            id="firstName"
                            label="Imię"
                        />
                        <TextField
                            InputProps={{
                                className: classes.formFieldText
                            }}
                            className={classes.formField}
                            fullWidth
                            required
                            value={formData.surname}
                            name="surname"
                            onChange={onChangeHandler}
                            id="surname"
                            label="Nazwisko"
                        />
                        <TextField
                            InputProps={{
                                className: classes.formFieldText
                            }}
                            className={classes.formField}
                            fullWidth
                            required
                            value={formData.email}
                            name="email"
                            onChange={onChangeHandler}
                            id="email"
                            label="Adres e-mail"
                        />

                        <TextField
                            InputProps={{
                                className: classes.formFieldText
                            }}
                            className={classes.formField}
                            fullWidth
                            value={formData.password}
                            name="password"
                            onChange={onChangeHandler}
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
                        <Grid item><Button onClick={registrationHandler} variant="outlined" color="primary">Zarejestruj się</Button></Grid>
                        <Grid item><Button className={classes.smallButton} component={Link} to="/logowanie">Przejdź do logowania</Button></Grid>
                    </Grid>
                </CardActions>
            </Card>
        </Grid>
    );
}