import React, {useEffect, useState} from 'react';
import {useHistory, useParams} from "react-router-dom";
import {directorApi} from "./director.api";
import {movieApi} from "./movie.api";
import MovieGrid from "./MovieGrid";

export default function DirectorMovies() {
    const [movies, setMovies] = useState([]);
    const [director, setDirector] = useState({ firstName: '', surname: ''});
    const urlParams = useParams();
    let history = useHistory();

    useEffect(() => {
        const fetchData = async () => {
            const directorId = urlParams.directorId;
            const _director = await directorApi.get(directorId);

            if (!_director) {
                history.push('/')
            } else {
                let _movies = await movieApi.getForDirector(directorId);
                setDirector(_director);

                setMovies(_movies);
            }
        };

        fetchData();
    }, [urlParams.filmtypeName]);


    return (
        <MovieGrid movies={movies} setMovies={setMovies} title={`Filmy wyreżyserowane przez: ${director.firstName} ${director.surname}`}/>
    );
}
