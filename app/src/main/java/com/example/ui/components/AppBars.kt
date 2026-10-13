package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedPrepTopBar(
    currentScreen: Screen,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = currentScreen.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "MEDPREP AI • Healthcare College Platform",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                )
            }
        },
        actions = {
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("admin_nav_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Curriculum Admin",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.testTag("profile_nav_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Student Profile",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MedNavyPrimary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun MedPrepBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(Screen.Dashboard, Icons.Default.Dashboard, "Dashboard"),
            Triple(Screen.Syllabus, Icons.Default.MenuBook, "Syllabus"),
            Triple(Screen.ClinicalLab, Icons.Default.LocalHospital, "Clinical Lab"),
            Triple(Screen.AiTutor, Icons.Default.Psychology, "AI Tutor"),
            Triple(Screen.Timetable, Icons.Default.CalendarMonth, "Timetable")
        )

        navItems.forEach { (screen, icon, label) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) MedNavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MedNavyPrimary,
                    selectedTextColor = MedNavyPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("bottom_nav_${screen.route}")
            )
        }
    }
}

@Composable
fun VerifiedBadge(isVerified: Boolean) {
    val bgColor = if (isVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val textColor = if (isVerified) Color(0xFF2E7D32) else Color(0xFFE65100)
    val text = if (isVerified) "VERIFIED CURRICULUM" else "SAMPLE / UNVERIFIED"
    val icon = if (isVerified) Icons.Default.Verified else Icons.Default.HelpOutline

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
