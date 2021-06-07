// import React from 'react';
// import ReactDOM from 'react-dom';
// import './index.css';
// import App from './App';
// import reportWebVitals from './reportWebVitals';
//
// ReactDOM.render(
//   <React.StrictMode>
//     <App />
//   </React.StrictMode>,
//   document.getElementById('root')
// );
//
// // If you want to start measuring performance in your app, pass a function
// // to log results (for example: reportWebVitals(console.log))
// // or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();



import React from 'react';
import ReactDOM from 'react-dom';
import CssBaseline from '@material-ui/core/CssBaseline';
import {ThemeProvider} from '@material-ui/core/styles';
import theme from './theme';
import {BrowserRouter, Redirect, Switch} from "react-router-dom";
import Movie from "./components/Movie";
import BaseLayout from "./components/BaseLayout";
import AuthLayout from "./components/AuthLayout";
import AppRoute from "./utils/AppRoute";
import Login from "./components/auth/Login";
import SignUp from "./components/auth/Registration";
import UserContextProvider from "./components/UserContextProvider";
import GenreMovies from "./components/FilmtypeMovies";
import UserLibrary from "./components/UserLibrary";
import ActorMovies from "./components/ActorMovies";
// import NotFound from "./layout/NotFound";
import BasketContextProvider from "./components/BasketContextProvider";
import Basket from "./components/Basket";
import UserProfile from "./components/UserProfile";
import DirectorMovies from "./components/DirectorMovies";
import AllMovies from "./components/AllMovies";
import GoogleAuth from "./components/auth/Google";

const routing = (
    <ThemeProvider theme={theme}>
        <CssBaseline/>
        <UserContextProvider>
            <BasketContextProvider>
                <BrowserRouter>
                    <Switch>
                        <AppRoute exact path="/" component={AllMovies} layout={BaseLayout}/>
                        <AppRoute path="/gatunek/:genreName" component={GenreMovies} layout={BaseLayout}/>
                        <AppRoute path="/filmy/:movieId" component={Movie} layout={BaseLayout}/>
                        <AppRoute path="/aktor/:actorId" component={ActorMovies} layout={BaseLayout}/>
                        <AppRoute path="/rezyser/:directorId" component={DirectorMovies} layout={BaseLayout}/>
                        <AppRoute path="/biblioteka" component={UserLibrary} layout={BaseLayout}/>
                        <AppRoute path="/koszyk" component={Basket} layout={BaseLayout}/>
                        <AppRoute path="/profil" component={UserProfile} layout={BaseLayout}/>

                        <AppRoute exact path="/logowanie" component={Login} layout={AuthLayout}/>
                        <AppRoute exact path="/rejestracja" component={SignUp} layout={AuthLayout}/>
                        <AppRoute exact path="/oauth/google" component={GoogleAuth} layout={AuthLayout}/>
                        {/*<AppRoute exact path="/404" component={NotFound} layout={AuthLayout}/>*/}
                        <Redirect to="/404"/>
                    </Switch>
                </BrowserRouter>
            </BasketContextProvider>
        </UserContextProvider>
    </ThemeProvider>
);

ReactDOM.render(routing, document.querySelector('#root'));
