#!/bin/bash

# Dynamic backend startup script
# Usage: ./start-backend.sh [PORT]

# Default port
DEFAULT_PORT=8080

# Get port from argument or environment variable
PORT=${1:-${SERVER_PORT:-$DEFAULT_PORT}}

echo "Starting HansaFlex Backend on port $PORT..."

# Set the port as environment variable
export SERVER_PORT=$PORT

# Start the Spring Boot application
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=$PORT"

