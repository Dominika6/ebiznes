import React, {useContext, useState} from "react";
import {UserContext} from "./UserContext";
import TextField from "@material-ui/core/TextField/TextField";
import Paper from "@material-ui/core/Paper";
import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button";
import {authApi} from "../utils/api/auth.api";
import Alert from "@material-ui/lab/Alert/Alert";
import Snackbar from "@material-ui/core/Snackbar/Snackbar";

export default function EditUser() {
    const {userCtx, setUserCtx} = useContext(UserContext);
    const [firstName, setFirstName] = useState(userCtx.user.firstName || '');
    const [surname, setSurname] = useState(userCtx.user.surname || '');
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const handleEdit = async () => {
        try {
            await authApi.edit({firstName, surname})
            const ctx = { user: {...userCtx.user, firstName, surname}, token: userCtx.token };
            localStorage.setItem('userCtx', JSON.stringify(ctx));
            setUserCtx(ctx);
            setError(null)
            setSuccess('Poprawnie zmieniono dane uzytkownika')
            setTimeout(() => setSuccess(null), 3000);
        } catch (e) {
            setError('Błąd podczas edycji, spróbuj jeszcze raz');
        }
    }

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setError(null);
    };

    return (
        <>
            <h3>Edycja</h3>
            <Snackbar open={error} autoHideDuration={4000} onClose={handleClose}>
                <Alert onClose={handleClose} severity="error">
                    {error}
                </Alert>
            </Snackbar>

            <Paper>
                <Box px={3} py={2}>

                    <TextField
                        fullWidth
                        required
                        value={firstName}
                        onChange={e => setFirstName(e.target.value)}
                        id="firstName"
                        label="Imię"
                    />

                    <TextField
                        fullWidth
                        required
                        value={surname}
                        onChange={e => setSurname(e.target.value)}
                        id="surname"
                        label="Nazwisko"
                    />
                    <Box mt={3}>
                        {success ?
                            <Alert  variant="filled"  severity="success">{success}</Alert> :
                            <Button onClick={handleEdit} variant="outlined" color="secondary">Edytuj</Button>}

                    </Box>
                </Box>
            </Paper>
        </>
    )
}
