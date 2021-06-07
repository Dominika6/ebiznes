import {Grid, Paper} from "@material-ui/core";
import React, {useContext, useEffect, useState} from "react";
import Typography from "@material-ui/core/Typography";
import Rate from '@material-ui/lab/Rating';
import makeStyles from "@material-ui/core/styles/makeStyles";
import {useHistory} from 'react-router-dom';
import Box from "@material-ui/core/Box";
import Divider from "@material-ui/core/Divider";
import Chip from "@material-ui/core/Chip";
import API from "../../src/utils/api/API";
import {movieApi} from "../utils/api/movie.api";
import {UserContext} from "./UserContext";


const useStyles = makeStyles({
    gridItem: {
        backgroundColor: 'transparent',
    },
    movieTitle: {
        marginBlockStart: '0.3em',
        marginBlockEnd: '0.1em'
    },
    moviePublicationDate: {
        color: '#ccc'
    },
    movieDetails: {
        marginBlockStart: '32px'
    },
    link: {
        textDecoration: 'none'
    },
    filmtypes: {
        marginLeft: '16px'
    },
    filmtype: {
        borderRadius: 0,
        marginLeft: '4px'
    },
    actors: {
        marginTop: '16px'
    },
    actor: {
        marginLeft: '8px',
        color: 'white'
    },
    directors: {
        marginTop: '32px'
    },
    director: {
        marginLeft: '8px',
        color: 'white'
    }
});


export default function LibraryItem(props) {
    const movie = props.movie;
    const classes = useStyles();
    const {userCtx} = useContext(UserContext);

    let history = useHistory();
    const [filmtypes, setFilmtypes] = useState([]);
    const [actors, setActors] = useState([]);
    const [directors, setDirectors] = useState([]);
    const [rate, setRate] = useState(0);

    useEffect(() => {
        const fetchData = async () => {
            setFilmtypes(await movieApi.getMovieFilmtype(movie.movieId));
            setActors(await movieApi.getMovieActors(movie.movieId));
            setDirectors(await movieApi.getMovieDirectors(movie.movieId));
            if (userCtx.user) {
                const rates = await movieApi.getMovieRates(movie.movieId);
                const userRates = rates.filter(_rate => _rate.user.userId === userCtx.user.userId);
                if (userRates.length > 0) {
                    setRate(userRates[0].result)
                }
            }
        };

        fetchData();
    }, [movie.movieId, rate, userCtx.user]);

    const handleFilmtypeClick = (filmtypeName) => {
        history.push(`/gatunek/${filmtypeName.toLowerCase()}`);
    };

    const handleActorClick = (actorId) => {
        history.push(`/aktor/${actorId}`);
    };

    const handleDirectorClick = (directorId) => {
        history.push(`/rezyser/${directorId}`);
    };


    const onRateChange = async (event, result) => {
        await API.post('/rates', {
            result: result,
            movie: movie.movieId
        });
        setRate(result)
    };

    return (
        <Grid item xs={12}>
            <Paper elevation={0} className={classes.gridItem}>
                <Grid container spacing={3}>
                    <Grid item xs={6}>
                        <Box>
                            <h1 className={classes.movieTitle}>{movie.title}</h1>
                            <Divider />
                            <Box display="flex" flexDirection="row" justifyContent="space-between">
                                <Box>
                                    <Typography component="span" className={classes.moviePublicationDate} variant="subtitle1">{movie.publicationDate}</Typography>
                                    <Box component="span" className={classes.filmtypes}>
                                        {filmtypes.map(filmtype => (
                                            <Chip size="small" key={filmtype.filmtypeId} onClick={() => handleFilmtypeClick(filmtype.filmtype)} className={classes.filmtype} variant="outlined" color="secondary" label={filmtype.filmtype} />
                                        ))}
                                    </Box>
                                </Box>
                                <Box>
                                    <Rate name={movie.title} onChange={onRateChange} value={rate}  defaultValue={2} max={10} />
                                </Box>
                            </Box>
                            <Box>
                                <Typography className={classes.movieDescription}>{movie.description}</Typography>
                                <Divider />
                                <Box className={classes.directors}>
                                    <Typography component="span" variant="subtitle2">Reżyseria: </Typography>
                                    {directors.map(director => (
                                        <Chip key={director.directorId} onClick={() => handleDirectorClick(director.directorId)} className={classes.director} variant="outlined" label={`${director.firstName} ${director.surname}`} />
                                    ))}
                                </Box>
                                <Box className={classes.actors}>
                                    <Typography component="span" variant="subtitle2">Obsada: </Typography>
                                    {actors.map(actor => (
                                        <Chip key={actor.actorId} onClick={() => handleActorClick(actor.actorId)} className={classes.actor} variant="outlined" label={`${actor.firstName} ${actor.surname}`} />
                                    ))}
                                </Box>
                            </Box>
                        </Box>
                    </Grid>
                </Grid>
            </Paper>
        </Grid>
    )

}
