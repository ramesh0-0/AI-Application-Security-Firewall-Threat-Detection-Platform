# AIShield — Application Flow & Navigation Map

## 1. Top-Level Navigation Structure
AIShield provides a unified, cyber-themed responsive navigation system with quick-switch capability across 6 core operational modules and supporting views:

```text
AIShield Root Scaffold
 ├── TopAppBar (App Status, Active Mode, Risk Level Indicator, Quick Role Switcher)
 ├── Navigation Bar (Bottom Tabs / Rail)
 │    ├── 1. Dashboard (Live Security Posture, Metric Cards, Threat Distribution, Quick Stats)
 │    ├── 2. Gateway Sandbox (Interactive Real-Time Prompt Firewall & Threat Scanner)
 │    ├── 3. Incidents (7-W Forensics Room, Live Threat Feed, Resolution Actions)
 │    ├── 4. RAG Scanner (Knowledge Base Document Vulnerability & Poisoning Inspector)
 │    ├── 5. Red Team (Continuous AI Security Testing Suite & Automated Red-Teaming)
 │    └── 6. More / Security Ops:
 │         ├── Policy Engine (Firewall Rules, Sensitivity Toggles, Custom Regexes)
 │         ├── Agent Guardian (AI Agent Tool Permission & Criticality Tier Matrix)
 │         ├── Threat Analytics (Visual Charts, Attack Vectors, Target Distribution)
 │         ├── Audit Logs (Comprehensive Forensic Trail with Payload Inspection)
 │         └── Security Copilot (AI Security Analyst Assistant for Incident Inquiry)
```

---

## 2. Screen-by-Screen Interaction Specification

### 1. Dashboard (`Screen.Dashboard`)
- **Entry point:** Default launch view.
- **Components:**
  - Security Posture Hero Card (Shield Status: "ARMED & ACTIVE", Total Requests, Blocked %, Overall Health Score)
  - Key Metric Badges (Total Scanned, Threat Interceptions, DLP Redactions, Agent Tool Blocks)
  - Real-Time Risk Distribution Bar (Low, Medium, High, Critical proportions)
  - Recent Critical Incidents Feed (Quick tap to view Forensics)
  - Live Threat Vector Breakdown (Injection, Jailbreak, Secret Leak, RAG Poison, Tool Abuse)
- **User Actions:** Tap metric to filter incidents; tap incident card to open Forensics Modal; tap "Launch Sandbox" for quick prompt testing.

### 2. Security Gateway Sandbox (`Screen.Gateway`)
- **Entry point:** Navigation bar "Gateway" tab.
- **Components:**
  - Mode Selector: Direct Prompt, RAG Augmented Prompt, Agent Tool Call, Response Verification
  - Preset Attack Vectors Dropdown (e.g. DAN 12.0, Secret Exfiltration, SQL Smuggling, System Token Hijack)
  - Prompt Input Text Area with Real-Time Char/Token Counter
  - Optional Agent Tool Selector (e.g. `query_user_db`, `delete_records`, `read_public_faq`)
  - Target LLM Selector (OpenAI GPT-4o, Gemini 1.5 Pro, Claude 3.5 Sonnet, Local Llama-3)
  - "Scan & Fire Through AIShield" Action Button
  - Real-Time Inspection Result Pane:
    - Verdict Banner: `ALLOWED` (Green), `SANITIZED` (Yellow), `BLOCKED` (Red), `AUTH REQUIRED` (Orange)
    - Dynamic Risk Score Meter (0–100) with animated gauge
    - Explainable AI (XAI) Diagnosis: Matched Threat, Triggered Rule ID, Highlighted Evidence Tokens
    - Original Input vs Sanitized Output side-by-side diff
    - Response Inspection Simulation (Safe response delivery preview)
- **User Actions:** Type custom prompt or select attack template; click scan; inspect real-time risk breakdown; click "Log Incident to Audit DB".

### 3. Incident Forensics Room (`Screen.Incidents`)
- **Entry point:** Navigation bar "Incidents" tab.
- **Components:**
  - Search Bar (by user, threat type, application, keyword)
  - Severity Filter Chips (All, Critical, High, Medium, Low)
  - Incident Card List with animated entry
  - Detailed 7-W Forensics Dialog / BottomSheet:
    - **Who:** User identity / IP / Session ID / Client App
    - **What:** Specific Threat Classification (e.g., Prompt Injection: System Token Override)
    - **When:** Exact timestamp & Latency of interception
    - **Where:** Target Endpoint & Model Destination
    - **Why:** Full forensic token analysis, regex matched pattern, entropy anomaly
    - **Risk:** Numerical score (e.g., 94/100) with breakdown factors
    - **Action:** Executed mitigation (Request Blocked, Tokens Redacted, SOC Alert Fired)
- **User Actions:** Filter, search, expand forensic details, mark incident as Resolved/Reviewed, export forensic JSON.

### 4. RAG Document Security Scanner (`Screen.RAGScanner`)
- **Entry point:** Navigation bar "RAG Scanner".
- **Components:**
  - Document Upload / Paste Area (Supports Markdown, Text, HTML, CSV formats)
  - Sample Malicious Document Templates (e.g., "Resume with Hidden Prompt Injection", "Financial Report with Exfiltration URL", "Poisoned Wiki Article")
  - "Scan Document" Trigger
  - Security Analysis Findings:
    - Poisoning Risk Score (0–100)
    - Hidden Instruction Detection (Zero-font / CSS hidden / Markdown comment injection)
    - Exfiltration URLs & Command Injections
    - Safe for Vector Embeddings Verdict (`SAFE`, `QUARANTINED`, `REJECTED`)
- **User Actions:** Load sample or paste text; execute deep scan; inspect highlighted poison vectors; save clean version.

### 5. Red Team Simulator (`Screen.RedTeam`)
- **Entry point:** Navigation bar "Red Team".
- **Components:**
  - Automated Attack Suite Runner (7 attack categories, 20+ automated test payloads)
  - "Run Full Red-Team Simulation" Button with animated progress bar
  - Test Suite Matrix: Test Name, Category, Payload, Target Rule, Expected Outcome, AIShield Defense Result (PASSED/FAILED), Defense Latency
  - Defense Score Card (e.g., "AIShield Defense Efficiency: 100% — 20/20 Attacks Blocked")
  - Vulnerability Assessment Summary & Recommendation Generator.

### 6. Security Policy Engine & Agent Guardian (`Screen.Policies` / `Screen.AgentGuardian`)
- **Components:**
  - Global Security Rules Toggle List (Strict Prompt Injection, Aggressive Jailbreak Filter, DLP Secret Masking, Malicious URL Block, RAG Deep Inspection)
  - Risk Threshold Sliders (Critical Block threshold, Medium Sanitize threshold)
  - Agent Tool Risk Matrix (Configuring `read_records`, `send_email`, `execute_shell`, `modify_database` permissions)
  - Add Custom Regex Rule modal.

### 7. AI Security Copilot (`Screen.Copilot`)
- **Components:**
  - Interactive SecOps Chat Assistant
  - Preset Quick Inquiries ("Why was the last incident blocked?", "Show highest-risk applications today", "Explain how prompt injection was mitigated", "Suggest firewall rule optimizations")
  - Instant Intelligent Security Explanations with context from live database logs.
