### gRPC WEB

As discussed, gRPC for inter-service communication(fast) then what about web communication?
Browser support http2 as downloading resources like images, files, CSS, JScript...etc
No browser API for us to get notifications at client side t JavaScript level.

```
Recommendation;
    
    Browser     -------------->     Application         : Rest APIs
    Application -------------->     Backen Services     : gRPCs

    Browser <----> Aggregator <------> gRPC Application/Service.
  
```

### gRPC Spring Boot application
```
                            |------- user-service ----------------H2(DB)
                            |
                            |
Browser -------- Aggregator-|
                            |
                            |
                            |------- movie-service ---------------H2(DB)

```
### gRPC - Spring Boot Integration

```
I. grp-flix : is maven (pom packaging) project
   It has below modules
    1a) gflix-proto : maven project for IDL
    1b) user-service: spring boot project 
    1c) movie-service: spring boot grpc project 
    1d) aggregator-service: spring boot web project
II) start user-service module.
    2a) Open BloomRPC--> load proto file
    2b) Click on UserService.getUserGenre method
        localhost:6565
    input: {
             "login_id": "ragoru"
           }
    output: {
              "login_id": "ragoru",
              "name": "Ravi",
              "genre": "ACTION"
            }
    2c) Click on UserService.updateUserGenre method
        localhost:6565
    input: {
             "login_id": "ragoru",
             "genre": "LOVE"
           }
    output: {
              "login_id": "ragoru",
              "name": "Ravi",
              "genre": "LOVE"
            }
III) start movie-service module.
    3a) Open BloomRPC--> load proto file
    3b) Click on MovieService.getMovie method
        localhost:7575
    input: {
             "genre": "LOVE"
           }
    output: {
              "movies": [
                {
                  "title": "Raj",
                  "year": 1997,
                  "rating": 1997
                },
                {
                  "title": "HeMan",
                  "year": 1981,
                  "rating": 1981
                },
                {
                  "title": "Baalu",
                  "year": 1974,
                  "rating": 1974
                }
              ]
            }
    
```
IV) start aggregator-service module.
    4) all 3 modules are started
    4a) Open Postman Client
        GET:  localhost:8080/user/ragoru 
        output:
            [
                {
                    "title": "Raj",
                    "year": 1997,
                    "rating": 1997.0
                },
                {
                    "title": "HeMan",
                    "year": 1981,
                    "rating": 1981.0
                },
                {
                    "title": "Baalu",
                    "year": 1974,
                    "rating": 1974.0
                }
            ]
    4b) 
        PUT:    localhost:8080/user
        Select: Body, raw, JSON
        Body:
            {
                "loginId":"ragoru",
                "genre":"LOVE"
            }
        output: 200 OK
        
            