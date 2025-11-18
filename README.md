# Ecommerce Application

A sample e-commerce backend built with Spring Boot, PostgreSQL, Redis, and Stripe integration.
This project demonstrates JPA, Spring Security, caching, REST API, and payment workflow using Stripe.

Tech Stack
- Java 21
- Spring Boot 3
- PostgreSQL 14
- Redis 6
- Docker & Docker Compose
- Stripe (Test Mode)

Prerequisites
- Docker & Docker Compose installed
- Java 21 installed (if running outside Docker)
- Git

Setup Instructions (Local Development)
1. Clone the repository:
   git clone https://github.com/Mykola-Nahornyi/ecommerce
   cd ecommerce

2. Copy the example environment file:
   cp .env.example .env

3. Edit `.env` with real/test values (optional):
    - `STRIPE_SECRET_KEY` = test/###_sk_test_51SUUfZ4E7JslMVcj5nRAgHHQC8w9z8kq0E0nBd2u7fDnGhiFBGiRrtdLR6KlKrYkqfBimRIMLRqRflICJPexJyHb00jJRUhITv#
    - `STRIPE_PUBLIC_KEY` = test/###_pk_test_51SUUfZ4E7JslMVcjnkqT7PK7kC040Cyo9kmum1H4T2T6YnvbVHfty7ltg1npuAFRoLYHZEwOxmKB7FNzrudtUETA00rx65uo3H#
    - `STRIPE_WEBHOOK_SECRET` = test/###_whsec_o7e9ecq43gKrkVvpEKNC23df8NLJsltv#

4. Start services using Docker Compose:
   docker-compose up -d

   This will start:
    - PostgreSQL on port 5433
    - Redis on port 6379

5. Run the Spring Boot application:
   ./mvnw spring-boot:run
   OR via Docker (if you have a Dockerfile/service for the app):
   docker-compose up app

6. Stripe Webhooks (Local Testing with ngrok)

-Stripe webhooks require a publicly accessible URL so Stripe can send event notifications to your application 
(e.g., payment_intent.succeeded). When running the app locally on localhost, Stripe cannot reach your machine directly.
-To test webhooks locally, use ngrok to create a secure public URL that tunnels to your local server:
-Install ngrok: https://ngrok.com/download
-Start ngrok on your Spring Boot port (default 8080):
    bash:    ngrok http 8080
-Ngrok will provide a URL like https://abcd1234.ngrok.io that forwards requests to your local app.
-Use this URL in the Stripe Dashboard as your webhook endpoint.
-Update .env if your application references the webhook secret or URL for testing.

This way, you can receive and test Stripe webhook events on your local machine without deploying your app to a public server.