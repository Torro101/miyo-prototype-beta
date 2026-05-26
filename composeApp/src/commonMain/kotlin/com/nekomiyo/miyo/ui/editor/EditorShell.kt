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
    selectedAssetKind: AssetKind,
    onModeSelected: (EditorMode) -> Unit,
    onSimpleTabSelected: (SimpleEditorTab) -> Unit,
    onAssetKindSelected: (AssetKind) -> Unit,
    onSceneSelected: (String, String) -> Unit,
    onActionSelected: (String) -> Unit,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .miyoPatternBackground(baseColor = MiyoColors.InkSoft)
    ) {
        val compactEditor = maxHeight < 560.dp || maxWidth < 760.dp
        val showSidePanels = maxWidth >= 1120.dp && !compactEditor
        val chromePadding = if (compactEditor) MiyoSpacing.xs else MiyoSpacing.md
        Row(modifier = Modifier.fillMaxSize()) {
            EditorRail(
                selectedMode = selectedMode,
                simpleTab = simpleTab,
                compact = compactEditor,
                onModeSelected = onModeSelected,
                onAssetKindSelected = onAssetKindSelected,
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
                if (showSidePanels) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
                    ) {
                        ProjectTreePanel(
                            project = project,
                            selectedBlockId = selectedBlockId,
                            selectedSceneId = selectedSceneId,
                            selectedAssetKind = selectedAssetKind,
                            onSceneSelected = onSceneSelected,
                            onAssetKindSelected = onAssetKindSelected,
                            modifier = Modifier.width(236.dp).fillMaxHeight()
                        )
                        EditorWorkspace(
                            project = project,
                            diagnosticsCount = diagnostics.size,
                            selectedMode = selectedMode,
                            simpleTab = simpleTab,
                            selectedBlockId = selectedBlockId,
                            selectedSceneId = selectedSceneId,
                            selectedActionId = selectedActionId,
                            selectedAssetKind = selectedAssetKind,
                            onSimpleTabSelected = onSimpleTabSelected,
                            onActionSelected = onActionSelected,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        InspectorPanel(
                            project = project,
                            simpleTab = simpleTab,
                            selectedActionId = selectedActionId,
                            selectedAssetKind = selectedAssetKind,
                            modifier = Modifier.width(284.dp).fillMaxHeight()
                        )
                    }
                } else {
                    EditorWorkspace(
                        project = project,
                        diagnosticsCount = diagnostics.size,
                        selectedMode = selectedMode,
                        simpleTab = simpleTab,
                        selectedBlockId = selectedBlockId,
                        selectedSceneId = selectedSceneId,
                        selectedActionId = selectedActionId,
                        selectedAssetKind = selectedAssetKind,
                        onSimpleTabSelected = onSimpleTabSelected,
                        onActionSelected = onActionSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutputDrawer(diagnostics = diagnostics, compact = compactEditor)
            }
        }
    }
}

@Composable
private fun EditorWorkspace(
    project: MiyoProject,
    diagnosticsCount: Int,
    selectedMode: EditorMode,
    simpleTab: SimpleEditorTab,
    selectedBlockId: String?,
    selectedSceneId: String?,
    selectedActionId: String?,
    selectedAssetKind: AssetKind,
    onSimpleTabSelected: (SimpleEditorTab) -> Unit,
    onActionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (selectedMode) {
        EditorMode.Simple -> SimpleModePanel(
            project = project,
            selectedTab = simpleTab,
            selectedSceneId = selectedSceneId,
            selectedActionId = selectedActionId,
            selectedAssetKind = selectedAssetKind,
            onTabSelected = onSimpleTabSelected,
            onActionSelected = onActionSelected,
            modifier = modifier
        )
        EditorMode.Preview -> RuntimePreviewPanel(
            project = project,
            selectedBlockId = selectedBlockId,
            selectedSceneId = selectedSceneId,
            modifier = modifier
        )
        EditorMode.NodeConnect -> NodeConnectPanel(
            project = project,
            diagnosticsCount = diagnosticsCount,
            modifier = modifier
        )
        EditorMode.Code -> CodeModePanel(project = project, modifier = modifier)
    }
}

@Composable
private fun EditorRail(
    selectedMode: EditorMode,
    simpleTab: SimpleEditorTab,
    compact: Boolean,
    onModeSelected: (EditorMode) -> Unit,
    onAssetKindSelected: (AssetKind) -> Unit,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(if (compact) 54.dp else 76.dp)
            .background(MiyoColors.Ink)
            .border(MiyoStroke.hairline, MiyoColors.Outline)
            .padding(vertical = if (compact) MiyoSpacing.xs else MiyoSpacing.md, horizontal = MiyoSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) MiyoSpacing.xxs else MiyoSpacing.xs)
    ) {
        MiyoLogo(size = if (compact) 34.dp else 44.dp)
        Spacer(modifier = Modifier.height(if (compact) MiyoSpacing.xs else MiyoSpacing.md))
        RailButton(MiyoIcons.Timeline, "Scenes", compact = compact, selected = selectedMode == EditorMode.Simple && simpleTab == SimpleEditorTab.Timeline) {
            onModeSelected(EditorMode.Simple)
        }
        RailButton(MiyoIcons.Assets, "Assets", compact = compact, selected = selectedMode == EditorMode.Simple && simpleTab.assetKind != null) {
            onModeSelected(EditorMode.Simple)
            onAssetKindSelected(AssetKind.Character)
        }
        RailButton(MiyoIcons.Preview, "Preview", compact = compact, selected = selectedMode == EditorMode.Preview) {
            onModeSelected(EditorMode.Preview)
        }
        RailButton(MiyoIcons.NodeMode, "Node Connect", compact = compact, selected = selectedMode == EditorMode.NodeConnect) {
            onModeSelected(EditorMode.NodeConnect)
        }
        RailButton(MiyoIcons.CodeMode, "Code", compact = compact, selected = selectedMode == EditorMode.Code) {
            onModeSelected(EditorMode.Code)
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onBackToHub,
            modifier = Modifier
                .clip(RoundedCornerShape(MiyoRadius.lg))
                .background(MiyoColors.Surface)
                .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
        ) {
            Icon(MiyoIcons.Back, contentDescription = "Hub", tint = MiyoColors.TextSecondary)
        }
    }
}

@Composable
private fun RailButton(
    icon: ImageVector,
    label: String,
    compact: Boolean,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val tint = if (selected) MiyoColors.Petal else MiyoColors.TextMuted
    Box(
        modifier = Modifier
            .size(if (compact) 38.dp else 48.dp)
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(if (selected) MiyoColors.Petal.copy(alpha = 0.14f) else Color.Transparent)
            .border(
                MiyoStroke.hairline,
                if (selected) MiyoColors.Petal.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(MiyoRadius.lg)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(if (compact) 20.dp else 24.dp))
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
private fun OutputDrawer(diagnostics: List<MiyoDiagnostic>, compact: Boolean) {
    val firstDiagnostic = diagnostics.firstOrNull()
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
            MiyoIconLabel(
                icon = if (diagnostics.isEmpty()) MiyoIcons.Preview else MiyoIcons.Warning,
                label = if (diagnostics.isEmpty()) "Ready" else "Problems",
                iconTint = tint,
                textColor = MiyoColors.TextPrimary
            )
            Text(
                text = firstDiagnostic?.message ?: "Project data is valid for the current schema.",
                color = MiyoColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.weight(1f))
            if (!compact) {
                MiyoPill("Output", icon = MiyoIcons.CodeMode)
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
