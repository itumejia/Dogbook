# Dogbook

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)
3. [FBU App Expectations](#FBU-Expectations-Checklist)

## Overview
### Description
This is a social network for dogs. In this app dogs owners will be able to create user profiles their dogs. In their profile they will be able to update photos of their dogs and everything they do. Also, each profile will have useful information for other dogs owners, like the breed, age, if the dog is looking for friends and contact information of the owner. This way, dogs will be able to connect with each others (as well as their owners). It also offers a nice way to connect with other people, the _Dogs Nearby Map_, which will have markers of posts that have a location attached, so that the users can find other users that are geographically close.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social Networking
- **Mobile:** The mobile app would be the only way to access this social network (for now), here the users will have all the available features.
- **Story:** The users will be able to keep all the photos of their dogs along with the stories of those photos in one place. Also they will be able to connect with other users to set up dates to play with other dogs.
- **Market:** All dog owners love their dogs, but they also love other dogs. They will love to set up a social network account just for their dogs and to engage with other dogs and their owners.
- **Habit:** Every time an owner and its dog go out for adventure (go for a walk, travelling, etc), the owner will want to uoload a photo of that. Also, whenever they want to find new friends for their dogs, they will visit this app.
- **Scope:** The app will start focusing in attracting dog owners to create nice profiles for their dogs and in creating ways in which the users will be able to connect with each other.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

1. Set up a new account dedicated to a dog.
2. Login using username and password
3. See a timeline feed with posts of other dogs
4. See a detail screen of a post when it is clicked.
5. A map with _Dogs nearby_
6. User can compose a new post
7. A ViewPager in the main activity to change the tabs by swiping horizontally
8. Pull to refresh gesture/animation
9. Animation when going from Login/Register to the main activity
10. The posts in the map will be fetched from the database according to the currently visible map´s area

**Optional Nice-to-have Stories**

11. See a user detail screen of the curren user
12. See a detail screen of a user (dog profile) when the username is clicked in a post  
13. Like posts
14. Comment on posts
15. Load comments on the Post details view
16. Follow other accounts
17. Specific timeline feed only for followed accounts
18. Login and registerusing Facebook SDK
19. Share posts to Facebook with SDK
20. Pinch to scale a photo in timeline 
21. DMs functionality
22. Improve horizontal orientation layout
23. Infinite scrolling
24. The map will cluster the markers when zooming out

### 2. Screen Archetypes

* Login screen
   * Login using username and password
   * Login and register using Facebook SDK
* Register Screen
   * Set up a new account dedicated to a dog.
   * Login and register using Facebook SDK
* Main feed
   * See a timeline feed with posts of other dogs
   * Like posts
   * Pinch to scale a photo in timeline
* Profile Details 
   * See a detail screen of a user (dog profile) when the username is clicked in a post
   * Follow other accounts
* Post Details 
   * See a detail screen of a post when it is clicked.
   * Like posts
   * Comment on posts
   * Share posts to Facebook with SDK 
* Map of Dogs Nearby
   * A map with _Dogs nearby_

* Only followed users feed (Optional)
   * Specific timeline feed only for followed accounts



### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Main feed
* Only followed users feed (Optional)
* Dogs Nearby Map
* My profile (Optional)

**Flow Navigation** (Screen to Screen)

* Welcome screen
   * Login screen
   * Navidation Screen
* Login Screen
   * Main feed
* Register Screen
   * Main feed
* Main Feed
   * Profile Details
   * Post Details
* Dogs Nearby Map
  * Post Details
* Only followed users feed (Optional)
   * Profile Details
   * Post Details


## Wireframes

Tab Navigation


<img src="https://github.com/itumejia/Dogbook/blob/master/Mock%20Ups/Tab%20Navigation.jpg" width=600>

Flow Navigation


<img src="https://github.com/itumejia/Dogbook/blob/master/Mock%20Ups/Flow%20navigation.jpg" width=600>


## Schema 

### Models

Post, Comments, Users, Likes, Follows


Model: Posts

| Property    | Type          | Required | Description                                  |
| ----------- | ------------- | -------- | -------------------------------------------- |
| objectId    | String        | True     | ID of the object (default)                   |
| author      | Pointer<User> | True     | Author of the post, pointer to a User object |
| description | String        | True     | Caption of the post                          |
| photo       | File          | False    | Image of the post                            |
| location    | ?             | False    | Location of the post                         |

Model: User
| Property            | Type    | Required | Description                                  |
| ------------------- | ------- | -------- | -------------------------------------------- |
| objectId            | String  | True     | ID of the object (default)                   |
| username            | String  | True     | Unique username of the user                  |
| password            | String  | True     | Password to authenticate                     |
| ownerName           | String  | True     | Name of the dog's owner                      |
| breed               | String  | True     | Breed of the dog                             |
| birthday            | Date    | True     | Birthday of the dog (to get age)             |
| lookingForPlaymates | Boolean | True     | Is the user looking for playmates?           |
| ownerContact        | String  | False    | Contact information of the owner             |

Model: Comment
| Property | Type          | Required | Description                              |
| -------- | ------------- | -------- | ---------------------------------------- |
| objectId | String        | True     | ID of the object (default)               |
| post     | Pointer<Post> | True     | ID of the post where the comment belongs |
| author   | Pointer<User> | True     | Author of the comment                    |
| content  | String        | True     | Content of the comment                   |


Model: Like
| Property | Type          | Required | Description                           |
| -------- | ------------- | -------- | ------------------------------------- |
| objectId | String        | True     | ID of the object (default)            |
| post     | Pointer<Post> | True     | ID of the post where the like belongs |
| author   | Pointer<User> | True     | Author of the like                    |

Model: Follow
| Property      | Type          | Required | Description                           |
| ------------- | ------------- | -------- | ------------------------------------- |
| objectId      | String        | True     | ID of the object (default)            |
| followedUser  | Pointer<User> | True     | This user is followed by followinUser |
| followingUser | Pointer<User> | True     | This user follows followedUser        |

### Networking
* Register Sceen
   * (Create/POST) Create new user
```
    ParseUser user = new ParseUser();
    user.setUsername(username);
    user.setPassword(password);
    user.put("ownerName", ownerName);
    user.put("breed", breed);
    user.put("birthday", birthday);
    user.put("lookingForPlaymates", lookingForPlaymates);
    user.put("ownerContact", ownerContact);                                

    user.signUpInBackground(new SignUpCallback() {
        @Override
        public void done(ParseException e) {
            //Sign up succeeded
            if (e == null){
                //What goes next
            }
            //Sign up did NOT succeed
            else {
                //TODO: Show specific issues to user with different toasts
                Toast.makeText(SignupActivity.this, "Signup was not possible", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Issue with signup", e);
            }
        }
    });
```
* Main feed
   * (Read/GET) Get all posts 
   ```
    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
    query.setLimit(20);
    query.orderByDescending("createdAt");
    query.include("user");
    query.findInBackground(new FindCallback<Post>() {
        @Override
        public void done(List<Post> objects, ParseException e) {
            //Found posts successfully
            if (e == null){
                //Display posts
            } else {
                Log.e(TAG, "Failed fething posts from Parse", e);
            }
        }
    });
   ```
   * (Read/GET) Get list of posts id liked by the current user
```
ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
query.selectKeys(Arrays.asList("post"));
query.whereEqualTo("author", ParseUser.getCurrentUser());
query.findInBackground(new FindCallback<ParseObject>() {

    @Override
    public void done(List<ParseObject> posts, ParseException e) {

        if (e == null) {
            //Liked Posts in posts
        }

        else {
            Toast.makeText(MainActivity.this, "query error: " + e, Toast.LENGTH_LONG).show();

        }

    }
    });
```
   * (Create/POST) Like a post
  
  ```
ParseObject<Like> entity = new ParseObject(Like.class);

entity.put("post", postId);
entity.put("author", authorId)

// Saves the new object.
// Notice that the SaveCallback is totally optional!
entity.saveInBackground(e -> {
    if (e==null){
        //Save was done
    } else {
        //Something went wrong
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
  ```
   * (Delete) Dislike a post
   ```
    ParseQuery<Like> query = new ParseObject(Like.class);

    query.getInBackground(likeId, (object, e) -> {
        if (e == null) {
            //Object was fetched
            //Deletes the fetched ParseObject from the database
            object.deleteInBackground(e2 -> {
                if(e2==null){
                    Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                }else{
                    //Something went wrong while deleting the Object
                    Toast.makeText(this, "Error: "+e2.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            //Something went wrong
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
   ```
* Compose new post
    * (Create/POST) Compose new post
    ```
    Post post = new Post();
    post.setDescription(description);
    post.setUser(user);
    post.setImage(new ParseFile(photoFile));
    post.setLocation(location);
    post.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null){
                Toast.makeText(getContext(), "Unsuccess to upload the comment", Toast.LENGTH_SHORT).show();
            } else {
                //Post saved successfully
            }
        }
    });
    ```
* Details Post Screen
    * (Read/GET) Get comments of the post 
    ```
    ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
    query.whereEqualTo("post", postId);
    query.findInBackground(new FindCallback<ParseObject>() {

        @Override
        public void done(List<ParseObject> comments, ParseException e) {

            if (e == null) {
                //Comments of posts in comments
            }

            else {
                Toast.makeText(MainActivity.this, "query error: " + e, Toast.LENGTH_LONG).show();

            }

        }
    });
    ``` 
   * (Create/POST) Like a post
   * (Delete) Dislike a post
   * (Create/POST) Compose new comment
   ```
    Comment comment = new Comment();
    comment.setAuthor(ParseUser.getCurrentUser());
    comment.setPost(postId);
    comment.setContent(content;
    comment.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null){
                Toast.makeText(getContext(), "Unsuccess to upload comment", Toast.LENGTH_SHORT).show();
            } else {
                //Comment saved successfully
            }
        }
    });
   ```
* Details Profile Screen
    * (Read/GET) Get posts where author == selected user
   * (Read/GET) Get list of posts liked by the current user
   * (Create/POST) Like a post
   * (Delete) Dislike a post
* Dogs nearby map
    * (Read/GET) Get all posts (maybe only the ones made 24 or less hours ago)
  
  
## FBU Expectations Checklist

- [x] Your app has multiple views.
    *    The app has login, register, timeline, map and post details screens
- [x] Your app interacts with a database (e.g. Parse)
    *    The app uses a Parse database to store users and posts information
- [x] You can log in/log out of your app as a user
    *    The user has to login to access the app.
- [x] You can sign up with a new user profile
    *    The user is able to create a new account from the app
- [x] Your app integrates with at least one SDK (e.g. Google Maps SDK, Facebook SDK) or API (that you didn’t learn about in CodePath)
    *    The app uses the Google Maps SDK
    *    The use of this SDK is different from what was seen in CodePath lab since the implementation of it is different (permissions are managed in a different way) and extra features are included like markers clustering, markers customization and a camera idle listener to load posts according to what is visible on the map.
- [x] Your app uses at least one gesture (e.g. double tap to like, e.g. pinch to scale) 
    *    The ViewPager introduce a gesture of scrolling horizontally to change the tab.
    *    Gestures of the ViewPager in the map fragement were disabled to let the user control the map freely.
    *    The user can pull down in the main feed to refresh.
- [x] Your app uses at least one animation (e.g. fade in/out, e.g. animating a view growing and shrinking)
    *    The pull to refresh gesture comes with an animation while the posts are being fetched.
    *    There is a transition animation when going from the login/register screen to the main activity.
- [x] Your app incorporates at least external library to add visual polish
    *    The app incorporates the Github library android-maps-utils to incorporate markers clustering to the map 
    *    The app incorporates the Github library android-PullRefreshLayout for the pull to refresg gesture/animation
    *    The app incorporates the Github library transition-button-android for the animation from login/register to the main activity
- [x] Your app provides opportunities for you to overcome difficult/ambiguous technical problems (more below)
    * The features that provided opportunities to overcome difficult problems were
        * The algorithm that fetches posts for the map only for a specific area depending on what is visible on the map.
        *    The app has a view pager that allows the user to move between the tabs of the main activity, involvement a special management of the fragments.
        * The markers clustering of the map.

