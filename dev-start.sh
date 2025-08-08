#!/bin/bash

echo "� Checking if port 8080 is in use..."
PORT_PID=$(lsof -ti:8080)
if [ ! -z "$PORT_PID" ]; then
    echo "⚠️  Port 8080 is in use by process $PORT_PID. Stopping it..."
    kill -9 $PORT_PID
    echo "✅ Process using port 8080 has been stopped."
else
    echo "✅ Port 8080 is available."
fi

echo "�🛑 Stopping all Docker containers..."
docker-compose down

echo "🐘 Starting PostgreSQL..."
docker-compose up -d postgres

echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 5

echo "🚀 Starting Spring Boot application..."
cd backend && ./mvnw spring-boot:run
