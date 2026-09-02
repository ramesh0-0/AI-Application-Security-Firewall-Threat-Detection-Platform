# AIShield — Phased Implementation Plan

## Phase 1: Project Setup & Core Configuration
- [x] Configure Project Metadata (`metadata.json`), App Name (`AIShield`), ApplicationId (`com.aistudio.aishield.secfw`).
- [x] Configure Custom Cyber Shield Launcher Foreground and Background Vector Drawables.
- [x] Verify Gradle Dependencies in `app/build.gradle.kts` (Compose Material 3, Room KSP, Coroutines, Moshi, Retrofit).

## Phase 2: Database Layer & Entity Models
- [x] Define Room Entities: `IncidentEntity`, `PolicyRuleEntity`, `RAGScanEntity`, `AgentToolEntity`, `RedTeamTestEntity`.
- [x] Implement Room DAO (`SecurityDao`) with reactive `Flow` queries and suspend mutation operations.
- [x] Implement Room Database (`AIShieldDatabase`) with pre-population initializers for policy rules, agent tools, default red-team tests, and seed incidents.

## Phase 3: Domain & Security Engines
- [x] **ThreatDetectionEngine**: Heuristic, signature, and regex analyzers for:
  - Direct & Indirect Prompt Injection
  - Jailbreaks (DAN, persona bypass, obfuscation)
  - Sensitive Data Loss Prevention (PII, API keys, Passwords, DB strings, JWTs)
  - RAG Document Poisoning (hidden instructions, zero-width tokens, malicious markdown)
  - AI Agent Tool execution risks
  - Response Leakage Scanner
- [x] **PolicyEngine**: Rule matching, policy evaluation, and action resolution.
- [x] **RiskScoreCalculator**: Multi-factor dynamic score calculator (0–100).
- [x] **AIShieldRepository**: Unified repository bridging DAO, Threat Detection Engine, and reactive StateFlows.

## Phase 4: State Management & ViewModel
- [x] Implement `AIShieldViewModel`:
  - Gateway real-time scanning state
  - Filtered incident feed & search state
  - Active incident forensic inspection state
  - RAG scanner state & document analysis
  - Red-Team test suite runner state & batch execution
  - Policy toggle & customization state
  - Agent Tool risk management state
  - AI Security Copilot interactive conversation state
  - Role-based Access Control (RBAC) switcher (Admin, SOC Analyst, Developer, User)

## Phase 5: Presentation & Material 3 UI Screens
- [x] `CyberTheme.kt`: Cyber Obsidian & Neon Cyan Material 3 Color Scheme & Typography.
- [x] Reusable Components: `RiskBadge`, `CyberCard`, `MetricCard`, `ForensicView`, `VerdictBanner`.
- [x] **DashboardScreen**: Live Posture, Real-time metrics, risk distribution, threat breakdown.
- [x] **GatewayScreen**: Live interactive AI firewall sandbox with real-time token detection and input/output diff.
- [x] **IncidentsScreen**: Incident investigation room with 7-W forensic drilldown dialog.
- [x] **RAGScannerScreen**: Document vulnerability scanner with poison highlighting.
- [x] **RedTeamScreen**: Automated AI attack simulator with batch testing and scorecards.
- [x] **PoliciesScreen**: Security rules management and custom regex engine.
- [x] **AgentGuardianScreen**: Agent tool permissions and risk tiers.
- [x] **AnalyticsScreen**: Threat statistics and attack vector charts.
- [x] **CopilotScreen**: SecOps AI assistant for conversational threat explanation.

## Phase 6: Testing & Quality Assurance
- [x] Implement comprehensive Unit Tests verifying Threat Detection, Risk Scoring, DLP Masking, and Policy Enforcement.
- [x] Compile and verify with `compile_applet`.
- [x] Production `README.md` documentation.
