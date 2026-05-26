package com.nekomiyo.miyo.ui.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nekomiyo.miyo.core.model.ProjectSummary
import com.nekomiyo.miyo.ui.design.MiyoIcons
import com.nekomiyo.miyo.ui.design.MiyoLogo
import com.nekomiyo.miyo.ui.design.MiyoPanel
import com.nekomiyo.miyo.ui.design.MiyoPill
import com.nekomiyo.miyo.ui.design.miyoPatternBackground
import com.nekomiyo.miyo.ui.state.HubTab
import com.nekomiyo.miyo.ui.theme.MiyoColors
import com.nekomiyo.miyo.ui.theme.MiyoRadius
import com.nekomiyo.miyo.ui.theme.MiyoSpacing
import com.nekomiyo.miyo.ui.theme.MiyoStroke

@Composable
fun HubScreen(
    projects: List<ProjectSummary>,
    selectedTab: HubTab,
    onTabSelected: (HubTab) -> Unit,
    onCreateProject: () -> Unit,
    onOpenProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .miyoPatternBackground()
            .padding(horizontal = MiyoSpacing.md, vertical = MiyoSpacing.sm)
    ) {
        val compact = maxWidth < 520.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
        ) {
            HubHeader(compact = compact, onCreateProject = onCreateProject)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
            ) {
                when (selectedTab) {
                    HubTab.Home -> HomeContent(projects = projects, onCreateProject = onCreateProject, onOpenProject = onOpenProject)
                    HubTab.Library -> LibraryContent(projects = projects, onOpenProject = onOpenProject)
                    HubTab.Explore -> ExploreContent()
                    HubTab.Settings -> SettingsContent()
                }
            }
            HubNav(selectedTab = selectedTab, onTabSelected = onTabSelected)
        }
    }
}

@Composable
private fun HubHeader(compact: Boolean, onCreateProject: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MiyoLogo(size = if (compact) 38.dp else 48.dp)
            Spacer(Modifier.width(MiyoSpacing.sm))
            Column {
                Text(
                    text = "Nekomiyo",
                    style = MaterialTheme.typography.titleLarge,
                    color = MiyoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Visual novel maker",
                    style = MaterialTheme.typography.labelLarge,
                    color = MiyoColors.TextSecondary
                )
            }
        }
        Button(
            onClick = onCreateProject,
            contentPadding = PaddingValues(horizontal = MiyoSpacing.sm, vertical = MiyoSpacing.xs),
            colors = ButtonDefaults.buttonColors(containerColor = MiyoColors.Petal, contentColor = MiyoColors.Ink)
        ) {
            Icon(MiyoIcons.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(if (compact) "New" else "New project")
        }
    }
}

@Composable
private fun HomeContent(
    projects: List<ProjectSummary>,
    onCreateProject: () -> Unit,
    onOpenProject: (String) -> Unit
) {
    MiyoPanel(containerColor = MiyoColors.SurfaceRaised) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
                Text("Continue", color = MiyoColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text("Open a project or start a clean VN.", color = MiyoColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = onCreateProject,
                colors = ButtonDefaults.buttonColors(containerColor = MiyoColors.Lagoon, contentColor = MiyoColors.Ink)
            ) {
                Icon(MiyoIcons.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Blank")
            }
        }
    }
    projects.take(2).forEachIndexed { index, project ->
        ProjectRow(
            project = project,
            accent = if (index % 2 == 0) MiyoColors.Petal else MiyoColors.Lagoon,
            onOpenProject = onOpenProject
        )
    }
}

@Composable
private fun LibraryContent(projects: List<ProjectSummary>, onOpenProject: (String) -> Unit) {
    SectionTitle("Library", "${projects.size} local projects")
    projects.forEachIndexed { index, project ->
        ProjectRow(
            project = project,
            accent = if (index % 2 == 0) MiyoColors.Petal else MiyoColors.Lagoon,
            onOpenProject = onOpenProject
        )
    }
}

@Composable
private fun ExploreContent() {
    SectionTitle("Explore", "Templates and packs")
    ExploreRow(MiyoIcons.Timeline, "Kocho-style starter", "Chapters, scenes, choices, and VN text boxes.", MiyoColors.Petal)
    ExploreRow(MiyoIcons.Assets, "Background pack", "Register scenery, character sprites, audio, and GUI files.", MiyoColors.Lagoon)
    ExploreRow(MiyoIcons.Settings, "Logic samples", "Variables, conditions, waits, and scene activation examples.", MiyoColors.Honey)
}

@Composable
private fun SettingsContent() {
    SectionTitle("Settings", "Workspace")
    SettingsRow("Editor workflow", "Visual drag-and-drop")
    SettingsRow("Script mode", "Hidden")
    SettingsRow("Preview orientation", "Landscape editor")
    SettingsRow("Autosave", "On")
}

@Composable
private fun SectionTitle(title: String, meta: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(title, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
        Text(meta, color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ProjectRow(
    project: ProjectSummary,
    accent: Color,
    onOpenProject: (String) -> Unit
) {
    MiyoPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clickable { onOpenProject(project.projectId) },
        containerColor = MiyoColors.SurfaceRaised
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ProjectThumbnail(accent)
            Spacer(Modifier.width(MiyoSpacing.md))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
                Text(
                    text = project.title,
                    color = MiyoColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(project.meta, color = MiyoColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
                    MiyoPill(project.autosaveLabel, containerColor = MiyoColors.InkSoft, contentColor = MiyoColors.Mint)
                    if (project.diagnosticsCount > 0) {
                        MiyoPill("${project.diagnosticsCount}", icon = MiyoIcons.Warning, contentColor = MiyoColors.Honey)
                    }
                }
            }
            Icon(MiyoIcons.Preview, contentDescription = "Open", tint = accent, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ProjectThumbnail(accent: Color) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(MiyoColors.InkSoft)
            .border(MiyoStroke.hairline, accent.copy(alpha = 0.7f), RoundedCornerShape(MiyoRadius.lg)),
        contentAlignment = Alignment.Center
    ) {
        Icon(MiyoIcons.Timeline, contentDescription = null, tint = accent, modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun ExploreRow(icon: ImageVector, title: String, detail: String, tint: Color) {
    MiyoPanel(modifier = Modifier.fillMaxWidth(), containerColor = MiyoColors.SurfaceRaised) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(MiyoSpacing.md))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
                Text(title, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(detail, color = MiyoColors.TextMuted, style = MaterialTheme.typography.bodySmall)
            }
            Text(">", color = MiyoColors.TextMuted, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    MiyoPanel(modifier = Modifier.fillMaxWidth(), containerColor = MiyoColors.SurfaceRaised) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.bodyMedium)
            MiyoPill(value, contentColor = MiyoColors.TextSecondary)
        }
    }
}

@Composable
private fun HubNav(selectedTab: HubTab, onTabSelected: (HubTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(MiyoColors.SurfaceRaised)
            .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
            .padding(MiyoSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)
    ) {
        HubNavItem(MiyoIcons.Hub, "Home", HubTab.Home, selectedTab, onTabSelected, Modifier.weight(1f))
        HubNavItem(MiyoIcons.Library, "Library", HubTab.Library, selectedTab, onTabSelected, Modifier.weight(1f))
        HubNavItem(MiyoIcons.Workshop, "Explore", HubTab.Explore, selectedTab, onTabSelected, Modifier.weight(1f))
        HubNavItem(MiyoIcons.Settings, "Settings", HubTab.Settings, selectedTab, onTabSelected, Modifier.weight(1f))
    }
}

@Composable
private fun HubNavItem(
    icon: ImageVector,
    label: String,
    tab: HubTab,
    selectedTab: HubTab,
    onTabSelected: (HubTab) -> Unit,
    modifier: Modifier
) {
    val selected = tab == selectedTab
    val color = if (selected) MiyoColors.Petal else MiyoColors.TextMuted
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(if (selected) MiyoColors.Petal.copy(alpha = 0.14f) else Color.Transparent)
            .clickable { onTabSelected(tab) }
            .padding(horizontal = MiyoSpacing.xs, vertical = MiyoSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xxs)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
        Text(label, color = color, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}
