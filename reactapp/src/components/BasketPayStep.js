import React, {useContext, useEffect, useState} from "react";
import StepLabel from "@material-ui/core/StepLabel/StepLabel";
import StepContent from "@material-ui/core/StepContent/StepContent";
import Button from "@material-ui/core/Button";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Step from "@material-ui/core/Step/Step";
import {orderApi} from "../utils/api/order.api";
import FormControl from "@material-ui/core/FormControl";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Radio from "@material-ui/core/Radio";
import TextField from "@material-ui/core/TextField";
import {BasketContext} from "./BasketContext";

const useStyles = makeStyles((theme) => ({
    button: {
        marginTop: theme.spacing(1),
        marginRight: theme.spacing(1),
    },
    actionsContainer: {
        marginBottom: theme.spacing(2),
    }
}));

export default function BasketPayStep({handleBack, handleNext, ...other}) {
    const classes = useStyles();
    const [payMethods, setPayMethods] = useState([]);
    const { getBasketPay, setPay } = useContext(BasketContext);
    const [value, setValue] = useState(getBasketPay);

    const handleChange = (event) => {
        setPay(event.target.value);
        setValue(event.target.value);
    };

    useEffect(() => {
        const fetchData = async () => {
            setPayMethods(await orderApi.getPayMethods())
        };
        fetchData();
    }, []);

    return (
        <Step key="platnosc" {...other}>
            <StepLabel>Metoda płatności</StepLabel>
            <StepContent>
                <FormControl component="fieldset">
                    <RadioGroup aria-label="pay" name="gender1" value={value} onChange={handleChange}>
                        {payMethods.map(pay => (
                            <FormControlLabel value={pay.payId} control={<Radio />} label={pay.method} />
                        ))}
                    </RadioGroup>
                    {value === "4" ? <TextField id="filled-basic" label="Wpisz kupon" variant="filled" /> : null }
                </FormControl>

                <div className={classes.actionsContainer}>
                    <Button
                        onClick={handleBack}
                        className={classes.button}
                    >
                        Wróć
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleNext}
                        className={classes.button}
                    >Podsumowanie</Button>
                </div>
            </StepContent>
        </Step>
    )
}
