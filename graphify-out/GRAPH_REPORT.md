# Graph Report - backend/src/main/java/com/cockroach/crdbcluster  (2026-06-03)

## Corpus Check
- Corpus is ~3,806 words - fits in a single context window. You may not need a graph.

## Summary
- 234 nodes · 270 edges · 36 communities (8 shown, 28 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 43 edges (avg confidence: 0.8)
- Token cost: 68,424 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Auth & JWT Service|Auth & JWT Service]]
- [[_COMMUNITY_Account Domain Model|Account Domain Model]]
- [[_COMMUNITY_Account API Layer|Account API Layer]]
- [[_COMMUNITY_Spring Security Config|Spring Security Config]]
- [[_COMMUNITY_Benchmark Endpoints|Benchmark Endpoints]]
- [[_COMMUNITY_Auth Security Pipeline|Auth Security Pipeline]]
- [[_COMMUNITY_Metrics Configuration|Metrics Configuration]]
- [[_COMMUNITY_Transaction Data Layer|Transaction Data Layer]]
- [[_COMMUNITY_Account Service Logic|Account Service Logic]]
- [[_COMMUNITY_Account Controller|Account Controller]]
- [[_COMMUNITY_User Service|User Service]]
- [[_COMMUNITY_Audit Log Service|Audit Log Service]]
- [[_COMMUNITY_User Controller|User Controller]]
- [[_COMMUNITY_Transaction API Layer|Transaction API Layer]]
- [[_COMMUNITY_Auth Controller|Auth Controller]]
- [[_COMMUNITY_Audit Log Controller|Audit Log Controller]]
- [[_COMMUNITY_App Bootstrap|App Bootstrap]]
- [[_COMMUNITY_Audit Log Entity|Audit Log Entity]]
- [[_COMMUNITY_Audit Log Repository|Audit Log Repository]]
- [[_COMMUNITY_Account Repository|Account Repository]]
- [[_COMMUNITY_Transaction Entity|Transaction Entity]]
- [[_COMMUNITY_Audit Response DTO|Audit Response DTO]]
- [[_COMMUNITY_Seed Response DTO|Seed Response DTO]]
- [[_COMMUNITY_Account Status Request|Account Status Request]]
- [[_COMMUNITY_Account Create Request|Account Create Request]]
- [[_COMMUNITY_Account Response DTO|Account Response DTO]]
- [[_COMMUNITY_User Response DTO|User Response DTO]]
- [[_COMMUNITY_User Update Request|User Update Request]]
- [[_COMMUNITY_Transaction Create Request|Transaction Create Request]]
- [[_COMMUNITY_Transaction Response DTO|Transaction Response DTO]]
- [[_COMMUNITY_Register Request|Register Request]]
- [[_COMMUNITY_Login Request|Login Request]]
- [[_COMMUNITY_Auth Response|Auth Response]]
- [[_COMMUNITY_Diagnostics Controllers|Diagnostics Controllers]]
- [[_COMMUNITY_App Entry Point|App Entry Point]]

## God Nodes (most connected - your core abstractions)
1. `User` - 13 edges
2. `MetricsConfig` - 9 edges
3. `AccountService` - 7 edges
4. `SecurityConfig` - 7 edges
5. `JwtService` - 7 edges
6. `AccountController` - 6 edges
7. `UserService` - 6 edges
8. `TransactionService` - 6 edges
9. `BenchmarkController` - 6 edges
10. `SeedService` - 6 edges

## Surprising Connections (you probably didn't know these)
- `AuditLogResponseDTO` --semantically_similar_to--> `AccountResponseDTO`  [INFERRED] [semantically similar]
  backend/src/main/java/com/cockroach/crdbcluster/audit/AuditLogResponseDTO.java → backend/src/main/java/com/cockroach/crdbcluster/account/AccountResponseDTO.java
- `AuditLogResponseDTO` --semantically_similar_to--> `UserResponseDTO`  [INFERRED] [semantically similar]
  backend/src/main/java/com/cockroach/crdbcluster/audit/AuditLogResponseDTO.java → backend/src/main/java/com/cockroach/crdbcluster/user/UserResponseDTO.java
- `AccountCreateRequest` --semantically_similar_to--> `AccountStatusRequest`  [INFERRED] [semantically similar]
  backend/src/main/java/com/cockroach/crdbcluster/account/AccountCreateRequest.java → backend/src/main/java/com/cockroach/crdbcluster/account/AccountStatusRequest.java
- `AccountCreateRequest` --semantically_similar_to--> `UserUpdateRequest`  [INFERRED] [semantically similar]
  backend/src/main/java/com/cockroach/crdbcluster/account/AccountCreateRequest.java → backend/src/main/java/com/cockroach/crdbcluster/user/UserUpdateRequest.java
- `AccountResponseDTO` --semantically_similar_to--> `UserResponseDTO`  [INFERRED] [semantically similar]
  backend/src/main/java/com/cockroach/crdbcluster/account/AccountResponseDTO.java → backend/src/main/java/com/cockroach/crdbcluster/user/UserResponseDTO.java

## Hyperedges (group relationships)
- **Audit Log MVC Triad** — auditlogcontroller_auditlogcontroller, auditlogservice_auditlogservice, auditlogrepository_auditlogrepository [EXTRACTED 1.00]
- **Account MVC Triad** — accountcontroller_accountcontroller, accountservice_accountservice, accountrepository_accountrepository [EXTRACTED 1.00]
- **User MVC Triad** — usercontroller_usercontroller, userservice_userservice, userrepository_userrepository [EXTRACTED 1.00]
- **JWT Authentication Pipeline** — jwtauthenticationfilter_jwtauthenticationfilter, jwtservice_jwtservice, customuserdetailsservice_customuserdetailsservice [EXTRACTED 1.00]
- **Security Filter Chain Configuration** — securityconfig_securityconfig, jwtauthenticationfilter_jwtauthenticationfilter, ratelimitfilter_ratelimitfilter, securityexceptionhandler_securityexceptionhandler [EXTRACTED 1.00]
- **Authentication Flow (Register/Login)** — authcontroller_authcontroller, authservice_authservice, jwtservice_jwtservice [EXTRACTED 1.00]

## Communities (36 total, 28 thin omitted)

### Community 0 - "Auth & JWT Service"
Cohesion: 0.11
Nodes (5): AuthService, JwtService, User, UserRepository, UserDetails

### Community 1 - "Account Domain Model"
Cohesion: 0.09
Nodes (5): Account, StatusController, SeedController, SeedService, TransactionController

### Community 2 - "Account API Layer"
Cohesion: 0.18
Nodes (21): Account, AccountController, AccountCreateRequest, AccountRepository, AccountResponseDTO, AccountService, AccountStatusRequest, AuditLog (+13 more)

### Community 3 - "Spring Security Config"
Cohesion: 0.16
Nodes (6): AccessDeniedHandler, AuthenticationEntryPoint, CustomUserDetailsService, SecurityConfig, SecurityExceptionHandler, UserDetailsService

### Community 4 - "Benchmark Endpoints"
Cohesion: 0.16
Nodes (4): BenchmarkController, OncePerRequestFilter, JwtAuthenticationFilter, RateLimitFilter

### Community 5 - "Auth Security Pipeline"
Cohesion: 0.23
Nodes (13): AuthController, AuthResponse, AuthService, CustomUserDetailsService, JwtAuthenticationFilter, JwtService, LoginRequest, MetricsConfig (+5 more)

### Community 13 - "Transaction API Layer"
Cohesion: 0.6
Nodes (6): Transaction Entity, TransactionController, TransactionCreateRequest, TransactionRepository, TransactionResponseDTO, TransactionService

## Knowledge Gaps
- **18 isolated node(s):** `AuditLogResponseDTO`, `SeedResponse`, `AccountStatusRequest`, `AccountCreateRequest`, `AccountResponseDTO` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **28 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `User` connect `Auth & JWT Service` to `Account Service Logic`, `Account Domain Model`?**
  _High betweenness centrality (0.134) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `User` (e.g. with `.seedAccounts()` and `.createAccount()`) actually correct?**
  _`User` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `AuditLogResponseDTO`, `SeedResponse`, `AccountStatusRequest` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Auth & JWT Service` be split into smaller, more focused modules?**
  _Cohesion score 0.11 - nodes in this community are weakly interconnected._
- **Should `Account Domain Model` be split into smaller, more focused modules?**
  _Cohesion score 0.09 - nodes in this community are weakly interconnected._