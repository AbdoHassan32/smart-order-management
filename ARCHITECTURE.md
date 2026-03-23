# Architecture Decision Records

## Why microservices over monolith?
This project was built to demonstrate microservices patterns.
For a real business at this scale, a modular monolith would 
be the right starting point. Microservices add operational 
complexity that is only justified at scale.

## Why synchronous communication (Feign) over async (Kafka)?
Synchronous communication was chosen first because:
- Simpler to reason about for the order flow
- Easier to debug and trace
- Order placement requires immediate consistency

Async messaging (Kafka/RabbitMQ) would be added for:
- Notifications (fire and forget)
- Inventory updates after confirmed orders
- Audit logging

## Why database per service?
- Each service owns its data completely
- Services can be deployed, scaled, and migrated independently
- No shared schema coupling between services
- Follows the bounded context principle from DDD

## Why JWT at the gateway only?
- Single validation point — easier to rotate keys
- Business services stay simple — no security boilerplate
- User context forwarded via trusted internal headers
- In production, internal network would be private (VPC)

## Why Resilience4j over Hystrix?
- Hystrix is in maintenance mode
- Resilience4j is the Spring Cloud recommended replacement
- Better integration with Spring Boot 3.x actuator
- Supports annotation-based configuration

## Why Spring Cloud Config with native profile?
- Git-backed config is the production standard
- Native (filesystem) was used for simplicity
- Switching to Git requires only changing one property
- All service configs are in one place for easy review

## Trade-offs made
| Decision | Trade-off |
|---|---|
| No saga pattern | Compensating transactions are manual |
| No event sourcing | No full audit trail of state changes |
| Single Postgres instance | Not truly isolated in Docker setup |
| No service mesh | No mTLS between services |
| Simulated payments | Not production ready without real gateway |
