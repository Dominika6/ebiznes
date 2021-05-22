import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import React from "react";
import Rating from "@material-ui/lab/Rating/Rating";
import {AccountCircle} from "@material-ui/icons";
import * as colors from "@material-ui/core/colors";
import ListItemIcon from "@material-ui/core/ListItemIcon/ListItemIcon";
import * as _ from 'lodash';

export default function MovieRatings({ratings}) {
    return (
        <>
            <h3>Oceny</h3>
            {_.reverse(ratings).map(rating => (
                <ListItem>
                    <ListItemIcon>
                        <AccountCircle fontSize="large" style={{ color: colors.common.white }} />
                    </ListItemIcon>


                    <ListItemText
                        id={rating.rateId}
                        primary={`${rating.user.firstName} ${rating.user.surname}`}
                        secondary={
                            <React.Fragment>
                                <Rating value={rating.result} max={10} size="small" readOnly/>
                            </React.Fragment>
                        }
                    />
                </ListItem>
            ))}
        </>
    )
}