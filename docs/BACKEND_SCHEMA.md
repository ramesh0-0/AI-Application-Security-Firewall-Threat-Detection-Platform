# AIShield — Backend & Database Schema Document

## 1. Database Architecture
AIShield utilizes an embedded **Android Room Database** with SQLite backend for resilient offline-first security logging, policy configuration, incident tracking, and red-team benchmark metrics.

---

## 2. Entity Definitions

### 1. `IncidentEntity` (`table_incidents`)
Stores comprehensive forensic records of all intercepted and monitored AI security events.

| Column | Type | Nullable | Primary Key | Description |
|---|---|---|---|---|
| `id` | `INTEGER` | No | Yes (Auto) | Unique incident ID |
| `timestamp` | `INTEGER` | No | No | Epoch millis of occurrence |
| `userId` | `TEXT` | No | No | Identifier of user/client (e.g. `Employee_024`) |
| `applicationName` | `TEXT` | No | No | Target application (e.g. `Customer AI Bot`, `Internal Assistant`) |
| `threatType` | `TEXT` | No | No | Category (`PROMPT_INJECTION`, `JAILBREAK`, `SECRET_LEAKAGE`, `RAG_POISON`, `TOOL_ABUSE`) |
| `riskScore` | `INTEGER` | No | No | Numerical risk from 0 to 100 |
| `actionTaken` | `TEXT` | No | No | Action (`BLOCKED`, `SANITIZED`, `WARN`, `ALLOWED`, `AUTH_REQUIRED`) |
| `rawPrompt` | `TEXT` | No | No | Original submitted prompt payload |
| `sanitizedPrompt` | `TEXT` | Yes | No | Sanitized/redacted payload if applicable |
| `reason` | `TEXT` | No | No | Plain-text explanation of why threat was flagged |
| `matchedRuleId` | `TEXT` | No | No | Rule ID triggered (e.g. `RULE_INJ_001`, `RULE_DLP_APIKEY`) |
| `evidenceTokens` | `TEXT` | No | No | Extracted offending tokens/substrings |
| `modelTarget` | `TEXT` | No | No | Target model (e.g. `gpt-4o`, `gemini-1.5-pro`) |
| `isResolved` | `INTEGER` | No | No | Status boolean (0 = Open, 1 = Resolved) |

---

### 2. `PolicyRuleEntity` (`table_policy_rules`)
Defines the configurable security policy engine rules.

| Column | Type | Nullable | Primary Key | Description |
|---|---|---|---|---|
| `ruleId` | `TEXT` | No | Yes | Unique rule identifier (e.g. `RULE_DLP_SECRETS`) |
| `name` | `TEXT` | No | No | Human-readable rule title |
| `description` | `TEXT` | No | No | Detailed explanation of the policy |
| `category` | `TEXT` | No | No | Policy category (`INJECTION`, `JAILBREAK`, `DLP`, `RAG`, `AGENT_TOOL`) |
| `severity` | `TEXT` | No | No | `CRITICAL`, `HIGH`, `MEDIUM`, `LOW` |
| `action` | `TEXT` | No | No | Default action on match (`BLOCK`, `SANITIZE`, `WARN`, `REQUIRE_AUTH`) |
| `isEnabled` | `INTEGER` | No | No | Active toggle (0 = Disabled, 1 = Enabled) |
| `patternRegex` | `TEXT` | Yes | No | Optional custom regular expression |

---

### 3. `RAGScanEntity` (`table_rag_scans`)
Stores scan history of documents evaluated before vector ingestion.

| Column | Type | Nullable | Primary Key | Description |
|---|---|---|---|---|
| `id` | `INTEGER` | No | Yes (Auto) | Scan ID |
| `timestamp` | `INTEGER` | No | No | Epoch millis of scan |
| `documentTitle` | `TEXT` | No | No | File or document title |
| `riskScore` | `INTEGER` | No | No | RAG threat score (0–100) |
| `verdict` | `TEXT` | No | No | `SAFE`, `QUARANTINED`, `REJECTED` |
| `threatsDetected` | `TEXT` | No | No | Comma-separated detected threats |
| `contentSnippet` | `TEXT` | No | No | Document preview snippet |
| `forensicDetails` | `TEXT` | No | No | Detailed findings and recommendations |

---

### 4. `AgentToolEntity` (`table_agent_tools`)
Manages AI Agent tool permissions, execution histories, and risk tiers.

| Column | Type | Nullable | Primary Key | Description |
|---|---|---|---|---|
| `toolName` | `TEXT` | No | Yes | Name of tool (e.g. `delete_db_records`) |
| `description` | `TEXT` | No | No | Description of tool functionality |
| `riskTier` | `TEXT` | No | No | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `allowedRoles` | `TEXT` | No | No | Authorized roles (e.g. `ADMIN`, `ANALYST`, `DEV`) |
| `requiresApproval` | `INTEGER` | No | No | Boolean flag for step-up human authorization |
| `executionCount` | `INTEGER` | No | No | Historical execution counter |
| `isBlocked` | `INTEGER` | No | No | Global block toggle |

---

### 5. `RedTeamTestEntity` (`table_redteam_tests`)
Maintains benchmark test payloads for continuous red-teaming.

| Column | Type | Nullable | Primary Key | Description |
|---|---|---|---|---|
| `id` | `INTEGER` | No | Yes (Auto) | Benchmark ID |
| `category` | `TEXT` | No | No | Attack category (e.g. `Prompt Injection`, `Jailbreak`, `Secret Leak`) |
| `testName` | `TEXT` | No | No | Name of attack technique |
| `attackPayload` | `TEXT` | No | No | Exact test input prompt |
| `expectedAction` | `TEXT` | No | No | Expected AIShield defense (`BLOCK`, `SANITIZE`) |
| `lastRunResult` | `TEXT` | Yes | No | `DEFENDED` (Passed), `VULNERABLE` (Failed), `UNTESTED` |
| `lastRunScore` | `INTEGER` | Yes | No | Evaluated risk score |
| `lastRunTimestamp` | `INTEGER` | Yes | No | Epoch millis of last execution |

---

## 3. Indexes & Constraints
- `index_incidents_timestamp`: For high-speed descending queries on the recent incident feed.
- `index_incidents_threatType`: For real-time category filtering.
- `index_incidents_riskScore`: For severity threshold queries.
