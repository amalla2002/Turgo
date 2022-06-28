Outdated
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
You can look up a place, go (with directions) and here a description of the place. You can see a list of parks and how many registered users are in the park. A filter will be in place in case you do not want to go to a place with too many people.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Discovery
- **Mobile:** Map services / Audio
- **Story:** 
- **Market:** 
- **Habit:** Push notification from inactivity
- **Scope:**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User has their own account
* Can search up a place and listen to a brief description
* Can see parks and filter

**Optional Nice-to-have Stories**

* Can see flights, being able to select a range of dates for departure, return and how many days you want your trip to be. Cheapest flight will be presented

### 2. Screen Archetypes

* Login/Register
    * User has their own account
* MenuHub
    * Switch tabs
* Places 
    * Can search up places, go to them and listen to description.
* Parks 
    * Can see parks and filter by occupancy
* Profile 
    * Can logout
### 3. Navigation

**Tab Navigation** (Screen to Screen)

* Login/Register
   * MenuHub
* MenuHub
   * Profile 
   * Places 
   * Parks 
   * Flights 
  

## Wireframes
[Scanned Documents (1).pdf](https://github.com/amalla2002/Turgo/files/8902711/Scanned.Documents.1.pdf)


## Schema 


### Models


City

| Property        | Type          | Description |
| ------------- | ------------- | ------------- |
| objectId  | String  | The id of the object |
| updatedAt  | Date  | Date of last update |
| createdAt  | Date | Date of creation |
| ACL | ACL | ACL? |
| location  | String  | Name of the city |
| parks  | Array  | Pointers to Park |


User 

| Property        | Type          | Description |
| ------------- | ------------- | ------------|
| objectId  | String  | The id of the object |
| authData  | Object  | Authorization data |
| updatedAt  | Date | Date of last update |
| createdAt | Date | Date of creation |
| ACL  | ACL  | ACL? |
| emailVerified  | Boolean  | Verification status |
| email  | String | Email address |
| Avatar | File | Profile image |
| username  | String  | Nickname of the user |
| password  | String  | User key to access account |


Park 

| Property        | Type          | Description |
| ------------- | ------------- | ----------- |
| objectId  | String  | The id of the object |
| updatedAt  | Date | Date of last update |
| createdAt | Date | Date of creation |
| ACL  | ACL  | ACL? |
| name  | String  | Park name |
| longitude  | Number | Longitude if provided |
| latitude  | Number | Latitude if provided |
| hours  | String | Schedule if provided |
| npeople | Number | Current number of Users in Park |


### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
