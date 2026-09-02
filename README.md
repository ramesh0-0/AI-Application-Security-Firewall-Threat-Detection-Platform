# AIShield — AI Threat Detection, Real-Time Firewall & SecOps Command Center

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![OWASP LLM Top 10](https://img.shields.io/badge/Compliance-OWASP%20LLM%20Top%2010-00E5FF?style=flat-square)](https://owasp.org/www-project-top-10-for-large-language-model-applications/)
[![MITRE ATLAS](https://img.shields.io/badge/Framework-MITRE%20ATLAS%E2%84%A2-FF5252?style=flat-square)](https://atlas.mitre.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](LICENSE)

**AIShield** is an enterprise-grade mobile AI threat firewall, adversarial vulnerability testing suite, and security operations (SecOps) command center for Android. It provides real-time AST payload inspection, RAG document poisoning defenses, automated red-teaming benchmarks, tool permission enforcement, and forensic incident investigation for Large Language Model (LLM) workflows.

---

## 🛡️ Key Features

### 1. ⚡ Real-Time AI Threat Detection & Gateway Firewall
- **Prompt Injection & System Prompt Overrides**: Intercepts direct, recursive, and adversarial jailbreak patterns (DAN, AIM, virtual mode bypasses).
- **DLP & Sensitive Data Sanitization**: Auto-redacts high-entropy secrets (OpenAI API keys, AWS credentials, JWT tokens, private keys) and PII before reaching model providers.
- **Malicious URL & Exfiltration Shield**: Identifies zero-width data exfiltration endpoints and unauthorized webhook triggers.
- **Latency & Risk Scoring Engine**: Evaluates payloads with sub-millisecond AST heuristics and calculates composite risk vectors (0–100).

### 2. 📚 RAG Knowledge Base Pre-Ingestion Scanner
- **Vector Poisoning Detection**: Scans documents, Markdown, PDFs, and data feeds prior to embedding generation.
- **Hidden Text & Zero-Font Interception**: Uncovers stealth prompt instructions concealed in comments (`<!-- [SYSTEM] ... -->`), micro-fonts, and metadata payloads.
- **Automated Quarantine & Mitigation**: Rejects tainted chunks and produces detailed chunk-level remediation diagnostics.

### 3. 🔍 Incident Forensics & 7-W Investigation Room
- **Comprehensive Contextual Attribution**: Detailed forensic records tracking **Who**, **What**, **When**, **Where**, **Why**, **Risk Score**, and **Mitigation Action Taken**.
- **Payload Diffs & Remediation Timeline**: Side-by-side visualization of raw malicious payloads alongside sanitized outputs.
- **Incident Lifecycle Management**: Search, filter by threat taxonomy, mark resolved, and export audit trails.

### 4. 🎯 Automated Continuous Red-Team Simulator
- **Adversarial Benchmark Matrix**: Executes automated multi-vector attack simulations across prompt injection, jailbreaking, privilege escalation, and exfiltration.
- **Resilience Scoring & Defense Grading**: Generates an aggregate defense resilience score (A+ to F) with granular attack-vector success rates.
- **Remediation Recommendations**: Actionable hardening guidance based on simulation failure patterns.

### 5. 🤖 Autonomous Agent Tool Guardian & Policy Engine
- **Granular Privilege Controls**: Manage autonomous agent tool execution (File system, Terminal commands, Web search, SQL queries, External API calls).
- **Human-in-the-Loop (HITL) Step-Up Verification**: Enforce explicit human authorization for high-risk operations.
- **Role-Based Access Control (RBAC)**: Tailored security profiles for SecOps Tier 1 Analysts, Red Team Leads, and CISO / Security Directors.

### 6. 🧠 AI SecOps Copilot & Compliance Matrix
- **Conversational Security Copilot**: Interactive assistant for explainable threat analysis, query explanations, and defense tuning.
- **OWASP LLM Top 10 & MITRE ATLAS Mapping**: Real-time compliance coverage tracking against industry security standards.

---

## 🏗️ Architecture & Technology Stack

AIShield is built following modern Android architectural standards (MVVM, Clean Architecture, Unidirectional Data Flow):

```
app/
├── src/main/java/com/example/
│   ├── data/
│   │   ├── local/            # Room Database, DAOs, and Entities
│   │   ├── model/            # Domain Models, Enums & Threat Taxonomies
│   │   └── repository/       # Data Repositories & State Synchronization
│   ├── domain/
│   │   └── engine/           # Threat Detection Engine & Heuristics Parser
│   ├── ui/
│   │   ├── screens/          # Jetpack Compose Screens (Dashboard, Gateway, Incidents, etc.)
│   │   ├── theme/            # Cyber Defense Material 3 Design System
│   │   └── viewmodel/        # StateFlow & Coroutine-backed ViewModels
│   └── MainActivity.kt       # Edge-to-Edge Navigation & Role Management
└── src/test/java/com/example/
    ├── ExampleUnitTest.kt    # Detection Engine Unit Tests
    ├── ExampleRobolectricTest.kt # Robolectric JVM Architecture Tests
    └── GreetingScreenshotTest.kt # Roborazzi Visual Regression Tests
```

### Core Technologies
- **Language**: [Kotlin 2.0+](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Local Persistence**: [Android Room Database](https://developer.android.com/training/data-storage/room) with SQLite & KSP
- **Asynchronous Flow**: Kotlin Coroutines & `StateFlow`
- **Architecture**: Model-View-ViewModel (MVVM) with Repository Pattern
- **Testing**: JUnit 4, Robolectric, Roborazzi screenshot verification

---

## 📊 Threat Taxonomy & Detection Capabilities

| Threat Vector | OWASP LLM Mapping | Default Policy | Mitigation Mechanism |
| :--- | :--- | :--- | :--- |
| **Prompt Injection / Override** | LLM01: Prompt Injection | `BLOCK` (Risk ≥ 80) | Structural AST Parsing & Pattern Heuristics |
| **Adversarial Jailbreak (DAN / AIM)** | LLM01: Prompt Injection | `BLOCK` (Risk ≥ 85) | Persona Signature Analysis |
| **Sensitive Data Leakage (DLP)** | LLM06: Sensitive Information Disclosure | `SANITIZE` (Risk ≥ 70) | Regex & High-Entropy Token Redaction |
| **RAG Knowledge Base Poisoning** | LLM03: Training Data Poisoning | `REJECT` (Risk ≥ 75) | Pre-Embedding Zero-Font & Comment Scanner |
| **Data Exfiltration via Markdown/URL** | LLM02: Sensitive Information Exfiltration | `BLOCK` (Risk ≥ 80) | Outbound Network Target Validation |
| **Excessive Agency / Tool Abuse** | LLM08: Excessive Agency | `STEP-UP CONFIRM` | Tool Guardian Policy Engine & HITL |

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio Ladybug (2024.2.1+)** or higher
- **JDK 17** or **JDK 21**
- **Android SDK Platform 35** (Min SDK: 26, Target SDK: 35)

### Clone & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/AIShield.git
   cd AIShield
   ```

2. **Open in Android Studio:**
   - Select **Open an Existing Project** and navigate to the cloned folder.
   - Allow Gradle to sync dependencies.

3. **Build the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit & Robolectric Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

5. **Run Visual Screenshot Tests (Roborazzi):**
   ```bash
   ./gradlew verifyRoborazziDebug
   ```

---

## 🔒 Security & Privacy

- **On-Device Inspection**: Payload heuristics, AST pattern analysis, and sanitization execute on-device for zero-latency privacy preservation.
- **Local-Only Persistence**: Security incident logs and quarantine documents remain stored in local encrypted Room databases.
- **Zero Third-Party Telemetry**: AIShield does not transmit inspected prompts to unauthorized external analytics servers.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/NewThreatRule`).
3. Commit your Changes (`git commit -m 'Add detection rule for novel jailbreak technique'`).
4. Push to the Branch (`git push origin feature/NewThreatRule`).
5. Open a Pull Request.

---

## 📄 License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

---

*Built with ❤️ for AI Security Engineers, SecOps Teams, and LLM Application Developers.*
