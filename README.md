# srsly

**srsly** is a spaced-repetition system (SRS) for LeetCode-style problems — inspired by the SM-2 algorithm used in **Anki**, but designed specifically for long-term retention of coding interview questions.

Instead of endlessly grinding new problems, srsly helps you retain the ones you've already solved.

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
- **Redis** — Caching & scheduling optimizations
- **OpenAI API** — Solution critique & feedback

---

## 🔍 Key Features

- SM-2 inspired review scheduling
- Adaptive interval recalculation
- Early review penalty handling
- Retention-focused design
- AI-powered solution critiques
- Problem history tracking

---

## Architecture (High-Level)

- Spring Boot REST API
- Postgres for durable storage
- Redis for refresh token rotations
- Optional AI critique layer for deeper feedback

---

## Target Users

- Developers preparing for technical interviews over months
- Engineers who want to **retain patterns**, not just solve problems
- Learners who value structured review over random grinding

---

## Status

Actively building client-side app.
