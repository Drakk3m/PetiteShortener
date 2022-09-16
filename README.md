# PetiteShortener
---
A little URL Shortener using Spring Boot and Mongo DB

the project will create a new service that will expose 3 endpoint
> __POST__ /petiteshortener will create a shortened URL from a normal URL  
> __POST__ /s/assign will assign an alias to a provided URL  
> __GET__ /s/{shortURL} will search for the shortened URL in the database and will redirect to the associated address  

## Some validations made
- URL is validated for correct structure.
- Entries are verified to be nonexistant in the database before inserting a new one.
- URLs are verified before creating a new shortened identifier to prevent duplications

## Shortener method used
There are several ways to create a Shortener. 
1. We could generate random characters and create a dictionary associating a URL with its randomly generated string.
2. We could deconstitute the URL into base64 using mathematical processing

For better understanding and using most of the capabilities needed, in this project we'll use the first option, so we can experiment a little with Java's JPA and... well... MongoDB
