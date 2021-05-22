import {React, Component} from "react";
import {Card} from "react-bootstrap";

class Welcome extends Component{
    render(){
        return (
            <Card className={"bg-dark text-white p-5"}>
                <Card.Header>
                    <h2>Witaj w Buy a Movie!</h2><br/>
                    U nas kupisz nie tylko swoje ulubione filmy, ale też nowości.
                </Card.Header>
                <br/>
                <Card.Body>
                    <h3>Masz jakieś pytania? Zapraszamy do kontaktu:</h3>
                    <ul>
                        <li><p>buy-a-movie@krakow.pl</p></li>
                        <li><p>123 456 789</p></li>
                    </ul>
                </Card.Body>
            </Card>
        )
    }
}

export default Welcome;