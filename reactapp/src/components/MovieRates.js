import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import React from "react";
import Rate from "@material-ui/lab/Rating/Rating";
import {AccountCircle} from "@material-ui/icons";
import * as colors from "@material-ui/core/colors";
import ListItemIcon from "@material-ui/core/ListItemIcon/ListItemIcon";
import * as _ from 'lodash';

export default function MovieRates({rates}) {
    return (
        <>
            <h3>Oceny</h3>
            {_.reverse(rates).map(rate => (
                <ListItem>
                    <ListItemIcon>
                        <AccountCircle fontSize="large" style={{ color: colors.common.white }} />
                    </ListItemIcon>


                    <ListItemText
                        id={rate.rateId}
                        primary={`${rate.user.firstName} ${rate.user.surname}`}
                        secondary={
                            <React.Fragment>
                                <Rate value={rate.result} max={10} size="small" readOnly/>
                            </React.Fragment>
                        }
                    />
                </ListItem>
            ))}
        </>
    )
}