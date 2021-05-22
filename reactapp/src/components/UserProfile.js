import React, {useContext, useEffect, useState} from "react";
import {orderApi} from "./order.api";
import UserOrders from "./UserOrders";
import Grid from "@material-ui/core/Grid";
import EditUser from "./EditUser";
import UserInfo from "./UserInfo";
import {UserContext} from "./UserContext";
import {useHistory} from "react-router-dom";

export default function UserProfile() {
    const [orders, setOrders] = useState([]);
    const {userCtx} = useContext(UserContext);
    let history = useHistory();

    useEffect(() => {
        const fetchData = async () => {
            const _orders = await orderApi.getUserOrders();
            setOrders(_orders);
        };

        fetchData();
    }, []);

    if (!userCtx.user) {
        history.push('/')
        return (<></>)
    } else {
        return (
            <>
                <Grid container direction="row" spacing={4}>
                    <Grid item xs={7}>
                        <UserOrders orders={orders}/>
                    </Grid>
                    <Grid item xs={3}>
                        <UserInfo/>
                        <EditUser/>
                    </Grid>
                </Grid>
            </>
        )
    }

}
