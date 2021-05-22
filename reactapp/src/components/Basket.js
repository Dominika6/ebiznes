import React, {useContext, useEffect, useState} from "react";
import {BasketContext} from "./BasketContext";
import Button from "@material-ui/core/Button";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Paper from "@material-ui/core/Paper";
import Typography from "@material-ui/core/Typography";
import Stepper from "@material-ui/core/Stepper";
import {movieApi} from "./movie.api";
import {UserContext} from "./UserContext";
import BasketMovieStep from "./BasketMovieStep";
import BasketPaymentStep from "./BasketPaymentStep";
import BasketSummaryStep from "./BasketSummaryStep";
import Alert from "@material-ui/lab/Alert/Alert";


const useStyles = makeStyles((theme) => ({
    button: {
        marginTop: theme.spacing(1),
        marginRight: theme.spacing(1),
    },
    actionsContainer: {
        marginBottom: theme.spacing(2),
    },
    resetContainer: {
        padding: theme.spacing(3),
    },
}));


export default function Basket() {
    const { getBasketMovies, getBasketPayment } = useContext(BasketContext);
    const {userCtx} = useContext(UserContext);

    const [basketMovies, setBasketMovies] = useState([]);
    const [basketPrice, setBasketPrice] = useState(0);

    useEffect(() => {
        const fetchData = async () => {
            let movies = [];
            for (let movieId of getBasketMovies) {
                movies.push(await movieApi.getById(movieId))
            }
            let sum = 0.0;
            for (let movie of movies) {
                sum += +(movie.price);
            }
            setBasketPrice(sum.toFixed(2));
            if (userCtx.user) {
                await movieApi.addUserInfo(movies)
            }
            setBasketMovies(movies);
        };
        fetchData();
    }, [getBasketMovies]);

    const classes = useStyles();
    const [activeStep, setActiveStep] = React.useState(0);

    const handleNext = () => {
        setActiveStep((prevActiveStep) => prevActiveStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevActiveStep) => prevActiveStep - 1);
    };

    const handleReset = () => {
        setActiveStep(0);
    };

    if (getBasketMovies.length === 0) {
        return (
            <>
                <h2>Koszyk</h2>
                <Alert className={classes.table} variant="filled"  severity="info">Twój koszyk jest pusty</Alert>
            </>
        )
    }
    return (
        <>
            <h2>Koszyk</h2>

            <Stepper activeStep={activeStep} orientation="vertical">
                <BasketMovieStep handleNext={handleNext} basketMovies={basketMovies}/>
                <BasketPaymentStep handleNext={handleNext} handleBack={handleBack}/>
                <BasketSummaryStep
                    handleNext={handleNext}
                    handleBack={handleBack}
                    basketMovies={basketMovies}
                    basketPayment={getBasketPayment}
                    basketMoviesIds={getBasketMovies}
                    basketPrice={basketPrice}
                />
            </Stepper>
            {activeStep === 3 && (
                <Paper square elevation={0} className={classes.resetContainer}>
                    <Typography>All steps completed - you&apos;re finished</Typography>
                    <Button onClick={handleReset} className={classes.button}>
                        Reset
                    </Button>
                </Paper>
            )}
        </>
    )
}
