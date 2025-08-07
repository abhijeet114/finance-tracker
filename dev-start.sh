#!/bin/bash

echo "🛑 Stopping all Docker containers..."
docker-compose down

echo "🐘 Starting PostgreSQL..."
docker-compose up -d postgres

echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 10

echo "🚀 Starting Spring Boot application..."
cd backend && ./mvnw spring-boot:run
