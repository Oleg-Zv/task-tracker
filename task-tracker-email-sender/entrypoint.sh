#!/bin/sh
set -e

export SPRING_MAIL_PASSWORD=$(cat /run/secrets/gmail_password)

exec java -jar /app/app.jar