import API from "./API";

class ActorApi {
    async get(actorId) {
        let actor = null;
        try {
            actor = await API.get(`/actors/${actorId}`);
            actor = actor.data;
        } catch (e) {
            console.log(e);
        }
        return actor;
    }
}

export const actorApi = new ActorApi();