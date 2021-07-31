//This code has no function in this AndroidStudio project.
//It is deployed in Parse and it is used in the project to fetch data from the API

const CLASS_POST = "Post";
const CLASS_LIKE = "Like";
const CLASS_COMMENT = "Comment";
const KEY_AUTHOR = "author";
const KEY_POST = "post";
const KEY_CREATED_AT = "createdAt";

//Retrieve 20 posts with all the info needed to populate the timeline
Parse.Cloud.define("getTimeline", async(request) => {
    let query = new Parse.Query(CLASS_POST);
    query.limit(20);
    query.descending(KEY_CREATED_AT);
    query.include(KEY_AUTHOR);

    const posts = await query.find();
    let reactions = []

    for (let i = 0; i < posts.length; i++) {
        let object = {};
        //Add extra useful info
        object["isLiked"] = await isPostLiked(posts[i], request.user);
        object["likesCount"] = await getLikesCount(posts[i]);
        object["commentsCount"] = await getCommentCount(posts[i]);
        reactions.push(object);
    }

    return {posts, reactions};
});

async function isPostLiked(post, user) {
    let query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_AUTHOR, user);
    query.equalTo(KEY_POST, post);
    let count = await query.count();
    return (count == 0 ? false : true);
}

async function getLikesCount(post) {
    let query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_POST, post);
    let results = await query.find();
    return await query.count();
}

async function getCommentCount(post) {
    let query = new Parse.Query(CLASS_COMMENT);
    query.equalTo(KEY_POST, post);
    return await query.count();
}
