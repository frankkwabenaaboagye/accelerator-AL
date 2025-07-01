| Step | Technology                        | Purpose                                          |
| ---- | --------------------------------- | ------------------------------------------------ |
| 1    | **Spring Boot**                   | Build microservices                              |
| 2    | **Spring Cloud Eureka Server**    | Service discovery and registration               |
| 3    | **Spring Cloud Config Server**    | Centralized configuration management             |
| 4    | **Spring Cloud Gateway**          | API Gateway for routing and single entry point   |
| 5    | **Spring Cloud OpenFeign**        | Declarative REST clients for inter-service calls |
| 6    | **Spring Security** (basic setup) | Secure microservices (later)                     |
| 7    | **Docker** (optional for local)   | Containerize microservices                       |
| 8    | **JUnit + Mockito**               | Unit and integration testing                     |


| Order | Service Name         | Functionality Summary                              |
| ----- | -------------------- | -------------------------------------------------- |
| 1     | **discovery-server** | Eureka Server for registering services             |
| 2     | **config-server**    | Serve configuration from a Git repo or local files |
| 3     | **api-gateway**      | Route requests to the correct service              |
| 4     | **user-service**     | Manage user data (registration, profiles, auth)    |
| 5     | **book-service**     | Manage books (CRUD operations, catalog)            |
| 6     | **order-service**    | Manage orders (place, update, query)               |


Links [Separate Repos]
- [https://github.com/frankkwabenaaboagye/eureka-server](https://github.com/frankkwabenaaboagye/eureka-server)
- [https://github.com/frankkwabenaaboagye/configserver](https://github.com/frankkwabenaaboagye/configserver)
- [https://github.com/frankkwabenaaboagye/gateway-server](https://github.com/frankkwabenaaboagye/gateway-server)
- [https://github.com/frankkwabenaaboagye/userservice](https://github.com/frankkwabenaaboagye/userservice)
- [https://github.com/frankkwabenaaboagye/bookservice](https://github.com/frankkwabenaaboagye/bookservice)
