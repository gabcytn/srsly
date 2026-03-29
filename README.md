# srsly

**srsly** is a spaced-repetition system (SRS) for LeetCode-style problems — inspired by the SM-2 algorithm used in **Anki**, but designed specifically for long-term retention of coding interview questions.

Instead of endlessly grinding new problems, srsly helps you retain the ones you've already solved. Seriously.

---

## Overview

Most developers solve hundreds of algorithm problems — and forget them weeks later.

srsly tracks your previously solved LeetCode-style questions and schedules intelligent reviews using a spaced-repetition algorithm. The goal is **durable understanding**, not short-term cramming.

This project is inspired by:

- Anki’s SM-2 spaced repetition algorithm  
- Long-term learning principles from cognitive science  

But applied to:
- Data structures & algorithms
- LeetCode-style interview problems
- Pattern recognition over time

---

## Core Philosophy

> Optimize for long-term retention, not interview panic mode.

srsly:
- Tracks solved problems
- Evaluates recall quality
- Dynamically adjusts review intervals
- Encourages deliberate re-attempts
- Reduces redundant problem grinding

---

## Tech Stack

- **Spring Boot** — Backend API
- **PostgreSQL** — Persistent storage
- **Redis** — Caching
- **OpenAI API** — Solution critique & feedback

---

## Key Features

- SM-2 inspired review scheduling
- Adaptive interval recalculation
- Late review reward handling
- Retention-focused design
- AI-powered solution critiques
- Problem history tracking

---

## Target Users

- Developers preparing for technical interviews over months
- Engineers who want to **retain patterns**, not just solve problems
- Learners who value structured review over random grinding

---

## How to run

1. Clone the repository
   ```bash
   git clone https://github.com/gabcytn/srsly.git
   cd srsly
   
2. Configure environment variables
   ```bash
   export SRSLY_FRONTEND_URL=http://localhost:5173
   export DB_ADDRESS=localhost
   export DB_PORT=5432
   export DB_NAME=<your-db>
   export DB_USERNAME=<your-username>
   export DB_PASSWORD=<your-password>
   export JWT_SECRET_KEY=
   export GEMINI_API_KEY=
   ```
   ```bash
   cd src/main/resources
   ```
   - Create postgres DB
   ```bash
   sudo -i -u postgres psql
   ```
   ```postgres
   CREATE DATABASE <your-db>
   ```
   - Put DB username/password in `env`
   - Generate JWT Secret key
   ```bash
   openssl rand -hex 32
   ```
   - Paste output to `env`
   - Generate API key at [AIStudio](https://aistudio.google.com)
   - Paste output to `env`
  
3. Run application
   ```bash
   ./mvnw spring-boot:run

## Links

Access the [API](https://srs-ly.app) or use the [web app](https://client.srs-ly.app)
