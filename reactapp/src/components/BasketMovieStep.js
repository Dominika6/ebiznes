import React from "react";
import StepLabel from "@material-ui/core/StepLabel/StepLabel";
import StepContent from "@material-ui/core/StepContent/StepContent";
import Grid from "@material-ui/core/Grid";
import BasketMovieItem from "./BasketMovieItem";
import Button from "@material-ui/core/Button";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Step from "@material-ui/core/Step/Step";

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
}))

export default function BasketMovieStep({basketMovies, handleNext, ...other}) {
    const classes = useStyles();
    return (
        <Step key="filmy" {...other}>
            <StepLabel>Filmy</StepLabel>
            <StepContent>

                <Grid container spacing={3}>
                    {basketMovies.map(movie => (
                        <Grid item xs={2}>
                            <BasketMovieItem movie={movie}/>
                        </Grid>
                    ))}

                </Grid>

                <div className={classes.actionsContainer}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleNext}
                        className={classes.button}
                    >Płatność</Button>
                </div>
            </StepContent>
        </Step>
    )
}