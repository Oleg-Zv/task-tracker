#!/bin/sh
set -e

export SPRING_DATASOURCE_USERNAME=$(cat /run/secrets/db_user)
export SPRING_DATASOURCE_PASSWORD=$(cat /run/secrets/db_password)
export SPRING_DATASOURCE_URL="jdbc:postgresql://database:5432/$(cat /run/secrets/db_name)"


export SPRING_JWT_PRIVATE_KEY=$(cat /run/secrets/jwt_private_key)
export SPRING_JWT_PUBLIC_KEY=$(cat /run/secrets/jwt_public_key)

exec java -jar /app/app.jar
