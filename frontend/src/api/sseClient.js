import { EventSourcePolyfill } from "event-source-polyfill";
import Cookies from "js-cookie";
import { refreshToken } from "./authService";

let eventSource = null;
let isReconnecting = false;

export const connectSSE = () => {
    if (eventSource) return eventSource;

    const token = Cookies.get("accessToken");

    eventSource = new EventSourcePolyfill(
        `${process.env.REACT_APP_SERVER_URL}/api/stream`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            withCredentials: true,
        }
    );

    eventSource.onerror = async () => {

        if (isReconnecting) return;

        if (eventSource.readyState === 2) { // CLOSED
            isReconnecting = true;

            try {
                await refreshToken();

                closeSSE();
                connectSSE(); // reconnect with new token

            } catch (err) {
                closeSSE();
            }

            isReconnecting = false;
        }
    };

    return eventSource;
};

export const closeSSE = () => {
    if (eventSource) {
        eventSource.close();
        eventSource = null;
    }
};