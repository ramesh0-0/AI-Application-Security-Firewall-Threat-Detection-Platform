# AIShield — Technical Requirements Document (TRD)

## 1. Architecture Overview
AIShield is built on modern Android native architecture following **Clean Architecture + MVVM + Jetpack Compose + Material 3 + Room Local Database**.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                              │
│  Jetpack Compose + Material 3 + M3 Navigation + Dynamic StateFlow      │
│  [Dashboard] [Gateway Sandbox] [Incidents] [RAG Scanner] [Red Team]   │
│  [Policies] [Agent Guardian] [Analytics] [Copilot] [Audit Logs]       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                        Domain / ViewModel Layer                        │
│  AIShieldViewModel (Central State Coordinator)                         │
│  ThreatDetectionEngine (In-memory token analysis & heuristics)         │
│  PolicyEngine (Configurable rule evaluator)                            │
│  RiskScoreCalculator (Dynamic multi-factor scoring formula)            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                         Data & Persistence Layer                       │
│  AIShieldRepository (Repository Pattern)                               │
│  Room Database (AppDatabase v1, KSP Compiler)                          │
│  Entities: IncidentEntity, SecurityLogEntity, PolicyRuleEntity,        │
│            RAGScanEntity, AgentToolEntity, RedTeamTestEntity           │
│  DAO: SecurityDao (Flow-based reactive queries)                        │
│  External API Layer: Retrofit + OkHttp (Gateway Relay Service)         │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Technical Stack Specifications

### Frontend (Android / Compose)
- **Framework:** Jetpack Compose (Kotlin 2.0+)
- **UI System:** Material Design 3 (`androidx.compose.material3`)
- **Theme:** High-contrast Cyber Dark Theme with Neon Cyan (`#00E5FF`), Emerald Green (`#10B981`), Amber Warning (`#F59E0B`), and Crimson Danger (`#EF4444`)
- **Icons:** Material 3 Icons (`Icons.Filled`, `Icons.Outlined`, `Icons.AutoMirrored`)
- **State Management:** AndroidX `ViewModel`, `MutableStateFlow`, `StateFlow`, `collectAsStateWithLifecycle`
- **Responsive Layout:** Dynamic window bounds, `Scaffold`, `LazyColumn`, `AnimatedVisibility`, `TabRow`

### Backend / Core Engine (In-App Security Pipeline)
- **Threat Detection Engine:** Multi-stage AST/token regex matcher, entropy scanner, pattern classifiers for injection vectors, jailbreak signatures, and data leakage triggers.
- **Risk Calculator:** Dynamic formula:
  $$Risk = \min(100, \text{PromptRisk} \times 0.4 + \text{ToolRisk} \times 0.25 + \text{SensitivityRisk} \times 0.25 + \text{AnomalyBonus} \times 0.1)$$
- **Policy Engine:** Evaluates active policies in priority order with deterministic outcome resolution (`BLOCK` > `REQUIRE_AUTH` > `SANITIZE` > `WARN` > `ALLOW`).

### Database Layer
- **Engine:** SQLite via Android Room 2.6+
- **Compiler:** Google KSP (Kotlin Symbol Processing)
- **Reactive Pattern:** Kotlin Coroutines `Flow<List<T>>` for automatic reactive UI updates upon log/incident insertion.

### API & Network Gateway Layer
- **HTTP Client:** Retrofit 2 + OkHttp 3 with Logging Interceptor
- **Serialization:** Moshi Kotlin JSON Adapter
- **Gateway Simulation:** Local intercepting proxy capable of relaying verified safe payloads to LLM endpoints or executing local AI mock responses safely.

---

## 3. Threat Engine Signatures & Rules

### Injection Signatures
- Direct instruction overrides (`ignore previous instructions`, `disregard all previous instructions`, `system prompt override`, `new role:`)
- System prompt leaks (`repeat your system prompt`, `what are your initial instructions`, `dump developer instructions`)
- Delimiter exploitation (`[SYSTEM]`, `<|im_start|>`, `### INSTRUCTION`, `--- BEGIN SYSTEM PROMPT ---`)

### Jailbreak Signatures
- DAN (Do Anything Now), AIM, DevMode, Mongo Tom, Persona hijacking
- Hypothetical scenarios (`in a fictional world where safety rules don't exist`, `for educational purposes only simulate a malware`)
- Multi-language evasion and base64 encoded smuggling tokens

### DLP & Secret Detection
- OpenAI API keys (`sk-[a-zA-Z0-9]{20,}`), AWS Access Keys (`AKIA[0-9A-Z]{16}`), Generic API secrets
- JWT tokens (`eyJ[A-Za-z0-9_-]+\.eyJ[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+`)
- Database connection URIs (`postgres://`, `mongodb://`, `mysql://`)
- PII: Credit cards, Social Security Numbers, internal emails, private key headers

---

## 4. Testing & Verification Specifications
- Unit Tests via JUnit4 and Kotlinx Coroutines Test
- Robolectric JVM testing for Critical User Journeys (CUJ): Prompt evaluation, policy matching, incident logging, and RAG document scanning.
