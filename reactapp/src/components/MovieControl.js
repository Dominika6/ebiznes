import React, {useContext, useEffect, useState} from "react";
import Button from "@material-ui/core/Button";
import {AddCircle, DeleteForever, LibraryAddCheck} from '@material-ui/icons';
import {movieApi} from "./movie.api";
import {BasketContext} from "./BasketContext";
import green from "@material-ui/core/colors/green";
import {createMuiTheme} from "@material-ui/core";
import {ThemeProvider} from "@material-ui/styles";
import Grid from "@material-ui/core/Grid";
import { red} from "@material-ui/core/colors";
import {NavLink} from "react-router-dom";

const basketButtonTheme = createMuiTheme({
    palette: {
        primary: green,
        secondary: red
    },
});


export default function MovieControl({movieId, movieTitle}) {
    const [isBought, setIsBought] = useState(false);
    const {addMovieToBasket, removeMovieFromBasket, isMovieAlreadyAdded} = useContext(BasketContext);


    useEffect(() => {
        const fetchData = async () => {
            const movies = await movieApi.getForUser();
            const movieIds = movies.map(m => m.movieId);
            setIsBought(movieIds.includes(movieId));
        };

        fetchData();
    }, []);


    const BasketButton = () => {
        if (isMovieAlreadyAdded(movieId)) {
            return (
                <ThemeProvider theme={basketButtonTheme}>
                    <Grid container spacing={1}>
                        <Grid item xs={3}>
                            <Button onClick={() => removeMovieFromBasket(movieId)} startIcon={<DeleteForever />} fullWidth variant="contained" color="secondary">Usuń</Button>
                        </Grid>
                        <Grid item xs={9}>
                            <Button startIcon={<LibraryAddCheck />} fullWidth variant="contained" color="primary" component={NavLink} exact={true} to="/koszyk">
                                Przejdź do koszyka
                            </Button>
                        </Grid>
                    </Grid>
                </ThemeProvider>
            )
        } else {
            return (
                <Button onClick={() => addMovieToBasket(movieId)} startIcon={<AddCircle />} fullWidth variant="contained" color="secondary">
                    Dodaj do koszyka
                </Button>
            )
        }
    };

    if (!isBought) {
        return (
            <BasketButton/>
        )
    } else {
        return (
            <>
                <div> Watch film! </div>
            </>
        );
    }

}
