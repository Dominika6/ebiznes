import React, {useEffect, useState} from 'react';
import {useHistory, useParams} from "react-router-dom";
import {genreApi} from "./genre.api";
import {movieApi} from "./movie.api";
import MovieGrid from "./MovieGrid";

export default function GenreMovies() {
    const [movies, setMovies] = useState([]);
    const [gridTitle, setGridTitle] = useState(null);
    const urlParams = useParams();
    let history = useHistory();

    useEffect(() => {
        const fetchData = async () => {
            const genres = await genreApi.getAll();
            const _genre = genres.find(genre => genre.name.toLowerCase() === urlParams.filmtypeName);

            if (!_genre) {
                history.push('/');
                return
            }
            let _movies = await movieApi.getForGenre(_genre.id);
            setGridTitle(_genre.name);

            setMovies(_movies);
        };

        fetchData();
    }, [urlParams.filmtypeName]);


    return (
       <MovieGrid movies={movies} setMovies={setMovies} title={gridTitle} />
    );
}
