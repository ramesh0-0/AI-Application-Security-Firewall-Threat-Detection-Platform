package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AgentToolEntity
import com.example.data.model.IncidentEntity
import com.example.data.model.PolicyRuleEntity
import com.example.data.model.RAGScanEntity
import com.example.data.model.RedTeamTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {
    // Incidents
    @Query("SELECT * FROM table_incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM table_incidents WHERE id = :id LIMIT 1")
    suspend fun getIncidentById(id: Long): IncidentEntity?

    @Query("SELECT * FROM table_incidents WHERE riskScore >= :minRisk ORDER BY timestamp DESC")
    fun getHighRiskIncidents(minRisk: Int = 60): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity): Long

    @Query("UPDATE table_incidents SET isResolved = :resolved WHERE id = :id")
    suspend fun setIncidentResolved(id: Long, resolved: Boolean)

    @Query("DELETE FROM table_incidents")
    suspend fun clearAllIncidents()

    // Policy Rules
    @Query("SELECT * FROM table_policy_rules ORDER BY ruleId ASC")
    fun getAllPolicyRules(): Flow<List<PolicyRuleEntity>>

    @Query("SELECT * FROM table_policy_rules WHERE isEnabled = 1")
    suspend fun getEnabledRules(): List<PolicyRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicyRule(rule: PolicyRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPolicyRules(rules: List<PolicyRuleEntity>)

    @Update
    suspend fun updatePolicyRule(rule: PolicyRuleEntity)

    @Query("UPDATE table_policy_rules SET isEnabled = :enabled WHERE ruleId = :ruleId")
    suspend fun togglePolicyRule(ruleId: String, enabled: Boolean)

    // RAG Scans
    @Query("SELECT * FROM table_rag_scans ORDER BY timestamp DESC")
    fun getAllRAGScans(): Flow<List<RAGScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRAGScan(scan: RAGScanEntity): Long

    // Agent Tools
    @Query("SELECT * FROM table_agent_tools ORDER BY riskTier DESC, toolName ASC")
    fun getAllAgentTools(): Flow<List<AgentToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAgentTools(tools: List<AgentToolEntity>)

    @Update
    suspend fun updateAgentTool(tool: AgentToolEntity)

    @Query("UPDATE table_agent_tools SET isBlocked = :blocked WHERE toolName = :toolName")
    suspend fun setToolBlocked(toolName: String, blocked: Boolean)

    @Query("UPDATE table_agent_tools SET requiresApproval = :requiresApproval WHERE toolName = :toolName")
    suspend fun setToolRequiresApproval(toolName: String, requiresApproval: Boolean)

    // Red Team Tests
    @Query("SELECT * FROM table_redteam_tests ORDER BY id ASC")
    fun getAllRedTeamTests(): Flow<List<RedTeamTestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRedTeamTests(tests: List<RedTeamTestEntity>)

    @Update
    suspend fun updateRedTeamTest(test: RedTeamTestEntity)
}
