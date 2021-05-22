import {Grid, Paper} from "@material-ui/core";
import React from "react";
import Typography from "@material-ui/core/Typography";
import Rating from '@material-ui/lab/Rating';
import makeStyles from "@material-ui/core/styles/makeStyles";
import {Link} from 'react-router-dom';
import Box from "@material-ui/core/Box";
import Chip from "@material-ui/core/Chip";
import {CheckCircle} from "@material-ui/icons"


const useStyles = makeStyles({
    gridItem: {
      backgroundColor: 'transparent',
        height: '100%',
    },
    movieTitle: {
        marginBlockStart: '0.3em',
        marginBlockEnd: '0.1em'
    },
    moviePublicationDate: {
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
    posterPrice: {
        position: 'absolute',
        top: -16,
        right: 0
    },
    customChip: {
        borderRadius: 0
    }
});


export default function MovieGridItem(props) {
    const movie = props.movie;
    const classes = useStyles();

    const PosterOverlay = () => {
        if (movie.isBought) {
            return (
                <>
                    <Box className={classes.posterOverlay}>
                        <Chip className={classes.customChip} size="small" color="secondary" icon={<CheckCircle />} label={'Kupiony'} />
                    </Box>
                </>
            )
        } else {
            return null
        }
    };

    return (
        <Grid item  xl={2} lg={3} md={4} sm={6} xs={12}>
            <Link className={classes.link} to={{ pathname: `/filmy/${movie.id}`}}>
                <Paper elevation={0} className={classes.gridItem}>
                    <Box className={classes.gridItem} display="flex" flexDirection="column" justifyContent="space-between">
                        <Box className={classes.posterBox} height={"100%"}>
                            <PosterOverlay/>
                        </Box>
                        <Box>
                            <h2 className={classes.movieTitle}>{movie.title}</h2>
                            <Box display="flex" flexDirection="row" justifyContent="space-between">
                                <Box>
                                    <Typography component="span" className={classes.moviepPublicationDate} variant="subtitle2">{movie.publicationDate}</Typography>
                                </Box>
                                <Box>
                                    <Rating name="half-rating-read size-small" defaultValue={movie.rate/2} precision={0.5} size="small" readOnly />
                                </Box>
                            </Box>
                        </Box>
                    </Box>
                </Paper>
            </Link>
        </Grid>
    )

}