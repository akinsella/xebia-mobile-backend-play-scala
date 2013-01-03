xebia-mobile-backend-play-scala
===============================

This file will be packaged with your application, when using `play dist`.

=======

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
Check and download the last version of 2.0 Play Framework version [here](http://download.playframework.org/releases/play-2.0.4.zip)

Think about installing the Play! Plugin for your favorite IDE.

### Redis 
The cache system uses Redis. You can download the source from the [Redis website](http://redis.io/).


```
HINT: On MacOS, think about installing XCode and Command line tools in order to compile Redis with make command

```

Then you can launch the Redis server with 

	src/redis-server


### Postgres
On MacOS, you can download the Postgres quick App from [here](http://postgresapp.com/).

Simply launch the application to get an up and running database. Create a database named 

	xebia-mobile-backend


##APIs key ids
This application feeds data from several public providers thanks to their REST APIs. The more often, you need keys to sign your requests and you are limited in a small number of queries per day.

In order to get a fully working version of this backend, you need your own keys.

### Eventbrite
Go to [the Eventbrite website for developper](https://www.eventbrite.com/api/key/) and suscribe to EventBrite to get your own key.
Then, set an environment variable with it

	export API_EVENTBRITE_APP_KEY = xxxx

### Twitter
In the same manner, go to [Dev Twitter website](https://dev.twitter.com/apps/new) and sign in with your twitter account. Declare your new app. Register a token for your account then, get the key XXX and set and environment variable for it : 

	export API_TWITTER_APP_KEY = XXX
	export API_TWITTER_APP_SECRET = XXX


## REST APIs
REST APIs will be powered by [Swagger](http://swagger.wordnik.com/)
Swagger provides a set of annotations to be placed in the REST controllers.
The framework has its own controller that generates JSON description your the application REST APIs. 
To get this controller, you need to clone Swagger from the [Github repo](https://github.com/wordnik/swagger-core).
Then publish in local the modules *swagger-play2-utils*

	git clone https://github.com/wordnik/swagger-core.git
	
	cd modules/swagger-play2-utils

	play publish-local


Start the server, then go to the URL **/api**. You can parse the content of your API and try it from live!

Run the application in dev mode:

play debug -Djava.util.logging.config.file=logging.properties run
