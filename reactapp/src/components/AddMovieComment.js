import React, {useContext, useState} from "react";
import {UserContext} from "./UserContext";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Box from "@material-ui/core/Box";
import {commentApi} from "../utils/api/comment.api";
import Alert from "@material-ui/lab/Alert/Alert";
import Snackbar from "@material-ui/core/Snackbar/Snackbar";

export default function AddMovieComment({movieId, refreshHandler}) {
    const {userCtx} = useContext(UserContext);
    const [comment, setComment] = useState(null);
    const [error, setError] = useState(null);

    const sendComment = async () => {
        try {
            await commentApi.post(movieId, comment);
            setComment('');
            setError(null);
            refreshHandler();
        } catch (e) {
            setError('Błąd podczas dodawania komentarza, spróbuj jeszcze raz');
        }
    };

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setError(null);
    };

    if (userCtx.user) {
        return (
            <Box mb={3}>
                <Snackbar open={error} autoHideDuration={4000} onClose={handleClose}>
                    <Alert onClose={handleClose} severity="error">
                        {error}
                    </Alert>
                </Snackbar>

                <h4>Dodaj komentarz:</h4>
                <TextField
                    id="commentField"
                    multiline
                    rows={2}
                    fullWidth
                    variant="filled"
                    value={comment}
                    onChange={e => setComment(e.target.value)}
                />
                <Box mt={2}>
                    <Button onClick={sendComment} variant="outlined" color="secondary">
                        Dodaj
                    </Button>
                </Box>
            </Box>
        )
    } else {
        return null
    }
}