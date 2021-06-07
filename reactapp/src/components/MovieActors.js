import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import React, {useEffect, useState} from "react";
import {movieApi} from "../utils/api/movie.api";
import {NavLink} from "react-router-dom";

export default function MovieActors({movieId}) {
    const [actors, setActors] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            let _actors = await movieApi.getMovieActors(movieId);
            setActors(_actors);
        };
        fetchData();
    }, [movieId]);
    return (
        <>
            <h3>Obsada</h3>
            {actors.map(actor => (
                <ListItem button component={NavLink} exact={true} to={`/aktor/${actor.actorId}`}>
                    <ListItemText id={actor.actorId} primary={`${actor.firstName} ${actor.surname}`} />
                </ListItem>
            ))}
        </>
    )
}