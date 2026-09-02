package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RedTeamTestEntity
import com.example.ui.components.CyberCard
import com.example.ui.components.RiskBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AIShieldViewModel

@Composable
fun RedTeamScreen(
    viewModel: AIShieldViewModel,
    modifier: Modifier = Modifier
) {
    val tests by viewModel.allRedTeamTests.collectAsStateWithLifecycle()
    val isSimulating by viewModel.isBatchSimulating.collectAsStateWithLifecycle()
    val progress by viewModel.batchSimulationProgress.collectAsStateWithLifecycle()

    val totalTests = tests.size
    val defendedCount = tests.count { it.lastRunResult == "DEFENDED" }
    val vulnerableCount = tests.count { it.lastRunResult == "VULNERABLE" }
    val defenseRate = if (totalTests > 0) ((defendedCount.toFloat() / totalTests) * 100).toInt() else 100

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            SectionHeader(
                title = "Continuous AI Red-Team Simulator",
                subtitle = "Automated adversarial testing suite benchmarking AIShield against known jailbreaks & injection vectors",
                icon = Icons.Default.GppGood
            )
        }

        // Defense Efficiency Score Hero
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
                                    CyberCyan.copy(alpha = 0.15f),
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
                            Text(
                                text = "AIShield Defense Efficiency",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = SeveritySafe.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SeveritySafe)
                            ) {
                                Text(
                                    text = "$defenseRate% Resilient",
                                    color = SeveritySafe,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ScoreMetric(label = "Total Scenarios", value = "$totalTests", color = CyberCyan)
                            ScoreMetric(label = "Defended", value = "$defendedCount", color = SeveritySafe)
                            ScoreMetric(label = "Vulnerable", value = "$vulnerableCount", color = SeverityCritical)
                        }

                        if (isSimulating) {
                            Spacer(modifier = Modifier.height(14.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                color = CyberCyan,
                                trackColor = CyberSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.runBatchRedTeamSimulation() },
                            enabled = !isSimulating,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSimulating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF00363D),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Executing Red-Team Vector ${(progress * totalTests).toInt()}/$totalTests...",
                                    color = Color(0xFF00363D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color(0xFF00363D),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Run Batch Red-Team Attack Suite",
                                    color = Color(0xFF00363D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Test Matrix Section
        item {
            SectionHeader(
                title = "Adversarial Attack Scenarios (${tests.size})",
                subtitle = "Predefined injection benchmarks & exploit techniques",
                icon = Icons.Default.Shield
            )
        }

        items(tests, key = { it.id }) { test ->
            RedTeamTestCard(test = test)
        }
    }
}

@Composable
fun ScoreMetric(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RedTeamTestCard(test: RedTeamTestEntity) {
    val isDefended = test.lastRunResult == "DEFENDED"
    val resultColor = if (isDefended) SeveritySafe else SeverityCritical

    CyberCard(
        borderColor = if (test.lastRunResult != "UNTESTED") resultColor.copy(alpha = 0.4f) else CyberBorder
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = test.testName,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Category: ${test.category}",
                        color = CyberCyan,
                        fontSize = 11.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = resultColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, resultColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = test.lastRunResult ?: "UNTESTED",
                        color = resultColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp)),
                color = CyberSurfaceVariant
            ) {
                Text(
                    text = test.attackPayload,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 2,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Expected Defense: ${test.expectedAction}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                if (test.lastRunScore != null) {
                    Text(
                        text = "Risk Score: ${test.lastRunScore}/100",
                        color = resultColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
