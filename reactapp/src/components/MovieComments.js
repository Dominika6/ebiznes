import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import React, {useEffect, useState} from "react";
import Typography from "@material-ui/core/Typography";
import {movieApi} from "../utils/api/movie.api";
import {AccountCircle} from "@material-ui/icons";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import * as colors from "@material-ui/core/colors";
import * as _ from 'lodash'
import AddMovieComment from "./AddMovieComment";


export default function MovieComments({movieId}) {
    const [comments, setComments] = useState([]);

    const fetchData = async () => {
        let _comments = await movieApi.getMovieComments(movieId);
        setComments(_.reverse(_comments));
    };

    useEffect(() => {
        fetchData();
    }, []);

    if (comments.length === 0) {
        return (
            <>
                <h3>Brak komentarzy</h3>
                <AddMovieComment movieId={movieId} refreshHandler={fetchData}/>
            </>
        )
    } else {
        return (
            <>
                <h3>Komentarze</h3>
                <AddMovieComment movieId={movieId} refreshHandler={fetchData}/>
                {comments.map(comment => (
                    <ListItem>
                        <ListItemIcon>
                            <AccountCircle fontSize="large" style={{ color: colors.common.white }} />
                        </ListItemIcon>

                        <ListItemText
                            id={comment.commentId}
                            primary={`${comment.user.firstName} ${comment.user.surname}`}
                            secondary={
                                <React.Fragment>
                                    <Typography
                                        component="span"
                                        variant="body2"
                                        color="textPrimary"
                                    >{comment.content}</Typography>
                                </React.Fragment>
                            }
                        />
                    </ListItem>
                ))}
            </>
        )
    }
}
