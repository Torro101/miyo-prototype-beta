package com.nekomiyo.miyo.ui.editor

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.nekomiyo.miyo.core.model.AssetKind
import com.nekomiyo.miyo.core.model.InteractiveAreaShape
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.SceneAction
import com.nekomiyo.miyo.core.model.findAction
import com.nekomiyo.miyo.core.model.primaryText
import com.nekomiyo.miyo.core.model.selectedBlock
import com.nekomiyo.miyo.core.model.selectedScene
import com.nekomiyo.miyo.core.validation.DiagnosticSeverity
import com.nekomiyo.miyo.core.validation.MiyoDiagnostic
import com.nekomiyo.miyo.ui.design.MiyoIconLabel
import com.nekomiyo.miyo.ui.design.MiyoIcons
import com.nekomiyo.miyo.ui.design.MiyoLogo
import com.nekomiyo.miyo.ui.design.MiyoPanel
import com.nekomiyo.miyo.ui.design.MiyoPill
import com.nekomiyo.miyo.ui.design.miyoPatternBackground
import com.nekomiyo.miyo.ui.design.toUiColor
import com.nekomiyo.miyo.ui.state.EditorMode
import com.nekomiyo.miyo.ui.state.SimpleEditorTab
import com.nekomiyo.miyo.ui.theme.MiyoColors
import com.nekomiyo.miyo.ui.theme.MiyoRadius
import com.nekomiyo.miyo.ui.theme.MiyoSpacing
import com.nekomiyo.miyo.ui.theme.MiyoStroke

@Composable
fun EditorShell(
    project: MiyoProject,
    diagnostics: List<MiyoDiagnostic>,
    selectedMode: EditorMode,
    simpleTab: SimpleEditorTab,
    selectedBlockId: String?,
    selectedSceneId: String?,
    selectedActionId: String?,
    selectedAreaId: String?,
    selectedAssetKind: AssetKind,
    onModeSelected: (EditorMode) -> Unit,
    onSimpleTabSelected: (SimpleEditorTab) -> Unit,
    onAssetKindSelected: (AssetKind) -> Unit,
    onSceneSelected: (String, String) -> Unit,
    onActionSelected: (String) -> Unit,
    onAreaSelected: (String) -> Unit,
    onAddInteractiveArea: (String, InteractiveAreaShape) -> Unit,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .miyoPatternBackground(baseColor = MiyoColors.InkSoft)
    ) {
        val compactEditor = maxHeight < 560.dp || maxWidth < 760.dp
        val chromePadding = if (compactEditor) MiyoSpacing.xs else MiyoSpacing.md
        Row(modifier = Modifier.fillMaxSize()) {
            EditorSidebar(
                project = project,
                selectedMode = selectedMode,
                simpleTab = simpleTab,
                selectedBlockId = selectedBlockId,
                selectedSceneId = selectedSceneId,
                compact = compactEditor,
                onModeSelected = onModeSelected,
                onSimpleTabSelected = onSimpleTabSelected,
                onAssetKindSelected = onAssetKindSelected,
                onSceneSelected = onSceneSelected,
                onBackToHub = onBackToHub
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(chromePadding),
                verticalArrangement = Arrangement.spacedBy(if (compactEditor) MiyoSpacing.xs else MiyoSpacing.sm)
            ) {
                EditorTopBar(
                    project = project,
                    diagnostics = diagnostics,
                    selectedBlockId = selectedBlockId,
                    selectedSceneId = selectedSceneId,
                    compact = compactEditor,
                    onPreviewRequested = { onModeSelected(EditorMode.Preview) }
                )
                if (!compactEditor) {
                    ModeTabs(
                        selectedMode = selectedMode,
                        onModeSelected = onModeSelected
                    )
                }
                EditorWorkspace(
                    project = project,
                    selectedMode = selectedMode,
                    simpleTab = simpleTab,
                    selectedBlockId = selectedBlockId,
                    selectedSceneId = selectedSceneId,
                    selectedActionId = selectedActionId,
                    selectedAreaId = selectedAreaId,
                    selectedAssetKind = selectedAssetKind,
                    onSimpleTabSelected = onSimpleTabSelected,
                    onActionSelected = onActionSelected,
                    onAreaSelected = onAreaSelected,
                    onAddInteractiveArea = onAddInteractiveArea,
                    modifier = Modifier.weight(1f)
                )
                EditorContextBar(
                    project = project,
                    diagnostics = diagnostics,
                    selectedBlockId = selectedBlockId,
                    selectedSceneId = selectedSceneId,
                    simpleTab = simpleTab,
                    compact = compactEditor
                )
            }
        }
    }
}

@Composable
private fun EditorWorkspace(
    project: MiyoProject,
    selectedMode: EditorMode,
    simpleTab: SimpleEditorTab,
    selectedBlockId: String?,
    selectedSceneId: String?,
    selectedActionId: String?,
    selectedAreaId: String?,
    selectedAssetKind: AssetKind,
    onSimpleTabSelected: (SimpleEditorTab) -> Unit,
    onActionSelected: (String) -> Unit,
    onAreaSelected: (String) -> Unit,
    onAddInteractiveArea: (String, InteractiveAreaShape) -> Unit,
    modifier: Modifier = Modifier
) {
    when (selectedMode) {
        EditorMode.Edit -> SimpleModePanel(
            project = project,
            selectedTab = simpleTab,
            selectedSceneId = selectedSceneId,
            selectedActionId = selectedActionId,
            selectedAreaId = selectedAreaId,
            selectedAssetKind = selectedAssetKind,
            onTabSelected = onSimpleTabSelected,
            onActionSelected = onActionSelected,
            onAreaSelected = onAreaSelected,
            onAddInteractiveArea = onAddInteractiveArea,
            modifier = modifier
        )
        EditorMode.Preview -> RuntimePreviewPanel(
            project = project,
            selectedBlockId = selectedBlockId,
            selectedSceneId = selectedSceneId,
            modifier = modifier
        )
    }
}

@Composable
private fun EditorSidebar(
    project: MiyoProject,
    selectedMode: EditorMode,
    simpleTab: SimpleEditorTab,
    selectedBlockId: String?,
    selectedSceneId: String?,
    compact: Boolean,
    onModeSelected: (EditorMode) -> Unit,
    onSimpleTabSelected: (SimpleEditorTab) -> Unit,
    onAssetKindSelected: (AssetKind) -> Unit,
    onSceneSelected: (String, String) -> Unit,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(if (compact) 154.dp else 190.dp)
            .background(MiyoColors.Ink)
            .border(MiyoStroke.hairline, MiyoColors.Outline)
            .padding(if (compact) MiyoSpacing.xs else MiyoSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MiyoLogo(size = if (compact) 30.dp else 36.dp)
            Spacer(Modifier.width(MiyoSpacing.xs))
            Column {
                Text("Workspace", color = MiyoColors.TextPrimary, style = MaterialTheme.typography.labelLarge)
                Text(project.displayTitle(), color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)
        ) {
            SidebarSection("Mode")
            SidebarItem(MiyoIcons.SimpleMode, "Kocho edit", selectedMode == EditorMode.Edit, onClick = { onModeSelected(EditorMode.Edit) })
            SidebarItem(MiyoIcons.Preview, "Preview", selectedMode == EditorMode.Preview, onClick = { onModeSelected(EditorMode.Preview) })

            SidebarSection("Story")
            SidebarItem(MiyoIcons.Timeline, "Chapters", simpleTab == SimpleEditorTab.Timeline) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Timeline)
            }
            SidebarItem(MiyoIcons.TextAction, "Scenes", simpleTab == SimpleEditorTab.Timeline) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Timeline)
            }
            SidebarItem(MiyoIcons.ChoiceAction, "Choices", false) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Timeline)
            }
            StoryNavigator(
                project = project,
                selectedBlockId = selectedBlockId,
                selectedSceneId = selectedSceneId,
                onSelectScene = { blockId, sceneId ->
                    onModeSelected(EditorMode.Edit)
                    onSimpleTabSelected(SimpleEditorTab.Timeline)
                    onSceneSelected(blockId, sceneId)
                }
            )

            SidebarSection("Logic")
            SidebarItem(MiyoIcons.Settings, "Variables", simpleTab == SimpleEditorTab.Variables) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Variables)
            }
            SidebarItem(MiyoIcons.Back, "Conditions", simpleTab == SimpleEditorTab.Conditions) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Conditions)
            }
            SidebarItem(MiyoIcons.Inspector, "2D collision", simpleTab == SimpleEditorTab.Areas) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Areas)
            }

            SidebarSection("Assets")
            SidebarItem(MiyoIcons.Assets, "File manager", simpleTab == SimpleEditorTab.Files) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Files)
            }
            SidebarItem(MiyoIcons.CharacterAction, "Characters", simpleTab == SimpleEditorTab.Characters) {
                onModeSelected(EditorMode.Edit)
                onAssetKindSelected(AssetKind.Character)
            }
            SidebarItem(MiyoIcons.BackgroundAction, "Scenery", simpleTab == SimpleEditorTab.Scenery) {
                onModeSelected(EditorMode.Edit)
                onAssetKindSelected(AssetKind.Scenery)
            }
            SidebarItem(MiyoIcons.SoundAction, "Audio", simpleTab == SimpleEditorTab.Bgm || simpleTab == SimpleEditorTab.Sfx) {
                onModeSelected(EditorMode.Edit)
                onAssetKindSelected(AssetKind.Bgm)
            }

            SidebarSection("UI")
            SidebarItem(MiyoIcons.Inspector, "Message boxes", simpleTab == SimpleEditorTab.Gui) {
                onModeSelected(EditorMode.Edit)
                onSimpleTabSelected(SimpleEditorTab.Gui)
            }
        }
        SidebarStatus(project = project, selectedBlockId = selectedBlockId, selectedSceneId = selectedSceneId)
        SidebarItem(MiyoIcons.Back, "Home", false, onClick = onBackToHub)
    }
}

@Composable
private fun StoryNavigator(
    project: MiyoProject,
    selectedBlockId: String?,
    selectedSceneId: String?,
    onSelectScene: (String, String) -> Unit
) {
    SidebarSection("Volume")
    NavigatorRow(
        label = "Volume 1",
        detail = project.displayTitle(),
        icon = MiyoIcons.Library,
        selected = false,
        enabled = false,
        depth = 0
    )
    project.story.blocks.forEachIndexed { index, block ->
        val blockSelected = block.id == selectedBlockId
        NavigatorRow(
            label = "Chapter ${index + 1}",
            detail = block.label,
            icon = MiyoIcons.Timeline,
            selected = blockSelected,
            depth = 1,
            onClick = {
                block.scenes.firstOrNull()?.let { scene ->
                    onSelectScene(block.id, scene.id)
                }
            }
        )
        block.scenes.forEachIndexed { sceneIndex, scene ->
            NavigatorRow(
                label = "Scene ${sceneIndex + 1}",
                detail = scene.title,
                icon = MiyoIcons.TextAction,
                selected = block.id == selectedBlockId && scene.id == selectedSceneId,
                depth = 2,
                onClick = { onSelectScene(block.id, scene.id) }
            )
        }
    }
}

@Composable
private fun NavigatorRow(
    label: String,
    detail: String,
    icon: ImageVector,
    selected: Boolean,
    enabled: Boolean = true,
    depth: Int,
    onClick: () -> Unit = {}
) {
    val color = when {
        selected -> MiyoColors.Petal
        enabled -> MiyoColors.TextSecondary
        else -> MiyoColors.TextMuted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(if (selected) MiyoColors.Petal.copy(alpha = 0.13f) else Color.Transparent)
            .border(
                MiyoStroke.hairline,
                if (selected) MiyoColors.Petal.copy(alpha = 0.46f) else Color.Transparent,
                RoundedCornerShape(MiyoRadius.md)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(start = MiyoSpacing.xs + (depth * 10).dp, end = MiyoSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(MiyoSpacing.xs))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xxs)) {
            Text(label, color = color, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(detail, color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(">", color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SidebarSection(label: String) {
    Text(
        text = label,
        color = MiyoColors.TextMuted,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(top = MiyoSpacing.xs, start = MiyoSpacing.xs)
    )
}

@Composable
private fun SidebarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) MiyoColors.Petal else MiyoColors.TextSecondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(if (selected) MiyoColors.Petal.copy(alpha = 0.14f) else Color.Transparent)
            .border(
                width = MiyoStroke.hairline,
                color = if (selected) MiyoColors.Petal.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(MiyoRadius.md)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = MiyoSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(MiyoSpacing.xs))
        Text(label, color = color, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(">", color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SidebarStatus(project: MiyoProject, selectedBlockId: String?, selectedSceneId: String?) {
    val block = project.selectedBlock(selectedBlockId)
    val scene = project.selectedScene(selectedBlockId, selectedSceneId)
    MiyoPanel(containerColor = MiyoColors.Surface, contentPadding = PaddingValues(MiyoSpacing.xs)) {
        Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xxs)) {
            Text("Current", color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium)
            Text(block?.label ?: "No chapter", color = MiyoColors.TextPrimary, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(scene?.title ?: "No scene", color = MiyoColors.TextSecondary, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun EditorTopBar(
    project: MiyoProject,
    diagnostics: List<MiyoDiagnostic>,
    selectedBlockId: String?,
    selectedSceneId: String?,
    compact: Boolean,
    onPreviewRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val block = project.selectedBlock(selectedBlockId)
    val scene = project.selectedScene(selectedBlockId, selectedSceneId)
    val accent = block?.color?.toUiColor() ?: MiyoColors.Petal

    MiyoPanel(
        modifier = modifier.fillMaxWidth(),
        containerColor = MiyoColors.SurfaceRaised,
        contentPadding = PaddingValues(
            horizontal = if (compact) MiyoSpacing.sm else MiyoSpacing.md,
            vertical = if (compact) MiyoSpacing.xs else MiyoSpacing.sm
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                ProjectDot(accent, compact = compact)
                Spacer(Modifier.width(MiyoSpacing.sm))
                Column {
                    Text(
                        text = project.displayTitle(),
                        color = MiyoColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${block?.label ?: "No chapter"} / ${scene?.title ?: "No scene"}",
                        color = MiyoColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                if (!compact) {
                    MiyoPill(project.editor.autosaveLabel, icon = MiyoIcons.Export, contentColor = MiyoColors.Mint)
                }
                if (diagnostics.isNotEmpty() && !compact) {
                    MiyoPill("${diagnostics.size} diagnostics", icon = MiyoIcons.Warning, contentColor = MiyoColors.Honey)
                }
                Button(
                    onClick = onPreviewRequested,
                    contentPadding = PaddingValues(
                        horizontal = if (compact) MiyoSpacing.sm else MiyoSpacing.lg,
                        vertical = if (compact) MiyoSpacing.xs else MiyoSpacing.sm
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = MiyoColors.Ink
                    )
                ) {
                    Icon(MiyoIcons.Preview, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(MiyoSpacing.xs))
                    Text("Preview")
                }
            }
        }
    }
}

@Composable
private fun ProjectDot(color: Color, compact: Boolean) {
    Box(
        modifier = Modifier
            .size(if (compact) 30.dp else 36.dp)
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(color.copy(alpha = 0.16f))
            .border(MiyoStroke.hairline, color.copy(alpha = 0.58f), RoundedCornerShape(MiyoRadius.md)),
        contentAlignment = Alignment.Center
    ) {
        Icon(MiyoIcons.Timeline, contentDescription = null, tint = color, modifier = Modifier.size(if (compact) 17.dp else 20.dp))
    }
}

@Composable
private fun ProjectTreePanel(
    project: MiyoProject,
    selectedBlockId: String?,
    selectedSceneId: String?,
    selectedAssetKind: AssetKind,
    onSceneSelected: (String, String) -> Unit,
    onAssetKindSelected: (AssetKind) -> Unit,
    modifier: Modifier = Modifier
) {
    MiyoPanel(modifier = modifier, containerColor = MiyoColors.Surface.copy(alpha = 0.96f)) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
        ) {
            MiyoIconLabel(MiyoIcons.Library, "Project", iconTint = MiyoColors.Petal, textColor = MiyoColors.TextPrimary)
            project.story.blocks.forEach { block ->
                TreeItem(
                    icon = MiyoIcons.Timeline,
                    label = block.label,
                    selected = block.id == selectedBlockId,
                    tint = block.color.toUiColor()
                )
                block.scenes.forEach { scene ->
                    TreeItem(
                        icon = MiyoIcons.TextAction,
                        label = scene.title,
                        selected = block.id == selectedBlockId && scene.id == selectedSceneId,
                        indent = true,
                        onClick = { onSceneSelected(block.id, scene.id) }
                    )
                }
            }
            Spacer(Modifier.height(MiyoSpacing.sm))
            MiyoIconLabel(MiyoIcons.Assets, "Assets", iconTint = MiyoColors.Lagoon, textColor = MiyoColors.TextPrimary)
            listOf(AssetKind.Character, AssetKind.Scenery, AssetKind.Bgm, AssetKind.Sfx, AssetKind.Cutscene, AssetKind.Gui).forEach { kind ->
                TreeItem(
                    icon = kind.icon,
                    label = "${kind.label} (${project.assets.byKind(kind).size})",
                    selected = kind == selectedAssetKind,
                    indent = true,
                    onClick = { onAssetKindSelected(kind) }
                )
            }
        }
    }
}

@Composable
private fun TreeItem(
    icon: ImageVector,
    label: String,
    selected: Boolean = false,
    indent: Boolean = false,
    tint: Color = MiyoColors.TextSecondary,
    onClick: () -> Unit = {}
) {
    val color = if (selected) MiyoColors.Petal else tint
    MiyoIconLabel(
        icon = icon,
        label = label,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(if (selected) MiyoColors.Petal.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(
                start = if (indent) MiyoSpacing.md else MiyoSpacing.xs,
                top = MiyoSpacing.xs,
                end = MiyoSpacing.xs,
                bottom = MiyoSpacing.xs
            ),
        iconTint = color,
        textColor = color
    )
}

@Composable
private fun InspectorPanel(
    project: MiyoProject,
    simpleTab: SimpleEditorTab,
    selectedActionId: String?,
    selectedAssetKind: AssetKind,
    modifier: Modifier = Modifier
) {
    val action = project.findAction(selectedActionId)
    val asset = simpleTab.assetKind?.let { kind -> project.assets.byKind(kind).firstOrNull() }

    MiyoPanel(modifier = modifier, containerColor = MiyoColors.Surface.copy(alpha = 0.96f)) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MiyoSpacing.md)
        ) {
            MiyoIconLabel(MiyoIcons.Inspector, "Inspector", iconTint = MiyoColors.Lagoon, textColor = MiyoColors.TextPrimary)
            when {
                simpleTab == SimpleEditorTab.Gui -> {
                    InspectorField("Theme", project.guiTheme.fontFamily)
                    InspectorField("Font size", project.guiTheme.fontSize.toString())
                    InspectorField("Message box", project.guiTheme.messageBoxColor.name)
                    InspectorField("Choice box", project.guiTheme.choiceBoxColor.name)
                }
                asset != null -> {
                    InspectorField("Asset type", selectedAssetKind.label)
                    InspectorField("Name", asset.displayName)
                    InspectorField("Path", asset.relativePath)
                    InspectorField("Status", asset.status.label)
                }
                action != null -> {
                    InspectorField("Selection", action.label)
                    InspectorField("Summary", action.primaryText(project))
                    if (action is SceneAction.Dialogue) {
                        InspectorField("Speaker", action.speaker ?: "Narrator")
                        InspectorField("Expression", action.expression ?: "Default")
                    }
                    if (action is SceneAction.Choice) {
                        InspectorField("Options", action.options.size.toString())
                    }
                }
                else -> {
                    InspectorField("Project", project.displayTitle())
                    InspectorField("Canvas", "${project.settings.canvasWidth} x ${project.settings.canvasHeight}")
                    InspectorField("Locale", project.defaultLocale)
                }
            }
        }
    }
}

@Composable
private fun InspectorField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
        Text(label, color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MiyoRadius.md))
                .background(MiyoColors.InkSoft)
                .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.md))
                .padding(horizontal = MiyoSpacing.sm, vertical = MiyoSpacing.xs)
        ) {
            Text(value, color = MiyoColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EditorContextBar(
    project: MiyoProject,
    diagnostics: List<MiyoDiagnostic>,
    selectedBlockId: String?,
    selectedSceneId: String?,
    simpleTab: SimpleEditorTab,
    compact: Boolean
) {
    val firstDiagnostic = diagnostics.firstOrNull()
    val block = project.selectedBlock(selectedBlockId)
    val scene = project.selectedScene(selectedBlockId, selectedSceneId)
    val tint = when (firstDiagnostic?.severity) {
        DiagnosticSeverity.Error -> MiyoColors.Danger
        DiagnosticSeverity.Warning -> MiyoColors.Honey
        DiagnosticSeverity.Info -> MiyoColors.Lagoon
        null -> MiyoColors.Mint
    }

    MiyoPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compact) 46.dp else 78.dp),
        containerColor = MiyoColors.Ink,
        contentPadding = PaddingValues(horizontal = MiyoSpacing.sm, vertical = if (compact) MiyoSpacing.xs else MiyoSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(if (compact) MiyoSpacing.sm else MiyoSpacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiyoPill("Volume 1", icon = MiyoIcons.Library, contentColor = MiyoColors.Lagoon)
            MiyoPill(block?.label ?: "Chapter", icon = MiyoIcons.Timeline, contentColor = MiyoColors.Petal)
            MiyoPill(scene?.title ?: "Scene", icon = MiyoIcons.TextAction, contentColor = MiyoColors.Mint)
            MiyoPill(simpleTab.label, icon = MiyoIcons.Inspector, contentColor = MiyoColors.TextSecondary)
            if (!compact) {
                Text(
                    text = firstDiagnostic?.message ?: "Ready",
                    color = tint,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.weight(1f))
            if (!compact) {
                MiyoPill(project.editor.autosaveLabel, icon = MiyoIcons.Export, contentColor = MiyoColors.Mint)
            }
        }
    }
}

private val AssetKind.icon: ImageVector
    get() = when (this) {
        AssetKind.Character -> MiyoIcons.CharacterAction
        AssetKind.Scenery -> MiyoIcons.BackgroundAction
        AssetKind.Bgm -> MiyoIcons.SoundAction
        AssetKind.Sfx -> MiyoIcons.SoundAction
        AssetKind.Cutscene -> MiyoIcons.Preview
        AssetKind.Gui -> MiyoIcons.Inspector
        AssetKind.Font -> MiyoIcons.TextAction
        AssetKind.Other -> MiyoIcons.Assets
    }
