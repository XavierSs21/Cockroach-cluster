# Graph Report - load-tests  (2026-06-03)

## Corpus Check
- Corpus is ~336 words - fits in a single context window. You may not need a graph.

## Summary
- 22 nodes · 34 edges · 4 communities (2 shown, 2 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 5 edges (avg confidence: 0.77)
- Token cost: 17,472 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Test Scenarios|Test Scenarios]]
- [[_COMMUNITY_Runner & Auth Pipeline|Runner & Auth Pipeline]]
- [[_COMMUNITY_Auth & Config Core|Auth & Config Core]]
- [[_COMMUNITY_Source Files|Source Files]]

## God Nodes (most connected - your core abstractions)
1. `runAuthenticatedTest - Autocannon Runner` - 10 edges
2. `runAuthenticatedTest()` - 6 edges
3. `getToken - JWT Auth Function` - 4 edges
4. `Read Benchmark Test Invocation` - 4 edges
5. `getToken()` - 3 edges
6. `Failover Benchmark Test Invocation` - 3 edges
7. `TEST_USER` - 2 edges
8. `API Base URL Constant` - 2 edges
9. `Write Benchmark Test Invocation` - 2 edges
10. `DB Ping Benchmark Test Invocation` - 2 edges

## Surprising Connections (you probably didn't know these)
- `runAuthenticatedTest - Autocannon Runner` --conceptually_related_to--> `JWT_TOKEN Environment Variable Fallback`  [INFERRED]
  load-tests/runner.js → load-tests/auth.js
- `runAuthenticatedTest()` --calls--> `getToken()`  [EXTRACTED]
  runner.js → auth.js
- `runAuthenticatedTest - Autocannon Runner` --references--> `API Base URL Constant`  [EXTRACTED]
  load-tests/runner.js → load-tests/config.js
- `Write Benchmark Test Invocation` --semantically_similar_to--> `Read Benchmark Test Invocation`  [INFERRED] [semantically similar]
  load-tests/write-test.js → load-tests/read-test.js
- `Read Benchmark Test Invocation` --semantically_similar_to--> `Failover Benchmark Test Invocation`  [INFERRED] [semantically similar]
  load-tests/read-test.js → load-tests/failover-test.js

## Hyperedges (group relationships)
- **Benchmark Test Execution Flow: Test → Runner → Auth → Autocannon** — write_test_runtest, read_test_runtest, failover_test_runtest, db_ping_test_runtest, runner_runauthenticatedtest, auth_gettoken [EXTRACTED 1.00]
- **Shared Configuration Constants Used by Auth and Runner** — config_api_base_url, config_test_user, auth_gettoken, runner_runauthenticatedtest [EXTRACTED 1.00]
- **Autocannon HTTP Load Test Pattern with Auth and Tracking** — runner_autocannon_instance, runner_runauthenticatedtest, auth_gettoken [EXTRACTED 1.00]

## Communities (4 total, 2 thin omitted)

### Community 0 - "Test Scenarios"
Cohesion: 0.39
Nodes (8): Default Connections Constant, Default Duration Constant, DB Ping Benchmark Test Invocation, Failover Benchmark Test Invocation, Read Benchmark Test Invocation, Autocannon HTTP Load Instance, runAuthenticatedTest - Autocannon Runner, Write Benchmark Test Invocation

### Community 2 - "Auth & Config Core"
Cohesion: 0.5
Nodes (4): getToken - JWT Auth Function, JWT_TOKEN Environment Variable Fallback, API Base URL Constant, Test User Credentials

## Knowledge Gaps
- **3 isolated node(s):** `Test User Credentials`, `Default Connections Constant`, `Default Duration Constant`
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `runAuthenticatedTest - Autocannon Runner` connect `Test Scenarios` to `Auth & Config Core`?**
  _High betweenness centrality (0.207) - this node is a cross-community bridge._
- **Why does `getToken - JWT Auth Function` connect `Auth & Config Core` to `Test Scenarios`?**
  _High betweenness centrality (0.050) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `Read Benchmark Test Invocation` (e.g. with `Failover Benchmark Test Invocation` and `Write Benchmark Test Invocation`) actually correct?**
  _`Read Benchmark Test Invocation` has 3 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Test User Credentials`, `Default Connections Constant`, `Default Duration Constant` to the rest of the system?**
  _3 weakly-connected nodes found - possible documentation gaps or missing edges._