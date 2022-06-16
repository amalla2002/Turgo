Original App Design Project - README Template
===

# Turgo

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app wil allow a user to upload photos, the photo will be marked on a map using the location of were the photo was taken. Other people can evaluate it by giving it a star rating 1-5. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social / Discovery
- **Mobile:** Map services / Camera / Audio
- **Story:** 
- **Market:** The same people that are already uploading their trips and adventures to social media
- **Habit:** Push notification from inactivity, bonus EXP from first post of the week
- **Scope:**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User has their own account
* Take pictures, with description and current location
* View and rate others submitions
* Access Profile screen where submitions are shown

**Optional Nice-to-have Stories**

* EXP is given for uploading and rating, bonus is given for first pic of the week and first rate of the day
* Explore feature, can go to a place and on arrival get EXP (once per week) Same place cannot give EXP twice in a month
* Optional Learn button for the explore feature, this should play an audio summary of the place logged (currently wikipedia summary)
* Push notification when: no uploads in a month, no ratings in a week, and no explores in a month.
* Other profiles can be viewed.
* EXP determines lvl
* lvl milestones unlock background colors.


### 2. Screen Archetypes

* Login/Register
    * User has their own account
* MenuHub
    * Take pictures, with description and current location
    * View and rate others submitions
    * Access Profile screen where submitions are shown
* Upload 
    * Take pictures, with description and current location
* Rate 
    * View and rate others submitions
* Map explore
    * View and rate others submitions
* Profile 
    * Access Profile screen where submitions are shown
### 3. Navigation

**Flow Navigation** (Screen to Screen)

* Login/Register
   * MenuHub
* MenuHub
   * Upload 
   * Rate 
   * Map Explore 
   * Profile 
* Upload
    * Take picture act
* Rate
    * Map
* Map exlpore
    * Start adventure
        * On place
* Profile
    * Detail view

## Wireframes
[Scanned Documents (1).pdf](https://github.com/amalla2002/Turgo/files/8902711/Scanned.Documents.1.pdf)


## Schema 

Post object has: 9

objectId 		| String
updatedAt 		| Date
createdAt 		| Date
ACL 			   | ACL
Location 		| GeoPoint 
Picture 			| File
Rates 			| Number
Rating 			| Number
Description 	| String

User object has: 10

objectId 		| String
authData 		| Object
updatedAt 		| Date
createdAt 		| Date
ACL 			   | ACL
emailVerified 	| Boolean 
email 			| String
Avatar 			| File
username 		| String
password 		| String

Map object has: 6

objectId 		| String
updatedAt 		| Date
createdAt 		| Date
ACL 			   | ACL
postLocation	| GeoPoint
Popularity		| Number
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
