import React from "react";
import Grid from "@material-ui/core/Grid";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell/TableCell";
import TableBody from "@material-ui/core/TableBody";
import Chip from "@material-ui/core/Chip";
import TableContainer from "@material-ui/core/TableContainer";
import makeStyles from "@material-ui/core/styles/makeStyles";

const useStyles = makeStyles((theme) => ({
    button: {
        marginTop: theme.spacing(1),
        marginRight: theme.spacing(1),
    },
    actionsContainer: {
        marginBottom: theme.spacing(2),
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
    },
    movieImg: {
        width: '70px'
    }
}));


export default function UserOrders({orders}) {
    const classes = useStyles();

    return (
        <>
            <h3>Historia zakupów</h3>

            <Grid container direction="row" spacing={4}>
                {orders.map(order => (
                    <Grid item xs={12}>
                        <Card >
                            <CardHeader
                                title={`Zamówienie #${order.orderId}`}
                            />
                            <CardContent>
                                <TableContainer>
                                    <Table className={classes.table} aria-label="simple table">
                                        <TableHead>
                                            <TableRow>
                                                <TableCell>Tytuł</TableCell>
                                                <TableCell align="right">Rok</TableCell>
                                                <TableCell align="right">Cena</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {order.items.map(item => (
                                                <TableRow key={item.movie.movieId}>
                                                    <TableCell className={classes.movieTitle}  component="th" scope="row">
                                                        {item.movie.title}
                                                    </TableCell>
                                                    <TableCell align="right">{item.movie.publicationDate}</TableCell>
                                                    <TableCell align="right">
                                                        <Chip className={classes.price} label={`${item.price} zł`} />
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                            <TableRow>
                                                <TableCell colSpan={3} align="right">Razem</TableCell>
                                                <TableCell align="right">
                                                    <Chip color="secondary" className={classes.summaryPrice}  size="large" label={`${order.value.toFixed(2)} zł`} />
                                                </TableCell>
                                            </TableRow>
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}

            </Grid>
        </>
    )

}
