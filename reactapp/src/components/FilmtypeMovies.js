import React, {useEffect, useState} from 'react';
import {useHistory, useParams} from "react-router-dom";
import {filmtypeApi} from "../utils/api/filmtype.api";
import {movieApi} from "../utils/api/movie.api";
import MovieGrid from "./MovieGrid";

export default function FilmtypeMovies() {
    const [movies, setMovies] = useState([]);
    const [gridTitle, setGridTitle] = useState(null);
    const urlParams = useParams();
    let history = useHistory();

    useEffect(() => {
        const fetchData = async () => {
            const filmtypes = await filmtypeApi.getAll();
            const _filmtype = filmtypes.find(filmtype => filmtype.filmtype.toLowerCase() === urlParams.filmtypeName);

            if (!_filmtype) {
                history.push('/');
                return
            }
            let _movies = await movieApi.getForFilmtype(_filmtype.id);
            setGridTitle(_filmtype.filmtype);

            setMovies(_movies);
        };

        fetchData();
    }, [history, urlParams.filmtypeName]);


    return (
       <MovieGrid movies={movies} setMovies={setMovies} title={gridTitle} />
    );
}
