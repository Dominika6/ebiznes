import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import React, {useContext} from "react";
import makeStyles from "@material-ui/core/styles/makeStyles";
import {Paper} from "@material-ui/core";
import Box from "@material-ui/core/Box";
import Chip from "@material-ui/core/Chip";
import {CheckCircle} from "@material-ui/icons";
import DeleteIcon from '@material-ui/icons/Delete';
import {BasketContext} from "./BasketContext";

const useStyles = makeStyles({
    gridItem: {
        backgroundColor: 'transparent',
        height: '100%',
    },
    movieTitle: {
        marginBlockStart: '0.3em',
        marginBlockEnd: '0.1em'
    },
    movieProductionYear: {
        color: '#ccc'
    },
    link: {
        textDecoration: 'none'
    },
    posterBox: {
        position: 'relative'
    },
    posterOverlay: {
        position: 'absolute',
        top: 8,
        left: 8
    },
    removeButton: {
        position: 'absolute',
        top: 8,
        right: 8
    },
    customChip: {
        borderRadius: 0
    }
});

export default function BasketMovieItem({movie}) {
    const { removeMovieFromBasket } = useContext(BasketContext);

    const PosterOverlay = () => {
        if (movie.isBought) {
            return (
                <>
                    <Box className={classes.posterOverlay}>
                        <Chip className={classes.customChip} size="small" color="secondary" icon={<CheckCircle />} label={'W bibliotece'} />

                    </Box>
                    <Box className={classes.removeButton}>
                        <Button
                            size="small"
                            onClick={() => removeMovieFromBasket(movie.id)}
                            variant="contained"
                            color="primary"
                            startIcon={<DeleteIcon />}
                        >
                            Usuń
                        </Button>
                    </Box>
                </>
            )
        } else {
            return (
                <>
                    <Box className={classes.removeButton}>
                        <Button
                            size="small"
                            onClick={() => removeMovieFromBasket(movie.id)}
                            variant="contained"
                            color="primary"
                            startIcon={<DeleteIcon />}
                        >
                            Usuń
                        </Button>
                    </Box>
                </>

            )
        }
    };

    const classes = useStyles();
    return (
        <Paper elevation={0} className={classes.gridItem}>
            <Box className={classes.gridItem} display="flex" flexDirection="column" justifyContent="space-between">
                <Box className={classes.posterBox} height={"100%"}>
                    <PosterOverlay/>
                </Box>
                <Box>
                    <h2 className={classes.movieTitle}>{movie.title}</h2>
                    <Box display="flex" flexDirection="row" justifyContent="space-between">
                        <Box>
                            <Typography component="span" className={classes.movieProductionYear} variant="subtitle2">{movie.publicationDate}</Typography>
                        </Box>
                        <Box>
                            <Typography color={"secondary"} component="span" className={classes.moviePrice} variant="h5">{movie.price} zł</Typography>
                        </Box>
                    </Box>
                </Box>
            </Box>
        </Paper>
    )
}
