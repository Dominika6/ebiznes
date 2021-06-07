import React, {useEffect, useState} from 'react';
import {useHistory, useParams} from "react-router-dom";
import {actorApi} from "../utils/api/actor.api";
import {movieApi} from "../utils/api/movie.api";
import MovieGrid from "./MovieGrid";

export default function ActorMovies() {
    const [movies, setMovies] = useState([]);
    const [actor, setActor] = useState({ firstName: '', surname: ''});
    const urlParams = useParams();
    let history = useHistory();

    useEffect(() => {
        const fetchData = async () => {
            const actorId = urlParams.actorId;
            const _actor = await actorApi.get(actorId);

            if (!_actor) {
                history.push('/')
            } else {
                let _movies = await movieApi.getForActor(actorId)
                setActor(_actor);
                setMovies(_movies);
            }
        };

        fetchData();
    }, [history, urlParams.actorId, urlParams.filmtype]);

    return (
        <MovieGrid movies={movies} setMovies={setMovies} title={`Filmy z udziałem: ${actor.firstName} ${actor.surname}`} />
    );
}