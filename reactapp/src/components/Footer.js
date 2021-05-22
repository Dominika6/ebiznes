import React, {Component} from "react";
import {Navbar, Container, Col} from "react-bootstrap";

export default class Footer extends Component{
    render(){
        let fullYear = new Date().getFullYear();
        return(
            <Navbar fixed="bottom" bg="dark" variant="dark" style={{"bottom":0}}>
                <Container className="text-center text-muted">
                    <Col lg={12} >
                        <div>
                            &nbsp;&nbsp;&nbsp;Dominika Jadach, Uniwersytet Jagielloński WFAIS {fullYear}
                        </div>
                    </Col>
                </Container>
            </Navbar>
        )
    }
}