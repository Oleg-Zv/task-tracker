#!/bin/sh
set -e

export SPRING_MAILJET_NAME=$(cat /run/secrets/api_key)
export SPRING_MAILJET_PASSWORD=$(cat /run/secrets/secret_key)

exec java -jar /app/app.jar