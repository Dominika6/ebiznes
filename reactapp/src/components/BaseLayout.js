import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import MyAppBar from "./MyAppBar";
import MyDrawer from "./MyDrawer";

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex',
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing(3),
    }
}));


export default function BaseLayout({children}) {
    const classes = useStyles();
    return (
        <div className={classes.root}>
            <MyAppBar/>
            <MyDrawer/>
            <main className={classes.content}>
                <Toolbar />
                {children}
            </main>
        </div>
    );
}