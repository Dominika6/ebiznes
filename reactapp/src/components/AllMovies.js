import React, {useEffect, useState} from 'react';
import {movieApi} from "./movie.api";
import MovieGrid from "./MovieGrid";

export default function AllMovies() {
    const [movies, setMovies] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            let movies = await movieApi.getAll();
            setMovies(movies);
        };

        fetchData();
    }, []);


    return (
        <MovieGrid movies={movies} setMovies={setMovies}/>
    );
}
