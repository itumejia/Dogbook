# Dogbook

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This is a social network for dogs. In this app dogs owners will be able to create user profiles their dogs. In their profile they will be able to update photos of their dogs and everything they do. Also, each profile will have useful information for other dogs owners, like the breed, age, if the dog is looking for friends and contact information of the owner. This way, dogs will be able to connect with each others (as well as their owners).

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
5. See a detail screen of a user (dog profile) when the username is clicked in a post  
12. A map with _Dogs nearby_
13. Pinch to scale a photo in timeline
14. User can compose a new post

**Optional Nice-to-have Stories**

6. Like posts
7. Comment on posts
8. Follow other accounts
9. Specific timeline feed only for followed accounts
10. Login and registerusing Facebook SDK
11. Share posts to Facebook with SDK 
15. DMs functionality

### 2. Screen Archetypes

* Welcome Screen (select to login or register)
* Login screen
   * 2. Login using username and password
   * 10. Login and register using Facebook SDK
* Register Screen
   * 1. Set up a new account dedicated to a dog.
   * 10. Login and register using Facebook SDK
* Main feed
   * 3. See a timeline feed with posts of other dogs
   * 6. Like posts
   * 13. Pinch to scale a photo in timeline
* Profile Details 
   * 5. See a detail screen of a user (dog profile) when the username is clicked in a post
   * 8. Follow other accounts
* Post Details 
   * 4. See a detail screen of a post when it is clicked.
   * 6. Like posts
   * 7. Comment on posts
   * 11. Share posts to Facebook with SDK 
* Map of Dogs Nearby
   * 12. A map with _Dogs nearby_

* Only followed users feed (Optional)
   * 9. Specific timeline feed only for followed accounts



### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Main feed
* Only followed users feed (Optional)
* Dogs Nearby Map
* My profile

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
* Only followed users feed (Optional)
   * Profile Details
   * Post Details


## Wireframes

Tab Navigation


<img src="https://github.com/itumejia/Dogbook/blob/master/Tab%20Navigation.jpg" width=600>

Flow Navigation


<img src="https://github.com/itumejia/Dogbook/blob/master/Flow%20navigation.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
