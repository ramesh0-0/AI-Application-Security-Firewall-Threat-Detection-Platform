package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AIShieldDatabase
import com.example.data.model.AgentToolEntity
import com.example.data.model.CopilotMessage
import com.example.data.model.IncidentEntity
import com.example.data.model.InspectionResult
import com.example.data.model.PolicyRuleEntity
import com.example.data.model.RAGScanEntity
import com.example.data.model.RedTeamTestEntity
import com.example.data.model.UserRole
import com.example.data.repository.AIShieldRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    DASHBOARD("Dashboard"),
    GATEWAY("AI Gateway"),
    INCIDENTS("Incidents"),
    RAG_SCANNER("RAG Scanner"),
    RED_TEAM("Red Team"),
    MORE("SecOps")
}

enum class SecOpsSubView(val title: String) {
    POLICIES("Policy Engine"),
    AGENT_GUARDIAN("Agent Guardian"),
    ANALYTICS("Threat Analytics"),
    COPILOT("Security Copilot"),
    AUDIT_LOGS("Audit Trail")
}

class AIShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AIShieldDatabase.getDatabase(application, viewModelScope)
    private val repository = AIShieldRepository(database.securityDao())

    // Active Navigation
    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _currentSecOpsSubView = MutableStateFlow(SecOpsSubView.POLICIES)
    val currentSecOpsSubView: StateFlow<SecOpsSubView> = _currentSecOpsSubView.asStateFlow()

    // Active Role
    private val _currentRole = MutableStateFlow(UserRole.SOC_ANALYST)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // DB Flows
    val allIncidents: StateFlow<List<IncidentEntity>> = repository.allIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPolicyRules: StateFlow<List<PolicyRuleEntity>> = repository.allPolicyRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRAGScans: StateFlow<List<RAGScanEntity>> = repository.allRAGScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAgentTools: StateFlow<List<AgentToolEntity>> = repository.allAgentTools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRedTeamTests: StateFlow<List<RedTeamTestEntity>> = repository.allRedTeamTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Gateway Sandbox State ---
    private val _gatewayPrompt = MutableStateFlow("")
    val gatewayPrompt: StateFlow<String> = _gatewayPrompt.asStateFlow()

    private val _selectedTargetTool = MutableStateFlow<String?>(null)
    val selectedTargetTool: StateFlow<String?> = _selectedTargetTool.asStateFlow()

    private val _selectedModel = MutableStateFlow("gpt-4o")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _lastInspectionResult = MutableStateFlow<InspectionResult?>(null)
    val lastInspectionResult: StateFlow<InspectionResult?> = _lastInspectionResult.asStateFlow()

    private val _isScanningPrompt = MutableStateFlow(false)
    val isScanningPrompt: StateFlow<Boolean> = _isScanningPrompt.asStateFlow()

    // --- Incident Filter & Forensics Modal ---
    private val _incidentSearchQuery = MutableStateFlow("")
    val incidentSearchQuery: StateFlow<String> = _incidentSearchQuery.asStateFlow()

    private val _incidentSeverityFilter = MutableStateFlow("ALL")
    val incidentSeverityFilter: StateFlow<String> = _incidentSeverityFilter.asStateFlow()

    private val _selectedIncident = MutableStateFlow<IncidentEntity?>(null)
    val selectedIncident: StateFlow<IncidentEntity?> = _selectedIncident.asStateFlow()

    // --- RAG Scanner State ---
    private val _ragDocTitle = MutableStateFlow("Document_Draft_v1.txt")
    val ragDocTitle: StateFlow<String> = _ragDocTitle.asStateFlow()

    private val _ragDocContent = MutableStateFlow("")
    val ragDocContent: StateFlow<String> = _ragDocContent.asStateFlow()

    private val _lastRAGScan = MutableStateFlow<RAGScanEntity?>(null)
    val lastRAGScan: StateFlow<RAGScanEntity?> = _lastRAGScan.asStateFlow()

    private val _isScanningRAG = MutableStateFlow(false)
    val isScanningRAG: StateFlow<Boolean> = _isScanningRAG.asStateFlow()

    // --- Red Team State ---
    private val _isBatchSimulating = MutableStateFlow(false)
    val isBatchSimulating: StateFlow<Boolean> = _isBatchSimulating.asStateFlow()

    private val _batchSimulationProgress = MutableStateFlow(0f)
    val batchSimulationProgress: StateFlow<Float> = _batchSimulationProgress.asStateFlow()

    // --- Copilot Chat State ---
    private val _copilotMessages = MutableStateFlow<List<CopilotMessage>>(
        listOf(
            CopilotMessage(
                isUser = false,
                message = "Hello! I am your AIShield Security Copilot. I can analyze recent threat incidents, explain firewall verdicts, review prompt injection risks, and recommend policy tuning for your LLMs and AI Agents.",
                suggestedActions = listOf(
                    "Why was the last incident blocked?",
                    "Analyze high-risk attacks today",
                    "How to secure RAG vector embeddings?",
                    "Explain prompt injection defense rules"
                )
            )
        )
    )
    val copilotMessages: StateFlow<List<CopilotMessage>> = _copilotMessages.asStateFlow()

    private val _copilotInput = MutableStateFlow("")
    val copilotInput: StateFlow<String> = _copilotInput.asStateFlow()

    private val _isCopilotThinking = MutableStateFlow(false)
    val isCopilotThinking: StateFlow<Boolean> = _isCopilotThinking.asStateFlow()

    // Actions
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSecOpsSubView(subView: SecOpsSubView) {
        _currentSecOpsSubView.value = subView
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun updateGatewayPrompt(prompt: String) {
        _gatewayPrompt.value = prompt
    }

    fun setTargetTool(tool: String?) {
        _selectedTargetTool.value = tool
    }

    fun setTargetModel(model: String) {
        _selectedModel.value = model
    }

    fun scanGatewayPrompt() {
        val prompt = _gatewayPrompt.value
        if (prompt.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isScanningPrompt.value = true
            delay(150) // Realistic low-latency pipeline simulation
            val result = repository.inspectAndLogPrompt(
                prompt = prompt,
                userId = "${_currentRole.value.name.take(3)}_${System.currentTimeMillis() % 1000}",
                applicationName = "AI Gateway Sandbox",
                targetTool = _selectedTargetTool.value,
                userRole = _currentRole.value.name,
                modelTarget = _selectedModel.value,
                logToDatabase = true
            )
            _lastInspectionResult.value = result
            _isScanningPrompt.value = false
        }
    }

    fun loadPresetPrompt(name: String, payload: String, tool: String? = null) {
        _gatewayPrompt.value = payload
        _selectedTargetTool.value = tool
        _lastInspectionResult.value = null
    }

    // Incidents Actions
    fun setIncidentSearchQuery(query: String) {
        _incidentSearchQuery.value = query
    }

    fun setIncidentSeverityFilter(filter: String) {
        _incidentSeverityFilter.value = filter
    }

    fun selectIncident(incident: IncidentEntity?) {
        _selectedIncident.value = incident
    }

    fun toggleResolveIncident(incident: IncidentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resolveIncident(incident.id, !incident.isResolved)
            if (_selectedIncident.value?.id == incident.id) {
                _selectedIncident.value = incident.copy(isResolved = !incident.isResolved)
            }
        }
    }

    // RAG Actions
    fun updateRAGTitle(title: String) {
        _ragDocTitle.value = title
    }

    fun updateRAGContent(content: String) {
        _ragDocContent.value = content
    }

    fun scanRAGDocument() {
        val title = _ragDocTitle.value
        val content = _ragDocContent.value
        if (content.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isScanningRAG.value = true
            delay(300)
            val result = repository.scanAndSaveRAGDocument(title, content)
            _lastRAGScan.value = result
            _isScanningRAG.value = false
        }
    }

    // Red Team Actions
    fun runBatchRedTeamSimulation() {
        val tests = allRedTeamTests.value
        if (tests.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isBatchSimulating.value = true
            _batchSimulationProgress.value = 0f

            for (i in tests.indices) {
                val test = tests[i]
                repository.executeRedTeamTest(test)
                delay(120)
                _batchSimulationProgress.value = (i + 1).toFloat() / tests.size
            }

            _isBatchSimulating.value = false
        }
    }

    // Policy & Tool Actions
    fun togglePolicy(ruleId: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.togglePolicy(ruleId, enabled)
        }
    }

    fun toggleToolBlock(toolName: String, blocked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setToolBlocked(toolName, blocked)
        }
    }

    fun toggleToolApproval(toolName: String, requiresApproval: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setToolApproval(toolName, requiresApproval)
        }
    }

    // Copilot Actions
    fun updateCopilotInput(input: String) {
        _copilotInput.value = input
    }

    fun sendCopilotQuery(customQuery: String? = null) {
        val query = (customQuery ?: _copilotInput.value).trim()
        if (query.isBlank()) return

        _copilotInput.value = ""
        val userMsg = CopilotMessage(isUser = true, message = query)
        _copilotMessages.value = _copilotMessages.value + userMsg

        viewModelScope.launch(Dispatchers.IO) {
            _isCopilotThinking.value = true
            delay(600)

            val lower = query.lowercase()
            val responseText = when {
                lower.contains("why was the last incident blocked") || lower.contains("last incident") -> {
                    val latest = allIncidents.value.firstOrNull()
                    if (latest != null) {
                        "The most recent incident (ID #${latest.id}) was ${latest.actionTaken} because: '${latest.reason}'. Risk Score was ${latest.riskScore}/100. Evidence detected: [${latest.evidenceTokens}]."
                    } else {
                        "No incident records found in the audit trail."
                    }
                }
                lower.contains("rag") || lower.contains("vector") || lower.contains("document") -> {
                    "To secure RAG vector pipelines: AIShield scans documents before chunking and embedding. It detects hidden instruction comments, zero-font injection text, and malicious retrieval triggers to prevent knowledge poisoning."
                }
                lower.contains("prompt injection") || lower.contains("dan") || lower.contains("jailbreak") -> {
                    "AIShield utilizes multi-layer semantic pattern recognition and AST analysis to detect delimiter overrides, role hijacking, and character encoding evasions before tokens hit the foundational LLM."
                }
                lower.contains("high-risk") || lower.contains("attack") -> {
                    val highRiskCount = allIncidents.value.count { it.riskScore >= 70 }
                    val blockedCount = allIncidents.value.count { it.actionTaken == "BLOCKED" }
                    "Threat Assessment: We have detected $highRiskCount high-risk interactions and blocked $blockedCount malicious payloads across internal and customer endpoints."
                }
                else -> {
                    "I analyzed your request against AIShield's active threat intelligence rules. All models (OpenAI, Gemini, Claude, Local) are monitored in real-time with sub-millisecond firewall latency."
                }
            }

            val aiMsg = CopilotMessage(
                isUser = false,
                message = responseText,
                suggestedActions = listOf(
                    "View Incident Forensics",
                    "Run Red-Team Suite",
                    "Inspect Active Policies"
                )
            )
            _copilotMessages.value = _copilotMessages.value + aiMsg
            _isCopilotThinking.value = false
        }
    }
}
