//This code has no function in this AndroidStudio project.
//It is deployed in Parse and it is used in the project to fetch data from the API

const CLASS_POST = "Post";
const CLASS_LIKE = "Like";
const CLASS_COMMENT = "Comment";
const KEY_AUTHOR = "author";
const KEY_POST = "post";

//Retrieve 20 posts with all the info needed to populate the timeline
Parse.Cloud.define("getTimeline", async(request) => {
    let query = new Parse.Query(CLASS_POST);
    query.limit(20);
    query.descending()

    const posts = await query.find();
    let timeline = []

    for (let i = 0; i < posts.length; i++) {
        let object = posts[i];
        //Add extra useful info
        object["isLiked"] = isPostLiked(object.id, request.user.id);
        object["likesCount"] = getLikesCount(object.id);
        object["commentsCount"] = getCommentCount(object.id);
        timeline.push(object);
    }

    return timeline;
});

function isPostLiked(postId, userId) {
    let query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_AUTHOR, userId);
    query.equalTo(KEY_POST, postId);
    let count = query.count();
    return (count == 0 ? false : true);
}

function getLikesCount(postId) {
    let query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_POST, postId);
    return query.count();
}

function getCommentCount(postId) {
    let query = new Parse.Query(CLASS_COMMENT);
    query.equalTo(KEY_POST, postId);
    return query.count();
}
