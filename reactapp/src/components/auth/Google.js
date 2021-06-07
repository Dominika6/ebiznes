import React, {useEffect} from "react";
import queryString from 'query-string';

export default function GoogleAuth(props) {
    useEffect(() => {
        const fetchData = async () => {
            let params = queryString.parse(props.location.search);
            window.addEventListener('message', (event) => {
                if (event.origin !== "http://localhost:3000")
                    return;
                event.source.postMessage(`token___${params.token}`, event.origin)
            }, false)
        };
        fetchData();
    }, []);

    return (
        <h2>Witamy!</h2>
    )
}