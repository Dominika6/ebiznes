import React, {Component} from "react";
import { Nav, Navbar} from 'react-bootstrap';
import {Link} from 'react-router-dom';
import logo from "./logo.svg"

export default class NavigationBar extends Component{

    render(){
        return(
            <Navbar className="App-header" bg="dark" variant="dark">

                <Nav className="mr-auto">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <b><Link to={"/home"} className="App-link">
                        <img src={logo} height={"25px"} alt={"Buy a Movie"}/>
                    </Link></b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/"} className="App-link">Wszystkie filmy</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/gatunek/:filmtypeName"} className="App-link">Gatunek</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/filmy/:movieId"} className="App-link">Film</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/aktor/:actorId"} className="App-link">Aktor</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/rezyser/:directorId"} className="App-link">Reżyser</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/biblioteka"} className="App-link">Biblioteka</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/koszyk"} className="App-link">Koszyk</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <Link to={"/profil"} className="App-link">Moje konto</Link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </Nav>
            </Navbar>
        );
    }
}