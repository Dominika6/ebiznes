import API from "./API";

class CommentApi {
    async getAll() {
        let comments = [];
        try {
            comments = await API.get('/comments');
            comments = comments.data;
        } catch (e) {
            console.log(e);
        }
        return comments;
    }

    async post(movie, content) {
        if (content.length === 0) {
            throw new Error()
        }
        await API.post('/comments', { movie, content })
    }
}

export const commentApi = new CommentApi();