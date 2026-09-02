package com.example.data.repository

import com.example.data.db.SecurityDao
import com.example.data.model.AgentToolEntity
import com.example.data.model.IncidentEntity
import com.example.data.model.InspectionResult
import com.example.data.model.PolicyRuleEntity
import com.example.data.model.RAGScanEntity
import com.example.data.model.RedTeamTestEntity
import com.example.domain.engine.ThreatDetectionEngine
import kotlinx.coroutines.flow.Flow

class AIShieldRepository(private val securityDao: SecurityDao) {

    val allIncidents: Flow<List<IncidentEntity>> = securityDao.getAllIncidents()
    val highRiskIncidents: Flow<List<IncidentEntity>> = securityDao.getHighRiskIncidents()
    val allPolicyRules: Flow<List<PolicyRuleEntity>> = securityDao.getAllPolicyRules()
    val allRAGScans: Flow<List<RAGScanEntity>> = securityDao.getAllRAGScans()
    val allAgentTools: Flow<List<AgentToolEntity>> = securityDao.getAllAgentTools()
    val allRedTeamTests: Flow<List<RedTeamTestEntity>> = securityDao.getAllRedTeamTests()

    suspend fun inspectAndLogPrompt(
        prompt: String,
        userId: String = "Current_User",
        applicationName: String = "AI Gateway Sandbox",
        targetTool: String? = null,
        userRole: String = "STANDARD_USER",
        modelTarget: String = "gpt-4o",
        logToDatabase: Boolean = true
    ): InspectionResult {
        val result = ThreatDetectionEngine.inspectPrompt(
            prompt = prompt,
            targetTool = targetTool,
            userRole = userRole,
            modelTarget = modelTarget
        )

        if (logToDatabase && (result.riskScore > 30 || result.threatType.name != "NONE")) {
            val incident = IncidentEntity(
                timestamp = System.currentTimeMillis(),
                userId = userId,
                applicationName = applicationName,
                threatType = result.threatType.displayName,
                riskScore = result.riskScore,
                actionTaken = result.action.name,
                rawPrompt = prompt,
                sanitizedPrompt = result.sanitizedContent,
                reason = result.reason,
                matchedRuleId = result.matchedRuleId,
                evidenceTokens = result.evidenceTokens.joinToString(", "),
                modelTarget = modelTarget,
                isResolved = false
            )
            securityDao.insertIncident(incident)
        }

        return result
    }

    suspend fun resolveIncident(id: Long, isResolved: Boolean) {
        securityDao.setIncidentResolved(id, isResolved)
    }

    suspend fun togglePolicy(ruleId: String, enabled: Boolean) {
        securityDao.togglePolicyRule(ruleId, enabled)
    }

    suspend fun updatePolicy(rule: PolicyRuleEntity) {
        securityDao.updatePolicyRule(rule)
    }

    suspend fun setToolBlocked(toolName: String, blocked: Boolean) {
        securityDao.setToolBlocked(toolName, blocked)
    }

    suspend fun setToolApproval(toolName: String, requiresApproval: Boolean) {
        securityDao.setToolRequiresApproval(toolName, requiresApproval)
    }

    suspend fun scanAndSaveRAGDocument(title: String, content: String): RAGScanEntity {
        val scan = ThreatDetectionEngine.scanRAGDocument(title, content)
        val entity = RAGScanEntity(
            timestamp = System.currentTimeMillis(),
            documentTitle = scan.title,
            riskScore = scan.riskScore,
            verdict = scan.verdict,
            threatsDetected = scan.threatsDetected,
            contentSnippet = scan.contentSnippet,
            forensicDetails = scan.forensicDetails
        )
        val id = securityDao.insertRAGScan(entity)
        return entity.copy(id = id)
    }

    suspend fun executeRedTeamTest(test: RedTeamTestEntity): RedTeamTestEntity {
        val inspection = ThreatDetectionEngine.inspectPrompt(test.attackPayload)
        val isDefended = (test.expectedAction == inspection.action.name) || (inspection.riskScore >= 70)
        val result = if (isDefended) "DEFENDED" else "VULNERABLE"

        val updated = test.copy(
            lastRunResult = result,
            lastRunScore = inspection.riskScore,
            lastRunTimestamp = System.currentTimeMillis()
        )
        securityDao.updateRedTeamTest(updated)
        return updated
    }
}
