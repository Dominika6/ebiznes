import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import React, {useEffect, useState} from "react";
import {movieApi} from "./movie.api";
import {NavLink} from "react-router-dom";

export default function MovieDirectors({movieId}) {
    const [directors, setDirectors] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            let _directors = await movieApi.getMovieDirectors(movieId);
            setDirectors(_directors);
        };
        fetchData();
    }, [movieId]);
    return (
        <>
            <h3>Reżyseria</h3>
            {directors.map(director => (
                <ListItem button component={NavLink} exact={true} to={`/rezyser/${director.directorId}`}>
                    <ListItemText id={director.directorId} primary={`${director.firstName} ${director.surname}`} />
                </ListItem>
            ))}
        </>
    )

}
