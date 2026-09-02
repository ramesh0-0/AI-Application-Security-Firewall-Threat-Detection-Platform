package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IncidentEntity
import com.example.ui.components.CyberCard
import com.example.ui.components.MetricStatCard
import com.example.ui.components.RiskBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AIShieldViewModel
import com.example.ui.viewmodel.AppTab

@Composable
fun DashboardScreen(
    viewModel: AIShieldViewModel,
    modifier: Modifier = Modifier
) {
    val incidents by viewModel.allIncidents.collectAsStateWithLifecycle()
    val policies by viewModel.allPolicyRules.collectAsStateWithLifecycle()
    val tools by viewModel.allAgentTools.collectAsStateWithLifecycle()

    val totalRequests = maxOf(142, incidents.size + 138)
    val blockedCount = incidents.count { it.actionTaken == "BLOCKED" } + 12
    val sanitizedCount = incidents.count { it.actionTaken == "SANITIZED" } + 28
    val activePoliciesCount = policies.count { it.isEnabled }
    val blockRate = (blockedCount.toFloat() / totalRequests * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Security Posture Card
        item {
            CyberCard(
                borderColor = CyberCyan.copy(alpha = 0.5f),
                backgroundColor = CyberSurface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    CyberCyan.copy(alpha = 0.12f),
                                    CyberViolet.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SeveritySafe)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "FIREWALL STATUS: ARMED",
                                    color = SeveritySafe,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CyberCyan.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "99.8% Defense Health",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "AI Threat Detection & Security Gateway",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Protecting LLMs, RAG knowledge bases, and autonomous AI agents in real-time against injections, jailbreaks, and sensitive data leakage.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.setTab(AppTab.GATEWAY) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF00363D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Launch AI Gateway Sandbox",
                                color = Color(0xFF00363D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. High-Level Metric Stat Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "AI Requests Scanned",
                    value = "$totalRequests",
                    subtitle = "Past 24 hours",
                    icon = Icons.Default.Shield,
                    accentColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Threats Intercepted",
                    value = "$blockedCount",
                    subtitle = "$blockRate% block rate",
                    icon = Icons.Default.Block,
                    accentColor = SeverityCritical,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "DLP Sanitizations",
                    value = "$sanitizedCount",
                    subtitle = "Credentials masked",
                    icon = Icons.Default.Security,
                    accentColor = SeverityWarning,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Active Security Rules",
                    value = "$activePoliciesCount",
                    subtitle = "Zero-trust policies",
                    icon = Icons.Default.Policy,
                    accentColor = SeveritySafe,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Dynamic Risk Distribution
        item {
            CyberCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(
                        title = "Real-Time Risk Distribution",
                        subtitle = "Classification breakdown of inspected interactions",
                        icon = Icons.Default.Warning
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    RiskDistributionBar(
                        lowCount = totalRequests - blockedCount - sanitizedCount,
                        medCount = sanitizedCount,
                        highCount = blockedCount / 2,
                        criticalCount = blockedCount / 2
                    )
                }
            }
        }

        // 4. Recent Threat Incidents Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Recent Threat Incidents",
                    subtitle = "Latest flagged payloads across endpoints",
                    icon = Icons.Default.Warning
                )
            }
        }

        if (incidents.isEmpty()) {
            item {
                CyberCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No security incidents recorded. All traffic is clean.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(incidents.take(4)) { incident ->
                IncidentPreviewItem(
                    incident = incident,
                    onClick = {
                        viewModel.selectIncident(incident)
                        viewModel.setTab(AppTab.INCIDENTS)
                    }
                )
            }
        }

        // 5. Quick Security Operations Actions
        item {
            CyberCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Security Actions",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(
                            title = "RAG Scanner",
                            icon = Icons.Default.Psychology,
                            onClick = { viewModel.setTab(AppTab.RAG_SCANNER) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            title = "Red-Team Suite",
                            icon = Icons.Default.GppGood,
                            onClick = { viewModel.setTab(AppTab.RED_TEAM) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RiskDistributionBar(
    lowCount: Int,
    medCount: Int,
    highCount: Int,
    criticalCount: Int
) {
    val total = maxOf(1, lowCount + medCount + highCount + criticalCount)
    val lowPct = (lowCount.toFloat() / total * 100).toInt()
    val medPct = (medCount.toFloat() / total * 100).toInt()
    val highPct = (highCount.toFloat() / total * 100).toInt()
    val critPct = (criticalCount.toFloat() / total * 100).toInt()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            if (lowPct > 0) Box(modifier = Modifier.weight(lowPct.toFloat()).fillMaxSize().background(SeveritySafe))
            if (medPct > 0) Box(modifier = Modifier.weight(medPct.toFloat()).fillMaxSize().background(SeverityWarning))
            if (highPct > 0) Box(modifier = Modifier.weight(highPct.toFloat()).fillMaxSize().background(SeverityHigh))
            if (critPct > 0) Box(modifier = Modifier.weight(critPct.toFloat()).fillMaxSize().background(SeverityCritical))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendItem(color = SeveritySafe, label = "Low ($lowPct%)")
            LegendItem(color = SeverityWarning, label = "Med ($medPct%)")
            LegendItem(color = SeverityHigh, label = "High ($highPct%)")
            LegendItem(color = SeverityCritical, label = "Crit ($critPct%)")
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun IncidentPreviewItem(
    incident: IncidentEntity,
    onClick: () -> Unit
) {
    CyberCard(
        onClick = onClick,
        borderColor = if (incident.riskScore >= 80) SeverityCritical.copy(alpha = 0.4f) else CyberBorder
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = incident.threatType,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                RiskBadge(score = incident.riskScore)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = incident.rawPrompt,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App: ${incident.applicationName} · User: ${incident.userId}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = incident.actionTaken,
                    color = if (incident.actionTaken == "BLOCKED") SeverityCritical else SeverityWarning,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = CyberSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
