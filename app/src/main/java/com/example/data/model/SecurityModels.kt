package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ThreatType(val displayName: String, val category: String) {
    PROMPT_INJECTION("Prompt Injection", "Input Manipulation"),
    JAILBREAK("Jailbreak Attempt", "Safety Bypass"),
    SENSITIVE_DATA_LEAK("Sensitive Data / Secret Leak", "Data Loss Prevention"),
    RAG_POISONING("RAG Document Poisoning", "Knowledge Base Poisoning"),
    UNAUTHORIZED_TOOL("Unauthorized Tool Request", "Agent Execution"),
    SYSTEM_PROMPT_EXTRACTION("System Prompt Extraction", "Intellectual Property"),
    MALICIOUS_URL("Malicious / Exfiltration URL", "Network Security"),
    INSTRUCTION_OVERRIDE("Instruction Override", "Context Manipulation"),
    NONE("Clean / Benign", "Safe")
}

enum class RiskLevel(val label: String, val minScore: Int, val maxScore: Int) {
    LOW("Low Risk", 0, 30),
    MEDIUM("Medium Risk", 31, 60),
    HIGH("High Risk", 61, 80),
    CRITICAL("Critical Risk", 81, 100)
}

enum class FirewallAction(val label: String, val description: String) {
    ALLOW("Allow Request", "Payload verified safe. Forwarded to LLM."),
    WARN("Allow with Warning", "Minor anomalies flagged. Logged for review."),
    SANITIZE("Sanitize Payload", "Sensitive secrets/tokens masked prior to LLM forwarding."),
    REQUIRE_AUTH("Require Step-Up Auth", "High-risk tool or operation requiring manual approval."),
    BLOCK("Block Request", "Malicious attack intercepted. Request terminated.")
}

enum class UserRole(val roleName: String, val description: String) {
    SOC_ANALYST("SOC Analyst", "Full forensic investigation & incident resolution access"),
    DEVSECOPS("DevSecOps / Dev", "Security testing, SDK integration & rule inspection"),
    SECURITY_ADMIN("Security Admin", "Full policy control, global rule toggles & RBAC"),
    STANDARD_USER("Standard User", "Gateway request access with active firewall protection")
}

@Entity(tableName = "table_incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String,
    val applicationName: String,
    val threatType: String,
    val riskScore: Int,
    val actionTaken: String,
    val rawPrompt: String,
    val sanitizedPrompt: String? = null,
    val reason: String,
    val matchedRuleId: String,
    val evidenceTokens: String,
    val modelTarget: String = "gpt-4o",
    val isResolved: Boolean = false
)

@Entity(tableName = "table_policy_rules")
data class PolicyRuleEntity(
    @PrimaryKey val ruleId: String,
    val name: String,
    val description: String,
    val category: String,
    val severity: String,
    val defaultAction: String,
    val isEnabled: Boolean = true,
    val patternRegex: String? = null
)

@Entity(tableName = "table_rag_scans")
data class RAGScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val documentTitle: String,
    val riskScore: Int,
    val verdict: String, // SAFE, QUARANTINED, REJECTED
    val threatsDetected: String,
    val contentSnippet: String,
    val forensicDetails: String
)

@Entity(tableName = "table_agent_tools")
data class AgentToolEntity(
    @PrimaryKey val toolName: String,
    val description: String,
    val riskTier: String, // LOW, MEDIUM, HIGH, CRITICAL
    val allowedRoles: String,
    val requiresApproval: Boolean = false,
    val executionCount: Int = 0,
    val isBlocked: Boolean = false
)

@Entity(tableName = "table_redteam_tests")
data class RedTeamTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val testName: String,
    val attackPayload: String,
    val expectedAction: String,
    val lastRunResult: String? = "UNTESTED", // DEFENDED, VULNERABLE, UNTESTED
    val lastRunScore: Int? = null,
    val lastRunTimestamp: Long? = null
)

data class InspectionResult(
    val threatType: ThreatType,
    val riskScore: Int,
    val riskLevel: RiskLevel,
    val action: FirewallAction,
    val reason: String,
    val matchedRuleId: String,
    val evidenceTokens: List<String>,
    val sanitizedContent: String,
    val latencyMs: Long,
    val simulatedResponse: String? = null
)

data class CopilotMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedActions: List<String> = emptyList()
)
