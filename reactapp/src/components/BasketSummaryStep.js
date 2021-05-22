import React, {useContext, useState} from "react";
import StepLabel from "@material-ui/core/StepLabel/StepLabel";
import StepContent from "@material-ui/core/StepContent/StepContent";
import Button from "@material-ui/core/Button";
import Step from "@material-ui/core/Step/Step";
import makeStyles from "@material-ui/core/styles/makeStyles";
import TableRow from "@material-ui/core/TableRow";
import TableHead from "@material-ui/core/TableHead";
import Table from "@material-ui/core/Table";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import Chip from "@material-ui/core/Chip";
import {useHistory} from "react-router-dom";
import {orderApi} from "./order.api";
import Alert from "@material-ui/lab/Alert";
import {BasketContext} from "./BasketContext";

const useStyles = makeStyles((theme) => ({
    button: {
        marginTop: theme.spacing(1),
        marginRight: theme.spacing(1),
    },
    actionsContainer: {
        marginBottom: theme.spacing(2),
    },
    table: {
        maxWidth: '60%'
    },
    price: {
        // borderRadius: 0,
        fontWeight: '600',
        // fontSize: '1.2rem'
    },
    summaryPrice: {
        borderRadius: 0,
        fontWeight: '900',
        fontSize: '1.6rem'
    },
    movieTitle: {
        fontWeight: '900'
    }
}));

export default function BaksetSummaryStep({handleBack, handleNext, basketMovies, basketMoviesIds, basketPayment, basketPrice, ...other}) {
    const classes = useStyles();
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState(false);
    let history = useHistory();
    const {removeAllMoviesFromBasket} = useContext(BasketContext);

    const makeOrderHandler = async () => {
        try {
            await orderApi.makeOrder(basketPayment, basketMoviesIds);
            setSuccess(true);
            setTimeout(() => {
                removeAllMoviesFromBasket();
                localStorage.removeItem('basketCtx');
                history.push('/biblioteka')
            }, 3000)

        } catch (e) {
            setError(true)
        }
    };

    return (
        <Step key={"podsumowanie"} {...other}>
            <StepLabel>Podsumowanie</StepLabel>
            <StepContent>
                <TableContainer component={Paper}>
                    <Table className={classes.table} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell>Tytuł</TableCell>
                                <TableCell align="right">Rok</TableCell>
                                <TableCell align="right">Cena</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {basketMovies.map(movie => (
                                <TableRow key={movie.movieId}>
                                    <TableCell className={classes.movieTitle}  component="th" scope="row">
                                        {movie.title}
                                    </TableCell>
                                    <TableCell align="right">{movie.publicationDate}</TableCell>
                                    <TableCell align="right">
                                        <Chip className={classes.price} label={`${movie.price} zł`} />
                                    </TableCell>
                                </TableRow>
                            ))}
                            <TableRow>
                                <TableCell colSpan={3} align="right">Razem</TableCell>
                                <TableCell align="right">
                                    <Chip color="secondary" className={classes.summaryPrice}  size="large" label={`${basketPrice} zł`} />
                                </TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>


                <div className={classes.actionsContainer}>
                    <div>
                        <Button
                            onClick={handleBack}
                            className={classes.button}
                        >
                            Wróć
                        </Button>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={makeOrderHandler}
                            className={classes.button}
                            disabled={success}
                        >
                            Kup teraz
                        </Button>
                    </div>
                </div>

                { success ? <Alert className={classes.table} variant="filled"  severity="success">Dziękujemy za zakup! Wkrótce zostaniesz przeniesiony do swojej biblioteki</Alert> : null }
                { error ? <Alert className={classes.table} variant="filled"  severity="error">Nie udało sie złożyć zamówienia!</Alert> : null }
            </StepContent>
        </Step>
    )
}
