import React from 'react';
import Grid from "@material-ui/core/Grid";
import Box from "@material-ui/core/Box";
import makeStyles from "@material-ui/core/styles/makeStyles";

const useStyles = makeStyles(theme => ({
    root: {
        height: "100vh"
    }
}));

export default function AuthLayout({children}) {
    const classes = useStyles();
    return (
        <Box height="100vh">
            <Grid
                className={classes.root}
                container
                direction="row"
                justify="center"
                alignItems="center"
            >
                {children}
            </Grid>
        </Box>

    );
}