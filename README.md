# covid
## Purpose 
1- collect covid data from available source and allow CRUD operations 
2- implement REST end point for CRUD operations 

## How to run this project 
1- ./mvnw spring-boot:run
2- GET http://localhost:8080/load/today (less than 1 minutes)
3- GET http://localhost:8080/load/sevendays (could take a while depending on the CPU)

## Available End Point 
GET http://localhost:8080/data/county/{county}/state/{state}/date/{mm-dd-yyyy}
GET http://localhost:8080/data/state/{state}/date/{mm-dd-yyyy}
GET http://localhost:8080/data/sevendays/county/{county}/state/{state}
GET http://localhost:8080/data/sevendays/state/{state}

