package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GatewayScreen
import com.example.ui.screens.IncidentsScreen
import com.example.ui.screens.RAGScannerScreen
import com.example.ui.screens.RedTeamScreen
import com.example.ui.screens.SecOpsScreen
import com.example.ui.theme.AIShieldTheme
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SeveritySafe
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AIShieldViewModel
import com.example.ui.viewmodel.AppTab

class MainActivity : ComponentActivity() {
    private val viewModel: AIShieldViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIShieldTheme {
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
                var roleDropdownExpanded by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyberCyan.copy(alpha = 0.15f))
                                            .border(1.dp, CyberCyan, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = "AIShield Logo",
                                            tint = CyberCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "AIShield",
                                            color = TextPrimary,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "AI Threat Firewall & SecOps",
                                            color = CyberCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            },
                            actions = {
                                Box {
                                    Surface(
                                        onClick = { roleDropdownExpanded = true },
                                        shape = RoundedCornerShape(20.dp),
                                        color = CyberSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Role",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = currentRole.roleName.split(" ").first(),
                                                color = TextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = roleDropdownExpanded,
                                        onDismissRequest = { roleDropdownExpanded = false },
                                        modifier = Modifier.background(CyberSurfaceVariant)
                                    ) {
                                        UserRole.values().forEach { role ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = role.roleName,
                                                            color = if (role == currentRole) CyberCyan else TextPrimary,
                                                            fontSize = 13.sp,
                                                            fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                        Text(
                                                            text = role.description,
                                                            color = TextMuted,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.setRole(role)
                                                    roleDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = CyberBackground
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = CyberSurface,
                            tonalElevation = 0.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == AppTab.DASHBOARD,
                                onClick = { viewModel.setTab(AppTab.DASHBOARD) },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("Dashboard", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00363D),
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.GATEWAY,
                                onClick = { viewModel.setTab(AppTab.GATEWAY) },
                                icon = { Icon(Icons.Default.Bolt, contentDescription = "Gateway") },
                                label = { Text("Gateway", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00363D),
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.INCIDENTS,
                                onClick = { viewModel.setTab(AppTab.INCIDENTS) },
                                icon = { Icon(Icons.Default.Warning, contentDescription = "Incidents") },
                                label = { Text("Incidents", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00363D),
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.RAG_SCANNER,
                                onClick = { viewModel.setTab(AppTab.RAG_SCANNER) },
                                icon = { Icon(Icons.Default.Psychology, contentDescription = "RAG") },
                                label = { Text("RAG Scan", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00363D),
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.RED_TEAM,
                                onClick = { viewModel.setTab(AppTab.RED_TEAM) },
                                icon = { Icon(Icons.Default.GppGood, contentDescription = "Red Team") },
                                label = { Text("Red Team", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00363D),
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.MORE,
                                onClick = { viewModel.setTab(AppTab.MORE) },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "SecOps") },
                                label = { Text("SecOps", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00363D),
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                            AppTab.GATEWAY -> GatewayScreen(viewModel = viewModel)
                            AppTab.INCIDENTS -> IncidentsScreen(viewModel = viewModel)
                            AppTab.RAG_SCANNER -> RAGScannerScreen(viewModel = viewModel)
                            AppTab.RED_TEAM -> RedTeamScreen(viewModel = viewModel)
                            AppTab.MORE -> SecOpsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
