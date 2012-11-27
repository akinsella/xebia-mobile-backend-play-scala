# Xebia Mobile Backend
##Goals
This backend is a proxy for feeding data from several sources for **Xebia Mobile** application.
It's written with Play! framework 2.0 in Scala.

It collects informations from :

* Github
* Twitter
* Eventbrite
* Xebia french blog

Then it caches the responses and provides information for the Xebia Mobile application throught REST APIs.

It also aims at publishing notifications for registered users for Apple and Android devices.


##Install me
This software needs : 


### Play! Framework 2.0 and Scala compiler
Check and download this url.
Think about installing the Play! Plugin for your favorite IDE.

### Redis 
The cache system uses Redis. You can download the source from the Redis website.


```
HINT: On MacOS, think about installing XCode and Command line tools in order to compile Redis with make command

```

Then you can launch the Redis server with 

	src/redis-server


### Postgres
On MacOS, you can download the Postgres quick App from …

Simply launch the application to get an up and running database. Create a database named 

	xebia-mobile-backend


##APIs key ids
This application feeds data from several public providers thanks to their REST APIs. The more often, you need keys to sign your requests and you are limited in a small number of queries per day.

In order to get a fully working version of this backend, you need your own keys.

### Eventbrite
Go to this URL and suscribe to EventBrite.
Then, set an environment variable with it

	export EVENBRITE_KEY = xxxx

### Twitter
Go to this URL and sign in with your twitter account.
Then, set an environment variable in your system called : 

	export TWITTER_KEY = xxxx




## REST APIs
REST APIs powered by Swagger

