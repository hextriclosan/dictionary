# Dictionary Server

## Required env variables

* RSA_PRIVATEKEYCONTENT
* RSA_PUBLICKEYCONTENT
* SECURITY_USER_NAME - username for actuator endpoint
* SECURITY_USER_PASSWORD - password for actuator endpoint
* SPRING_DATA_MONGODB_URI
* GOOGLE_OAUTH2_CLIENT_ID
* GOOGLE_OAUTH2_CLIENT_SECRET

## Run the application

### Dev

1. Specify all required env variables
2. Run the server application with `local` profile (`npm start`)
3. Run the client application with `development` profile

```bash

 docker run --rm \
  -e SPRING_PROFILES_ACTIVE=local \
  -e GOOGLE_OAUTH2_CLIENT_ID=$GOOGLE_OAUTH2_CLIENT_ID \
  -e GOOGLE_OAUTH2_CLIENT_SECRET=$GOOGLE_OAUTH2_CLIENT_SECRET \
  -e RSA_PRIVATEKEYCONTENT=$RSA_PRIVATEKEYCONTENT \
  -e RSA_PUBLICKEYCONTENT=$RSA_PUBLICKEYCONTENT \
  -e SPRING_DATA_MONGODB_URI=$SPRING_DATA_MONGODB_URI \
  -e SECURITY_USER_NAME=$SECURITY_USER_NAME \
  -e SECURITY_USER_PASSWORD=$SECURITY_USER_PASSWORD \
  -p 8080:8080 \
  solomkinmv/dictionary:latest

````

## Generate certificates

```bash
cd src/main/resources/certs
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out public.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
rm keypair.pem
``` 
