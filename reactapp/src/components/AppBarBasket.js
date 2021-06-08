import React, {useContext, useEffect, useState} from "react";
import Badge from "@material-ui/core/Badge";
import {ShoppingCart} from "@material-ui/icons";
import IconButton from "@material-ui/core/IconButton";
import {BasketContext} from "./BasketContext";
import Menu from "@material-ui/core/Menu";
import {withStyles} from "@material-ui/styles";
import ListItemText from "@material-ui/core/ListItemText";
import {movieApi} from "../utils/api/movie.api";
import ListItem from "@material-ui/core/ListItem";
import Box from "@material-ui/core/Box";
import Chip from "@material-ui/core/Chip";
import {NavLink} from "react-router-dom";

const StyledMenu = withStyles({

})((props) => (
    <Menu
        elevation={0}
        getContentAnchorEl={null}
        anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'left',
        }}
        transformOrigin={{
            vertical: 'top',
            horizontal: 'center',
        }}
        {...props}
    />
));

export default function AppBarBasket() {
    const {getBasketMovies} = useContext(BasketContext);
    const basketSize = getBasketMovies.length || 0;
    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);
    const [basketMovies, setBasketMovies] = useState([]);
    const [basketPrice, setBasketPrice] = useState(0);

    useEffect(() => {
        const fetchData = async () => {
            let movies = [];
            for (let movieId of getBasketMovies) {
                movies.push(await movieApi.getById(movieId))
            }
            let sum = 0.0;
            for (let movie of movies) {
                sum += +(movie.price);
            }
            setBasketPrice(sum.toFixed(2));
            setBasketMovies(movies);
        };
        fetchData();
    }, [getBasketMovies]);

    const handleMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    return (
        <>
            <IconButton aria-controls="customized-menu"
                        aria-haspopup="true"
                        onClick={handleMenu} color="inherit">
                <Badge badgeContent={basketSize} color="secondary">
                    <ShoppingCart />
                </Badge>
            </IconButton>

            <StyledMenu
                id="customized-menu"
                anchorEl={anchorEl}
                keepMounted
                open={open}
                onClose={handleClose}
            >
                {basketMovies.map(movie => (
                    <ListItem style={{width: '320px'}} key={movie.id} role={undefined} dense button component={NavLink} exact={true} to={`/filmy/${movie.id}`} >
                        <ListItemText primary={movie.title} />
                        <Box ml={4}>
                            <Chip color="secondary" label={`${movie.price} zł`} />
                        </Box>
                    </ListItem>
                ))}
                <ListItem style={{width: '320px'}} role={undefined} dense button component={NavLink} exact={true} to={`/koszyk`}>

                    <ListItemText primary="Podsumowanie"/>
                    <Chip color="primary" label={`${basketPrice} zł`} />
                </ListItem>
            </StyledMenu>
        </>
    )
}
