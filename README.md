# Turgo

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
You can look up a place, go (with directions) and hear a description of the place. 
You can see a list of parks and how many registered users are in the park. 
A filter is in place in case you do not want to go to a place with too many people. 
You can also get directions to a desired park in this screen.
You can also opt into notifications, getting one every time a park is decently crowded.
You can find the cheapest combination for a trip.
Select the range of days on which you are willing to go and return, the amount of days you want to spend and the hotel on which you want to stay.

Demo: https://drive.google.com/file/d/16Se7EVXdUvJYx5xvcN4Gpjk0xcUgNo4V/view?usp=sharing

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User has their own account
* Can search up a place and listen to a brief description
* Can see parks, filter and get directions

**Optional Nice-to-have Stories**

* Can select airports along with a range of dates for arrival, departure and how many days you want your trip to be, along with the hotel. Cheapest flight will be presented
* Can opt into notifications
* Gets suggestion for notification setting, based on data from previous users

### 2. Screen Archetypes

* Login/Register
    * User has their own account
    * Can opt into notifications
* MenuHub
    * Switch tabs
* Places 
    * Can search up places, go to them and listen to a brief description
* Parks 
    * Can see parks and filter by occupancy and get directions
* Flights 
    * Can select airports along with a range of dates for arrival, departure and how many days you want your trip to be, along with the hotel. Cheapest flight will be presented
* Profile 
    * Can logout
### 3. Navigation

**Tab Navigation** 

* Login/Register
   * MenuHub
* MenuHub
   * Profile 
   * Places 
   * Parks
  

## Wireframes
(Outdated)
[Scanned Documents (1).pdf](https://github.com/amalla2002/Turgo/files/8902711/Scanned.Documents.1.pdf)

## Schema 


### Models


City

| Property        | Type          | Description |
| ------------- | ------------- | ------------- |
| objectId  | String  | The id of the object |
| updatedAt  | Date  | Date of last update |
| createdAt  | Date | Date of creation |
| ACL | ACL | Access Control List |
| location  | String  | Name of the city |
| parks  | Array  | Park names |
| tree | Array | Min Seg Tree with how many people are in each park |
| latitude  | Array  | latitude for each park, default value 0 |
| longitude | Array | longitudes for each park, default value 0 |
| hours  | Array  | operational hours, defalut value "Hours not provided" |

User 

| Property        | Type          | Description |
| ------------- | ------------- | ------------|
| objectId  | String  | The id of the object |
| authData  | Object  | Authorization data |
| updatedAt  | Date | Date of last update |
| createdAt | Date | Date of creation |
| ACL  | ACL  | Access Control List |
| emailVerified  | Boolean  | Verification status |
| email  | String | Email address |
| username  | String  | Nickname of the user |
| password  | String  | User key to access account |

MLData

| Property        | Type          | Description |
| ------------- | ------------- | ------------|
| objectId  | String  | The id of the object |
| authData  | Object  | Authorization data |
| updatedAt  | Date | Date of last update |
| createdAt | Date | Date of creation |
| ACL  | ACL  | Access Control List |
| ages  | Array  | Integer array containing user ages at time of register |
| choices  | Array | Integer array containing user's decision to opt into push notification (1 means the user opt in) |

### Networking
- Google Maps Directions API
- Google Maps Places SDK Place Autocomplete
- Seattle Department of Parks and Recreation API
- Amadeus API (Hotel list, Hotel offers, Flight offers)
