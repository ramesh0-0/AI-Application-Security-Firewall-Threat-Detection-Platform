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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IncidentEntity
import com.example.ui.components.CyberCard
import com.example.ui.components.RiskBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceHighlight
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AIShieldViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val SEVERITY_FILTERS = listOf("ALL", "CRITICAL", "HIGH", "MEDIUM", "LOW")

@Composable
fun IncidentsScreen(
    viewModel: AIShieldViewModel,
    modifier: Modifier = Modifier
) {
    val incidents by viewModel.allIncidents.collectAsStateWithLifecycle()
    val searchQuery by viewModel.incidentSearchQuery.collectAsStateWithLifecycle()
    val severityFilter by viewModel.incidentSeverityFilter.collectAsStateWithLifecycle()
    val selectedIncident by viewModel.selectedIncident.collectAsStateWithLifecycle()

    val filtered = incidents.filter { inc ->
        val matchesSearch = searchQuery.isBlank() ||
                inc.rawPrompt.contains(searchQuery, ignoreCase = true) ||
                inc.userId.contains(searchQuery, ignoreCase = true) ||
                inc.threatType.contains(searchQuery, ignoreCase = true) ||
                inc.applicationName.contains(searchQuery, ignoreCase = true)

        val matchesSeverity = when (severityFilter) {
            "CRITICAL" -> inc.riskScore >= 81
            "HIGH" -> inc.riskScore in 61..80
            "MEDIUM" -> inc.riskScore in 31..60
            "LOW" -> inc.riskScore <= 30
            else -> true
        }

        matchesSearch && matchesSeverity
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "Incident Forensics Room",
                subtitle = "Comprehensive 7-W audit trail of all flagged and intercepted AI threats",
                icon = Icons.Default.Warning
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setIncidentSearchQuery(it) },
                placeholder = {
                    Text("Search by user, threat signature, app, or payload...", color = TextMuted, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setIncidentSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SEVERITY_FILTERS) { filter ->
                    val isSelected = severityFilter == filter
                    Surface(
                        onClick = { viewModel.setIncidentSeverityFilter(filter) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyberCyan else CyberBorder
                        )
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) CyberCyan else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Incident Counter
        item {
            Text(
                text = "Showing ${filtered.size} recorded incidents",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        if (filtered.isEmpty()) {
            item {
                CyberCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No incidents match your current filter criteria.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filtered, key = { it.id }) { inc ->
                IncidentFullCard(
                    incident = inc,
                    onClick = { viewModel.selectIncident(inc) }
                )
            }
        }
    }

    // 7-W Forensics Drilldown Dialog
    if (selectedIncident != null) {
        Forensics7WDialog(
            incident = selectedIncident!!,
            onDismiss = { viewModel.selectIncident(null) },
            onToggleResolve = { viewModel.toggleResolveIncident(selectedIncident!!) }
        )
    }
}

@Composable
fun IncidentFullCard(
    incident: IncidentEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()).format(Date(incident.timestamp))

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (incident.isResolved) SeveritySafe else SeverityCritical)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = incident.threatType,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                RiskBadge(score = incident.riskScore)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = incident.rawPrompt,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "User: ${incident.userId} · App: ${incident.applicationName}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = dateStr,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun Forensics7WDialog(
    incident: IncidentEntity,
    onDismiss: () -> Unit,
    onToggleResolve: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(Date(incident.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "7-W Incident Forensics",
                            color = CyberCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Incident ID #${incident.id}", color = TextSecondary, fontSize = 12.sp)
                        RiskBadge(score = incident.riskScore)
                    }
                }

                // The 7-W Pillars
                item {
                    ForensicRow(label = "WHO", value = "User: ${incident.userId}\nApplication: ${incident.applicationName}")
                }
                item {
                    ForensicRow(label = "WHAT", value = "Threat Type: ${incident.threatType}\nPolicy Matched: ${incident.matchedRuleId}")
                }
                item {
                    ForensicRow(label = "WHEN", value = dateStr)
                }
                item {
                    ForensicRow(label = "WHERE", value = "Destination Model: ${incident.modelTarget}\nGateway Endpoint: /v1/chat/completions")
                }
                item {
                    ForensicRow(label = "WHY", value = incident.reason)
                }
                item {
                    ForensicRow(label = "EVIDENCE", value = incident.evidenceTokens)
                }
                item {
                    ForensicRow(
                        label = "ACTION",
                        value = "Mitigation Taken: ${incident.actionTaken}\nStatus: ${if (incident.isResolved) "RESOLVED / AUDITED" else "OPEN INVESTIGATION"}"
                    )
                }

                item {
                    Text(
                        text = "Original Intercepted Payload:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        color = CyberSurfaceHighlight
                    ) {
                        Text(
                            text = incident.rawPrompt,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onToggleResolve,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (incident.isResolved) CyberSurfaceVariant else SeveritySafe
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (incident.isResolved) "Mark as Unresolved (Reopen)" else "Resolve & Acknowledge Incident",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ForensicRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(CyberSurfaceVariant)
            .padding(8.dp)
    ) {
        Text(
            text = label,
            color = CyberCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
