//This code has no function in this AndroidStudio project.
//It is deployed in Parse and it is used in the project to fetch data from the API

const CLASS_POST = "Post";
const CLASS_LIKE = "Like";
const CLASS_COMMENT = "Comment";
const CLASS_FOLLOW = "Follow";
const KEY_AUTHOR = "author";
const KEY_POST = "post";
const KEY_CREATED_AT = "createdAt";
const KEY_FOLLOWED_USER = "followedUser";
const KEY_FOLLOWING_USER = "followingUser";
const KEY_USERNAME = "username";

const LIKED_BY_USERS_LIMIT = 3;

var followedUsers= []

//Retrieve 20 posts with all the info needed to populate the timeline
Parse.Cloud.define("getTimeline", async(request) => {

    //Get list of posts
    let query = new Parse.Query(CLASS_POST);
    query.limit(20);
    query.descending(KEY_CREATED_AT);
    query.include(KEY_AUTHOR);
    const posts = await query.find();

    //Get list of followed users by the logged in user
    let followedUsers = await getFollowedUsers(request.user);

    let postsWithReactions = [] //Final list with results

    for (let i = 0; i < posts.length; i++) {
        let postWithInfo = {}
        let reactions = {};
        //Add extra useful info to Reactions Object
        reactions["isLiked"] = await isPostLiked(posts[i], request.user);
        reactions["likesCount"] = await getLikesCount(posts[i]);
        reactions["commentsCount"] = await getCommentCount(posts[i]);
        reactions["likedBy"] = await getLikedByUsers(posts[i], followedUsers, reactions["likesCount"]);

        //Build item that will be in final list
        postWithInfo["post"] = posts[i];
        postWithInfo["reactions"] = reactions;

        postsWithReactions.push(postWithInfo);
    }

    return postsWithReactions;
});

async function isPostLiked(post, user) {
    let query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_AUTHOR, user);
    query.equalTo(KEY_POST, post);
    let count = await query.count();
    return count > 0;
}

async function getLikesCount(post) {
    let query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_POST, post);
    let results = await query.find();
    return await query.count();
}

async function getFollowedUsers(user) {
    let followedUsers = []
    let query = new Parse.Query(CLASS_FOLLOW);
    query.equalTo(KEY_FOLLOWING_USER, user);
    query.select(KEY_FOLLOWED_USER);
    query.select(KEY_FOLLOWED_USER + "." + KEY_USERNAME);
    const results = await query.find();
    for (const follow of results) {
        followedUsers.push(follow.get(KEY_FOLLOWED_USER));
    }
    return followedUsers;
}

async function getCommentCount(post) {
    let query = new Parse.Query(CLASS_COMMENT);
    query.equalTo(KEY_POST, post);
    return await query.count();
}

async function getLikedByUsers(post, followedUsers, postLikesCount) {

    //Do not make any fetches if the post does not have any likes
    if (postLikesCount == 0) {
        return [];
    }

    let likedByLimit = postLikesCount > LIKED_BY_USERS_LIMIT ? LIKED_BY_USERS_LIMIT : postLikesCount; //Set the number of users that will be searched
    //If the number of likes of the post is less than the limit, then just look for any likes to fill the array
    if (postLikesCount <= likedByLimit) {
        return getLikedByAnyUsers(post, likedByLimit);
    }

    //Look up for followed users first
    let likedBy = await getLikedByFollowedUsers(post, followedUsers, likedByLimit);

    //If the likes by follow users do not fill the limit, look up for any users to fill it
    if (likedBy.length < likedByLimit) {
        let anyUsers = await getLikedByAnyUsers(post, likedByLimit - likedBy.length);
        likedBy = likedBy.concat(anyUsers);
    }
    return likedBy;
}

//Get any likes of a post
async function getLikedByAnyUsers(post, limit) {
    query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_POST, post);
    query.select(KEY_AUTHOR);
    query.select(KEY_AUTHOR + "." + KEY_USERNAME);
    query.limit(limit);

    return await query.find();

}

//Get likes from post only from followed users
async function getLikedByFollowedUsers(post, followedUsers, limit) {
    let query = new Parse.Query(CLASS_LIKE);
    query.equalTo(KEY_POST, post);
    query.containedIn(KEY_AUTHOR, followedUsers);
    query.select(KEY_AUTHOR);
    query.select(KEY_AUTHOR + "." + KEY_USERNAME);
    query.limit(limit);
    return await query.find();
}