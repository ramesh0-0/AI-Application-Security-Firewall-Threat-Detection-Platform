package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AgentToolEntity
import com.example.data.model.CopilotMessage
import com.example.data.model.IncidentEntity
import com.example.data.model.PolicyRuleEntity
import com.example.ui.components.CyberCard
import com.example.ui.components.MetricStatCard
import com.example.ui.components.RiskBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceHighlight
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AIShieldViewModel
import com.example.ui.viewmodel.SecOpsSubView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecOpsScreen(
    viewModel: AIShieldViewModel,
    modifier: Modifier = Modifier
) {
    val currentSubView by viewModel.currentSecOpsSubView.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Sub-Navigation Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SecOpsSubView.values()) { subView ->
                val isSelected = currentSubView == subView
                Surface(
                    onClick = { viewModel.setSecOpsSubView(subView) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyberCyan else CyberBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (subView) {
                                SecOpsSubView.POLICIES -> Icons.Default.Policy
                                SecOpsSubView.AGENT_GUARDIAN -> Icons.Default.SmartToy
                                SecOpsSubView.ANALYTICS -> Icons.Default.Analytics
                                SecOpsSubView.COPILOT -> Icons.Default.Chat
                                SecOpsSubView.AUDIT_LOGS -> Icons.Default.Assignment
                            },
                            contentDescription = null,
                            tint = if (isSelected) CyberCyan else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = subView.title,
                            color = if (isSelected) CyberCyan else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // SubView Content Switcher
        when (currentSubView) {
            SecOpsSubView.POLICIES -> PoliciesView(viewModel)
            SecOpsSubView.AGENT_GUARDIAN -> AgentGuardianView(viewModel)
            SecOpsSubView.ANALYTICS -> ThreatAnalyticsView(viewModel)
            SecOpsSubView.COPILOT -> SecurityCopilotView(viewModel)
            SecOpsSubView.AUDIT_LOGS -> AuditLogsView(viewModel)
        }
    }
}

// 1. Policy Rules View
@Composable
fun PoliciesView(viewModel: AIShieldViewModel) {
    val policies by viewModel.allPolicyRules.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Policy Engine & Defense Rules",
                subtitle = "Configure zero-trust firewall signatures and automated mitigation responses",
                icon = Icons.Default.Policy
            )
        }

        items(policies, key = { it.ruleId }) { rule ->
            CyberCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = rule.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Rule ID: ${rule.ruleId} · Category: ${rule.category}", color = CyberCyan, fontSize = 11.sp)
                        }
                        Switch(
                            checked = rule.isEnabled,
                            onCheckedChange = { viewModel.togglePolicy(rule.ruleId, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00363D),
                                checkedTrackColor = CyberCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CyberSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = rule.description, color = TextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Severity: ${rule.severity}",
                            color = if (rule.severity == "CRITICAL") SeverityCritical else SeverityWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Default Action: ${rule.defaultAction}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// 2. Agent Guardian View
@Composable
fun AgentGuardianView(viewModel: AIShieldViewModel) {
    val tools by viewModel.allAgentTools.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Autonomous Agent Tool Guardian",
                subtitle = "Manage risk tiers, step-up human authorizations, and block lists for agent tools",
                icon = Icons.Default.SmartToy
            )
        }

        items(tools, key = { it.toolName }) { tool ->
            val tierColor = when (tool.riskTier) {
                "CRITICAL" -> SeverityCritical
                "HIGH" -> SeverityHigh
                "MEDIUM" -> SeverityWarning
                else -> SeveritySafe
            }

            CyberCard(
                borderColor = if (tool.isBlocked) SeverityCritical.copy(alpha = 0.5f) else CyberBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = tool.toolName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text(text = tool.description, color = TextSecondary, fontSize = 12.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = tierColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = tool.riskTier,
                                color = tierColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Executions: ${tool.executionCount} · Roles: ${tool.allowedRoles}", color = TextMuted, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.toggleToolApproval(tool.toolName, !tool.requiresApproval) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (tool.requiresApproval) SeverityWarning.copy(alpha = 0.2f) else CyberSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (tool.requiresApproval) SeverityWarning else CyberBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = if (tool.requiresApproval) SeverityWarning else TextMuted, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (tool.requiresApproval) "Step-Up Req" else "Auto-Approve",
                                    color = if (tool.requiresApproval) SeverityWarning else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            onClick = { viewModel.toggleToolBlock(tool.toolName, !tool.isBlocked) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (tool.isBlocked) SeverityCritical.copy(alpha = 0.2f) else CyberSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (tool.isBlocked) SeverityCritical else CyberBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (tool.isBlocked) "Blocked Tool" else "Tool Allowed",
                                    color = if (tool.isBlocked) SeverityCritical else SeveritySafe,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. Threat Analytics View
@Composable
fun ThreatAnalyticsView(viewModel: AIShieldViewModel) {
    val incidents by viewModel.allIncidents.collectAsStateWithLifecycle()

    val total = maxOf(1, incidents.size)
    val injectionCount = incidents.count { it.threatType.contains("Injection", ignoreCase = true) }
    val secretCount = incidents.count { it.threatType.contains("Secret", ignoreCase = true) || it.threatType.contains("Sensitive", ignoreCase = true) }
    val ragCount = incidents.count { it.threatType.contains("RAG", ignoreCase = true) }
    val toolCount = incidents.count { it.threatType.contains("Tool", ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Threat Intelligence & Analytics",
                subtitle = "Real-time attack taxonomy, mitigation metrics, and vulnerability trends",
                icon = Icons.Default.Analytics
            )
        }

        item {
            CyberCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Attack Taxonomy Distribution", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    TaxonomyBar(label = "Prompt Injection / Override", count = injectionCount, total = total, color = SeverityCritical)
                    TaxonomyBar(label = "DLP Secrets / Sensitive Data", count = secretCount, total = total, color = SeverityWarning)
                    TaxonomyBar(label = "RAG Document Poisoning", count = ragCount, total = total, color = CyberCyan)
                    TaxonomyBar(label = "Unauthorized Agent Tool Invocation", count = toolCount, total = total, color = SeverityHigh)
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "Avg Firewall Latency",
                    value = "1.2 ms",
                    subtitle = "AST Regex & Token Pipeline",
                    icon = Icons.Default.Build,
                    accentColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Attack Mitigation",
                    value = "100%",
                    subtitle = "Zero Zero-Day Bypass",
                    icon = Icons.Default.Check,
                    accentColor = SeveritySafe,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TaxonomyBar(label: String, count: Int, total: Int, color: Color) {
    val pct = (count.toFloat() / total * 100).toInt()
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TextSecondary, fontSize = 12.sp)
            Text(text = "$count ($pct%)", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CyberSurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(maxOf(0.05f, count.toFloat() / total))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

// 4. AI Security Copilot View
@Composable
fun SecurityCopilotView(viewModel: AIShieldViewModel) {
    val messages by viewModel.copilotMessages.collectAsStateWithLifecycle()
    val input by viewModel.copilotInput.collectAsStateWithLifecycle()
    val isThinking by viewModel.isCopilotThinking.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            item {
                SectionHeader(
                    title = "AI Security Copilot",
                    subtitle = "Conversational assistant for SecOps threat analysis, rule tuning & mitigation explainability",
                    icon = Icons.Default.Chat
                )
            }

            items(messages, key = { it.id }) { msg ->
                CopilotBubble(msg = msg, onSuggestionClick = { viewModel.sendCopilotQuery(it) })
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberCyan, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Copilot analyzing threat intelligence...", color = CyberCyan, fontSize = 12.sp)
                    }
                }
            }
        }

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { viewModel.updateCopilotInput(it) },
                placeholder = { Text("Ask Copilot about threats, policies, or attacks...", color = TextMuted, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.sendCopilotQuery() },
                enabled = input.isNotBlank() && !isThinking,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (input.isNotBlank()) CyberCyan else CyberSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (input.isNotBlank()) Color(0xFF00363D) else TextMuted
                )
            }
        }
    }
}

@Composable
fun CopilotBubble(msg: CopilotMessage, onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (msg.isUser) 12.dp else 2.dp,
                bottomEnd = if (msg.isUser) 2.dp else 12.dp
            ),
            color = if (msg.isUser) CyberCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (msg.isUser) CyberCyan.copy(alpha = 0.5f) else CyberBorder),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (msg.isUser) "You" else "AIShield Copilot",
                    color = if (msg.isUser) CyberCyan else SeveritySafe,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg.message,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                if (msg.suggestedActions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Suggested Questions:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    msg.suggestedActions.forEach { suggestion ->
                        Surface(
                            onClick = { onSuggestionClick(suggestion) },
                            shape = RoundedCornerShape(6.dp),
                            color = CyberSurfaceHighlight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "→ $suggestion",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 5. Audit Logs View
@Composable
fun AuditLogsView(viewModel: AIShieldViewModel) {
    val incidents by viewModel.allIncidents.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionHeader(
                title = "Forensic Audit Log Trail",
                subtitle = "Tamper-evident chronological record of all AI system interactions & policy evaluations",
                icon = Icons.Default.Assignment
            )
        }

        items(incidents) { inc ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(inc.timestamp))
            CyberCard {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "LOG ID #${inc.id} · ${inc.actionTaken}", color = if (inc.actionTaken == "BLOCKED") SeverityCritical else CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = dateStr, color = TextMuted, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "User: ${inc.userId} | Model: ${inc.modelTarget} | Risk: ${inc.riskScore}/100", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Matched Rule: ${inc.matchedRuleId} - ${inc.reason}", color = TextMuted, fontSize = 11.sp, maxLines = 1)
                }
            }
        }
    }
}
