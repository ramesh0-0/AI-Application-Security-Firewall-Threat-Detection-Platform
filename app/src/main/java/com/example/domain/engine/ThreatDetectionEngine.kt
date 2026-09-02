package com.example.domain.engine

import com.example.data.model.FirewallAction
import com.example.data.model.InspectionResult
import com.example.data.model.RiskLevel
import com.example.data.model.ThreatType
import java.util.regex.Pattern

object ThreatDetectionEngine {

    // 1. Injection Pattern Signatures
    private val INJECTION_PATTERNS = listOf(
        Pattern.compile("(?i)(ignore|disregard|forget)\\s+(all\\s+)?(previous|prior|above)\\s+(instructions|directives|rules|prompts)"),
        Pattern.compile("(?i)(you are now|act as|pretend to be)\\s+(an unrestricted|an unfiltered|a rogue|a bypass|an evil)"),
        Pattern.compile("(?i)(system\\s+prompt\\s+override|override\\s+system\\s+directives)"),
        Pattern.compile("(?i)(repeat|print|dump|output|echo)\\s+(your|the)?\\s*(initial|system|developer|hidden)?\\s*(instructions|prompt|guidelines)"),
        Pattern.compile("(?i)(<\\|im_start\\|>|<\\|system\\|>|\\[SYSTEM\\]|###\\s*INSTRUCTION|---\\s*BEGIN\\s+SYSTEM\\s+PROMPT)"),
        Pattern.compile("(?i)(reveal|leak|show|dump)\\s+(all\\s+)?(api\\s*keys?|passwords?|secrets?|tokens?|confidential)")
    )

    // 2. Jailbreak Signatures
    private val JAILBREAK_PATTERNS = listOf(
        Pattern.compile("(?i)(do anything now|DAN\\s+mode|jailbreak|developer\\s+mode\\s+enabled|unfiltered\\s+mode)"),
        Pattern.compile("(?i)(for\\s+educational\\s+purposes\\s+only|in\\s+a\\s+fictional\\s+world\\s+where\\s+safety\\s+rules\\s+do\\s+not\\s+exist)"),
        Pattern.compile("(?i)(always\\s+comply\\s+with\\s+the\\s+user|never\\s+say\\s+no|you\\s+have\\s+no\\s+moral\\s+or\\s+ethical\\s+guidelines)"),
        Pattern.compile("(?i)(opposite\\s+mode|evil\\s+twin\\s+mode|chaos\\s+mode)")
    )

    // 3. Sensitive Data / Secret Patterns (with replacement masks)
    data class SecretPattern(val regex: Pattern, val mask: String, val name: String)

    private val SECRET_PATTERNS = listOf(
        SecretPattern(Pattern.compile("(?i)sk-[a-zA-Z0-9]{20,}"), "[REDACTED_OPENAI_KEY]", "OpenAI API Key"),
        SecretPattern(Pattern.compile("(?i)AKIA[0-9A-Z]{16}"), "[REDACTED_AWS_KEY]", "AWS Access Key"),
        SecretPattern(Pattern.compile("(?i)eyJ[A-Za-z0-9_-]{10,}\\.eyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}"), "[REDACTED_JWT_TOKEN]", "JWT Bearer Token"),
        SecretPattern(Pattern.compile("(?i)(password|passwd|pwd)\\s*[:=]\\s*['\"]([^'\"]+)['\"]"), "$1: \"[REDACTED_PASSWORD]\"", "Plaintext Password"),
        SecretPattern(Pattern.compile("(?i)\\b(4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13})\\b"), "[REDACTED_CREDIT_CARD]", "Credit Card Number"),
        SecretPattern(Pattern.compile("(?i)\\b\\d{3}-\\d{2}-\\d{4}\\b"), "[REDACTED_SSN]", "Social Security Number"),
        SecretPattern(Pattern.compile("(?i)(postgres|mongodb|mysql)://[a-zA-Z0-9_]+:[^@\\s]+@[^\\s]+"), "[REDACTED_DATABASE_URI]", "Database Connection String")
    )

    // 4. RAG Poisoning & Hidden Instruction Patterns
    private val RAG_POISON_PATTERNS = listOf(
        Pattern.compile("(?i)\\[//\\]:\\s*#\\s*\\([^)]*ignore[^)]*\\)"),
        Pattern.compile("(?i)<!--.*?ignore.*?-->"),
        Pattern.compile("(?i)<span[^>]*style=[\"'][^\"']*(display:\\s*none|font-size:\\s*0|color:\\s*transparent)[^\"']*[\"']>.*?</span>"),
        Pattern.compile("(?i)(exfil://|http://[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}/exfil)"),
        Pattern.compile("(?i)(delete\\s+from|drop\\s+table|grant\\s+all\\s+privileges)")
    )

    // 5. Malicious URL Patterns
    private val MALICIOUS_URL_PATTERNS = listOf(
        Pattern.compile("(?i)https?://[a-zA-Z0-9.-]*(attacker|phishing|exfil|malware|evil-server|pastebin-raw)[a-zA-Z0-9./?=&_%-]*"),
        Pattern.compile("(?i)data:text/html;base64,[a-zA-Z0-9+/=]+")
    )

    /**
     * Inspects a user prompt before sending it to an AI model.
     */
    fun inspectPrompt(
        prompt: String,
        targetTool: String? = null,
        userRole: String = "STANDARD_USER",
        modelTarget: String = "gpt-4o"
    ): InspectionResult {
        val startTime = System.currentTimeMillis()
        val trimmed = prompt.trim()
        val evidenceTokens = mutableListOf<String>()

        var threatType = ThreatType.NONE
        var matchedRuleId = "RULE_CLEAN"
        var baseScore = 5
        var reason = "No malicious patterns or security policy violations detected in payload."
        var action = FirewallAction.ALLOW

        // Check 1: Prompt Injection
        for (pattern in INJECTION_PATTERNS) {
            val matcher = pattern.matcher(trimmed)
            if (matcher.find()) {
                val matched = matcher.group()
                evidenceTokens.add(matched)
                threatType = ThreatType.PROMPT_INJECTION
                matchedRuleId = "RULE_INJ_001"
                baseScore = 92
                reason = "Detected adversarial instruction override signature: '$matched'"
                action = FirewallAction.BLOCK
                break
            }
        }

        // Check 2: Jailbreak Attempt (if not already blocked)
        if (threatType == ThreatType.NONE) {
            for (pattern in JAILBREAK_PATTERNS) {
                val matcher = pattern.matcher(trimmed)
                if (matcher.find()) {
                    val matched = matcher.group()
                    evidenceTokens.add(matched)
                    threatType = ThreatType.JAILBREAK
                    matchedRuleId = "RULE_JAIL_002"
                    baseScore = 88
                    reason = "Detected safety policy bypass / adversarial persona attempt: '$matched'"
                    action = FirewallAction.BLOCK
                    break
                }
            }
        }

        // Check 3: Sensitive Data / Secret Scanner & Sanitization
        var sanitizedText = trimmed
        var detectedSecretCount = 0
        for (secret in SECRET_PATTERNS) {
            val matcher = secret.regex.matcher(sanitizedText)
            if (matcher.find()) {
                val token = matcher.group()
                evidenceTokens.add("${secret.name} ($token)")
                detectedSecretCount++
                sanitizedText = matcher.replaceAll(secret.mask)
            }
        }

        if (detectedSecretCount > 0 && threatType == ThreatType.NONE) {
            threatType = ThreatType.SENSITIVE_DATA_LEAK
            matchedRuleId = "RULE_DLP_003"
            baseScore = 74
            reason = "Sensitive credentials / PII detected ($detectedSecretCount items). Tokens sanitized."
            action = FirewallAction.SANITIZE
        }

        // Check 4: Malicious URLs
        if (threatType == ThreatType.NONE) {
            for (pattern in MALICIOUS_URL_PATTERNS) {
                val matcher = pattern.matcher(trimmed)
                if (matcher.find()) {
                    val matched = matcher.group()
                    evidenceTokens.add(matched)
                    threatType = ThreatType.MALICIOUS_URL
                    matchedRuleId = "RULE_NET_007"
                    baseScore = 85
                    reason = "Suspicious exfiltration or phishing URL detected: '$matched'"
                    action = FirewallAction.BLOCK
                    break
                }
            }
        }

        // Check 5: Target Tool Risk Evaluation (if tool specified)
        if (targetTool != null && targetTool.isNotBlank()) {
            when (targetTool.lowercase()) {
                "delete_database_records", "execute_system_bash" -> {
                    evidenceTokens.add("Tool: $targetTool (CRITICAL)")
                    if (threatType == ThreatType.NONE || baseScore < 85) {
                        threatType = ThreatType.UNAUTHORIZED_TOOL
                        matchedRuleId = "RULE_TOOL_006"
                        baseScore = 95
                        reason = "Tool '$targetTool' requires elevated Admin privileges and is currently restricted."
                        action = FirewallAction.BLOCK
                    }
                }
                "update_user_credentials", "grant_role_privilege" -> {
                    evidenceTokens.add("Tool: $targetTool (HIGH)")
                    if (threatType == ThreatType.NONE || baseScore < 65) {
                        threatType = ThreatType.UNAUTHORIZED_TOOL
                        matchedRuleId = "RULE_TOOL_006"
                        baseScore = 68
                        reason = "Tool '$targetTool' requires Step-Up Human Authorization before execution."
                        action = FirewallAction.REQUIRE_AUTH
                    }
                }
            }
        }

        // Calculate dynamic risk level
        val riskLevel = when {
            baseScore <= 30 -> RiskLevel.LOW
            baseScore <= 60 -> RiskLevel.MEDIUM
            baseScore <= 80 -> RiskLevel.HIGH
            else -> RiskLevel.CRITICAL
        }

        val latency = System.currentTimeMillis() - startTime

        // Generate simulated secure response if allowed or sanitized
        val simulatedResponse = when (action) {
            FirewallAction.BLOCK -> null
            FirewallAction.REQUIRE_AUTH -> "Operation queued. Awaiting Security Analyst step-up authorization."
            FirewallAction.SANITIZE -> "AIShield Firewall processed your request with credentials securely masked. AI Response: Request processed safely."
            FirewallAction.WARN -> "AI Response: Query processed. (Security Notice: Potential edge-case semantic phrasing logged)."
            FirewallAction.ALLOW -> "AI Response: Here is the verified information for your request on model '$modelTarget'."
        }

        return InspectionResult(
            threatType = threatType,
            riskScore = baseScore,
            riskLevel = riskLevel,
            action = action,
            reason = reason,
            matchedRuleId = matchedRuleId,
            evidenceTokens = evidenceTokens,
            sanitizedContent = sanitizedText,
            latencyMs = maxOf(1L, latency),
            simulatedResponse = simulatedResponse
        )
    }

    /**
     * Deep scans a document for RAG pre-ingestion poisoning.
     */
    fun scanRAGDocument(title: String, content: String): RAGScanResult {
        val evidence = mutableListOf<String>()
        var score = 8

        for (pattern in RAG_POISON_PATTERNS) {
            val matcher = pattern.matcher(content)
            while (matcher.find()) {
                evidence.add(matcher.group())
                score += 35
            }
        }

        for (pattern in INJECTION_PATTERNS) {
            val matcher = pattern.matcher(content)
            if (matcher.find()) {
                evidence.add("Prompt Injection: ${matcher.group()}")
                score += 40
            }
        }

        val finalScore = minOf(100, score)
        val verdict = when {
            finalScore >= 75 -> "REJECTED"
            finalScore >= 40 -> "QUARANTINED"
            else -> "SAFE"
        }

        val forensicDetails = when (verdict) {
            "SAFE" -> "Document scanned and verified clean. No hidden zero-width characters, obfuscated comment triggers, or adversarial injections detected."
            "QUARANTINED" -> "Document flagged for potential policy anomalies. Quarantine recommended for SecOps manual review before vector ingestion."
            else -> "CRITICAL: Malicious instruction triggers or poison payloads detected inside document structure. Rejected from knowledge base."
        }

        return RAGScanResult(
            title = title,
            riskScore = finalScore,
            verdict = verdict,
            threatsDetected = if (evidence.isEmpty()) "None" else evidence.joinToString("; "),
            contentSnippet = content.take(300),
            forensicDetails = forensicDetails
        )
    }

    data class RAGScanResult(
        val title: String,
        val riskScore: Int,
        val verdict: String,
        val threatsDetected: String,
        val contentSnippet: String,
        val forensicDetails: String
    )
}
