package com.nekomiyo.miyo.ui.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.IconButton
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
import com.nekomiyo.miyo.ui.theme.MiyoColors
import com.nekomiyo.miyo.ui.theme.MiyoRadius
import com.nekomiyo.miyo.ui.theme.MiyoSpacing
import com.nekomiyo.miyo.ui.theme.MiyoStroke

@Composable
fun HubScreen(
    projects: List<ProjectSummary>,
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
                .widthIn(max = 680.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(if (compact) MiyoSpacing.sm else MiyoSpacing.md)
        ) {
            HubHeader(compact = compact)
            SectionTitle("Library", "${projects.size} local projects")
            if (compact) {
                Button(
                    onClick = onCreateProject,
                    colors = ButtonDefaults.buttonColors(containerColor = MiyoColors.Petal, contentColor = MiyoColors.Ink)
                ) {
                    Icon(MiyoIcons.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("New project")
                }
            }
            projects.forEachIndexed { index, project ->
                ProjectCard(
                    project = project,
                    accent = if (index % 2 == 0) MiyoColors.Petal else MiyoColors.Lagoon,
                    onOpenProject = onOpenProject
                )
            }
            if (!compact) {
                SectionTitle("Workshop", "Create and import")
                WorkshopRow(onCreateProject = onCreateProject)
                HubNav()
            }
        }
    }
}

@Composable
private fun HubHeader(compact: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MiyoLogo(size = if (compact) 42.dp else 54.dp)
            Spacer(Modifier.width(MiyoSpacing.sm))
            Column {
                Text(
                    text = "Nekomiyo",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MiyoColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Local visual novel workspace",
                    style = MaterialTheme.typography.labelLarge,
                    color = MiyoColors.TextSecondary
                )
            }
        }
        if (!compact) {
            Row(horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
                HeaderButton(icon = MiyoIcons.Import, label = "Import")
                HeaderButton(icon = MiyoIcons.Settings, label = "Settings")
            }
        }
    }
}

@Composable
private fun HeaderButton(icon: ImageVector, label: String) {
    IconButton(
        onClick = {},
        modifier = Modifier
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(MiyoColors.Surface.copy(alpha = 0.86f))
            .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
            .size(42.dp)
    ) {
        Icon(icon, contentDescription = label, tint = MiyoColors.TextSecondary)
    }
}

@Composable
private fun SectionTitle(title: String, meta: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            color = MiyoColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = meta,
            color = MiyoColors.TextMuted,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ProjectCard(
    project: ProjectSummary,
    accent: Color,
    onOpenProject: (String) -> Unit
) {
    MiyoPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clickable { onOpenProject(project.projectId) },
        containerColor = MiyoColors.SurfaceRaised
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                        MiyoPill("${project.diagnosticsCount} warnings", icon = MiyoIcons.Warning, contentColor = MiyoColors.Honey)
                    }
                }
            }
            Button(
                onClick = { onOpenProject(project.projectId) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent,
                    contentColor = MiyoColors.Ink
                )
            ) {
                Icon(MiyoIcons.Preview, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Open")
            }
        }
    }
}

@Composable
private fun ProjectThumbnail(accent: Color) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(MiyoColors.InkSoft)
            .border(MiyoStroke.hairline, accent.copy(alpha = 0.7f), RoundedCornerShape(MiyoRadius.lg)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(MiyoRadius.md))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(MiyoIcons.Timeline, contentDescription = null, tint = accent)
        }
    }
}

@Composable
private fun WorkshopRow(onCreateProject: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
    ) {
        WorkshopTile(
            icon = MiyoIcons.Add,
            title = "Blank VN",
            tint = MiyoColors.Petal,
            modifier = Modifier.weight(1f),
            onClick = onCreateProject
        )
        WorkshopTile(
            icon = MiyoIcons.Assets,
            title = "Asset Pack",
            tint = MiyoColors.Lagoon,
            modifier = Modifier.weight(1f),
            onClick = {}
        )
    }
}

@Composable
private fun WorkshopTile(
    icon: ImageVector,
    title: String,
    tint: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    MiyoPanel(
        modifier = modifier.clickable(onClick = onClick),
        containerColor = MiyoColors.Surface.copy(alpha = 0.92f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Text(title, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun HubNav() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(MiyoColors.SurfaceRaised.copy(alpha = 0.96f))
            .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
            .padding(MiyoSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HubNavItem(MiyoIcons.Library, "Library", selected = true, modifier = Modifier.weight(1f))
        HubNavItem(MiyoIcons.Workshop, "Workshop", selected = false, modifier = Modifier.weight(1f))
        HubNavItem(MiyoIcons.Settings, "Settings", selected = false, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HubNavItem(icon: ImageVector, label: String, selected: Boolean, modifier: Modifier) {
    val color = if (selected) MiyoColors.Petal else MiyoColors.TextMuted
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(if (selected) MiyoColors.Petal.copy(alpha = 0.14f) else Color.Transparent)
            .padding(horizontal = MiyoSpacing.sm, vertical = MiyoSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
        Text(label, color = color, style = MaterialTheme.typography.labelMedium)
    }
}
