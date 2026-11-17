# Weather API

A minimal Spring Boot 4 weather service that fetches data from Visual Crossing API with Redis caching.

## Features

- **External API Integration** - Calls Visual Crossing Weather API via `RestClient`
- **Redis Caching** - 12-hour TTL with cache-aside pattern

## Tech Stack

- Java 17, Spring Boot 4.1.2
- `spring-boot-starter-data-redis` - Redis caching

## Quick Start

### Prerequisites
- Java 17+
- Docker (for Redis)
- Visual Crossing API key (free at [visualcrossing.com](https://www.visualcrossing.com/weather-api))

### Setup

```bash
# 1. Clone & enter
git clone https://github.com/aayambhatt/external-weather-api.git
cd external-weather-api

# 2. Add API key
echo "WEATHER_API_KEY=your_key_here" > .env

# 3. Start Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 4. Run app
./mvnw spring-boot:run
