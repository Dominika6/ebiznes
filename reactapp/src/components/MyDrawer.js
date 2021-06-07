import Toolbar from "@material-ui/core/Toolbar";
import {NavLink} from "react-router-dom";
import React, {useContext, useEffect, useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon/ListItemIcon";
import {AccountBox, Apps, ChevronRight, ShoppingCart, VideoLibrary} from "@material-ui/icons";
import * as colors from "@material-ui/core/colors";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import Divider from "@material-ui/core/Divider";
import ListSubheader from "@material-ui/core/ListSubheader";
import Drawer from "@material-ui/core/Drawer/Drawer";
import {filmtypeApi} from "../utils/api/filmtype.api";
import {UserContext} from "./UserContext";

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
    drawer: {
        width: drawerWidth,
        flexShrink: 0,
    },
    drawerPaper: {
        width: drawerWidth,
        borderRight: 'none'
    },
    drawerContainer: {
        overflow: 'auto',
    },
    drawerSubtitle: {
        color: '#9f9f9f',
        marginBlockEnd: 0
    },
    title: {
        flexGrow: 1
    }
}));


export default function MyDrawer() {
    const classes = useStyles();
    const [filmtypes, setFilmtypes] = useState([]);
    const {userCtx} = useContext(UserContext);

    useEffect(() => {
        const fetchData = async () => {
            const _filmtypes = await filmtypeApi.getAll();
            setFilmtypes(_filmtypes);
        };

        fetchData();
    }, []);

    const UserListPanel = () => {
        if (userCtx.user) {
            return (
                <>
                    <Divider />
                    <List>
                        <ListSubheader component="div" >
                            <h4 className={classes.drawerSubtitle}>Moje konto</h4>
                        </ListSubheader>
                        <ListItem button component={NavLink} exact={true} to="/profil" >
                            <ListItemIcon><AccountBox style={{ color: colors.common.white }}/></ListItemIcon>
                            <ListItemText primary="Profil" />
                        </ListItem>
                        <ListItem button component={NavLink} exact={true} to="/biblioteka">
                            <ListItemIcon><VideoLibrary style={{ color: colors.common.white }}/></ListItemIcon>
                            <ListItemText primary="Biblioteka" />
                        </ListItem>
                        <ListItem button component={NavLink} exact={true} to="/koszyk">
                            <ListItemIcon><ShoppingCart style={{ color: colors.common.white }}/></ListItemIcon>
                            <ListItemText primary="Koszyk" />
                        </ListItem>
                    </List>
                </>
            )
        } else {
            return null
        }
    };


    return (
        <Drawer
            className={classes.drawer}
            variant="permanent"
            classes={{
                paper: classes.drawerPaper,
            }}
        >
            <Toolbar />
            <div className={classes.drawerContainer}>
                <List>
                    <ListItem button component={NavLink} exact={true} to="/" >
                        <ListItemIcon><Apps style={{ color: colors.common.white }}/></ListItemIcon>
                        <ListItemText primary="Home" />
                    </ListItem>
                </List>
                <UserListPanel/>
                <Divider />
                <List>
                    <ListSubheader component="div" >
                        <h4 className={classes.drawerSubtitle}>Gatunki</h4>
                    </ListSubheader>
                    {filmtypes.map((filmtype) => (
                        <ListItem button component={NavLink} to={"/gatunek/" + filmtype.filmtype.toLowerCase()}  key={filmtype.filmtype}>
                            <ListItemIcon><ChevronRight style={{ color: colors.common.white }}/></ListItemIcon>
                            <ListItemText primary={filmtype.filmtype}/>
                        </ListItem>
                    ))}
                </List>
            </div>
        </Drawer>
    )
}
