package com.nekomiyo.miyo.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nekomiyo.miyo.core.exporting.MiyoExportPlan
import com.nekomiyo.miyo.core.exporting.MiyoExportPlanner
import com.nekomiyo.miyo.core.model.AssetKind
import com.nekomiyo.miyo.core.model.AudioChannel
import com.nekomiyo.miyo.core.model.MiyoAsset
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.SceneAction
import com.nekomiyo.miyo.core.model.StoryScene
import com.nekomiyo.miyo.core.model.emptyPrompt
import com.nekomiyo.miyo.core.model.findAsset
import com.nekomiyo.miyo.core.model.primaryText
import com.nekomiyo.miyo.core.model.selectedScene
import com.nekomiyo.miyo.core.runtime.MiyoRuntimeEvent
import com.nekomiyo.miyo.core.runtime.MiyoRuntimePreviewState
import com.nekomiyo.miyo.core.runtime.toRuntimePreviewState
import com.nekomiyo.miyo.core.script.MiyoScriptCompiler
import com.nekomiyo.miyo.core.script.MiyoScriptDiagnostic
import com.nekomiyo.miyo.core.script.MiyoScriptFormatter
import com.nekomiyo.miyo.nodeconnect.MiyoNodeBridgeHost
import com.nekomiyo.miyo.nodeconnect.MiyoNodeConnectView
import com.nekomiyo.miyo.nodeconnect.toGraphSnapshot
import com.nekomiyo.miyo.ui.design.MiyoIconLabel
import com.nekomiyo.miyo.ui.design.MiyoIcons
import com.nekomiyo.miyo.ui.design.MiyoPanel
import com.nekomiyo.miyo.ui.design.MiyoPill
import com.nekomiyo.miyo.ui.design.toUiColor
import com.nekomiyo.miyo.ui.state.SimpleEditorTab
import com.nekomiyo.miyo.ui.theme.MiyoColors
import com.nekomiyo.miyo.ui.theme.MiyoRadius
import com.nekomiyo.miyo.ui.theme.MiyoSpacing
import com.nekomiyo.miyo.ui.theme.MiyoStroke

@Composable
fun SimpleModePanel(
    project: MiyoProject,
    selectedTab: SimpleEditorTab,
    selectedSceneId: String?,
    selectedActionId: String?,
    selectedAssetKind: AssetKind,
    onTabSelected: (SimpleEditorTab) -> Unit,
    onActionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    WorkspacePanel(
        title = "Simple",
        subtitle = project.displayTitle(),
        icon = MiyoIcons.SimpleMode,
        tint = MiyoColors.Petal,
        modifier = modifier
    ) {
        SimpleTabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)
        when (selectedTab) {
            SimpleEditorTab.Timeline -> TimelineEditor(
                project = project,
                selectedSceneId = selectedSceneId,
                selectedActionId = selectedActionId,
                onActionSelected = onActionSelected
            )
            SimpleEditorTab.Variables -> VariablesEditor(project)
            SimpleEditorTab.Gui -> GuiEditor(project)
            else -> AssetLibraryEditor(
                project = project,
                assetKind = selectedTab.assetKind ?: selectedAssetKind
            )
        }
    }
}

@Composable
fun NodeConnectPanel(
    project: MiyoProject,
    diagnosticsCount: Int,
    modifier: Modifier = Modifier
) {
    val graphSnapshot = project.toGraphSnapshot(diagnosticsCount = diagnosticsCount)
    val hostSnapshotJson = graphSnapshot.toHostLoadGraphEnvelopeJson()

    MiyoPanel(
        modifier = modifier,
        containerColor = MiyoColors.Surface,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MiyoNodeConnectView(
                bridgeHost = object : MiyoNodeBridgeHost {
                    override fun onNodeBridgeMessage(messageJson: String) {
                        // Graph messages are routed to editor commands in the next pass.
                    }
                },
                hostSnapshotJson = hostSnapshotJson
            )
            MiyoPill(
                text = "${graphSnapshot.nodes.size} nodes / ${graphSnapshot.edges.size} edges",
                icon = MiyoIcons.NodeMode,
                contentColor = MiyoColors.Lagoon,
                containerColor = MiyoColors.Ink.copy(alpha = 0.86f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MiyoSpacing.md)
            )
        }
    }
}

@Composable
fun CodeModePanel(
    project: MiyoProject,
    modifier: Modifier = Modifier
) {
    val source = MiyoScriptFormatter.format(project)
    val compileResult = MiyoScriptCompiler.compileActions(source)
    val exportPlan = MiyoExportPlanner.plan(project)

    WorkspacePanel(
        title = "Code",
        subtitle = "MiyoScript mirror",
        icon = MiyoIcons.CodeMode,
        tint = MiyoColors.Lagoon,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.md)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(MiyoRadius.lg))
                    .background(MiyoColors.Ink)
                    .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
                    .verticalScroll(rememberScrollState())
                    .padding(MiyoSpacing.md),
                verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)
            ) {
                source.lines().forEach { line ->
                    CodeLine(line, line.scriptLineColor())
                }
            }
            Column(
                modifier = Modifier
                    .width(312.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
            ) {
                ScriptDiagnosticsPanel(
                    diagnostics = compileResult.diagnostics,
                    actionCount = compileResult.actions.size
                )
                ExportPlanPanel(exportPlan)
            }
        }
    }
}

@Composable
fun RuntimePreviewPanel(
    project: MiyoProject,
    selectedBlockId: String?,
    selectedSceneId: String?,
    modifier: Modifier = Modifier
) {
    val preview = project.toRuntimePreviewState(blockId = selectedBlockId, sceneId = selectedSceneId)

    WorkspacePanel(
        title = "Preview",
        subtitle = "${preview.blockTitle} / ${preview.sceneTitle}",
        icon = MiyoIcons.Preview,
        tint = MiyoColors.Mint,
        modifier = modifier
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            val compact = maxHeight < 340.dp || maxWidth < 760.dp
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(if (compact) MiyoSpacing.sm else MiyoSpacing.md)
            ) {
                RuntimeStage(
                    preview = preview,
                    project = project,
                    stageHeight = if (compact) 170.dp else 300.dp,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                RuntimeTimeline(
                    preview = preview,
                    modifier = Modifier.width(if (compact) 280.dp else 320.dp).fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun RuntimeStage(
    preview: MiyoRuntimePreviewState,
    project: MiyoProject,
    stageHeight: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MiyoSpacing.md)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(stageHeight)
                .clip(RoundedCornerShape(MiyoRadius.lg))
                .background(Color.Black)
                .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
                .padding(MiyoSpacing.md)
        ) {
            MiyoPill(
                text = preview.backgroundName ?: "No scenery",
                icon = MiyoIcons.BackgroundAction,
                contentColor = MiyoColors.Lagoon,
                containerColor = MiyoColors.Ink.copy(alpha = 0.82f),
                modifier = Modifier.align(Alignment.TopStart)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.86f)
                    .clip(RoundedCornerShape(MiyoRadius.md))
                    .background(project.guiTheme.messageBoxColor.toUiColor().copy(alpha = 0.82f))
                    .border(MiyoStroke.hairline, Color.White.copy(alpha = 0.18f), RoundedCornerShape(MiyoRadius.md))
                    .padding(MiyoSpacing.md)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
                    preview.speaker?.let { speaker ->
                        Text(
                            text = speaker,
                            color = MiyoColors.Lagoon,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = preview.dialogue,
                        color = project.guiTheme.messageTextColor.toUiColor(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        if (preview.choices.isEmpty()) {
            MiyoPill("No visible choices in this scene", icon = MiyoIcons.ChoiceAction, contentColor = MiyoColors.TextMuted)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
            ) {
                preview.choices.forEachIndexed { index, choice ->
                    MiyoPanel(
                        modifier = Modifier.weight(1f),
                        containerColor = project.guiTheme.choiceBoxColor.toUiColor().copy(alpha = 0.94f),
                        contentPadding = PaddingValues(MiyoSpacing.sm)
                    ) {
                        Text(
                            text = "${index + 1}. $choice",
                            color = MiyoColors.TextPrimary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RuntimeTimeline(
    preview: MiyoRuntimePreviewState,
    modifier: Modifier = Modifier
) {
    MiyoPanel(
        modifier = modifier,
        containerColor = MiyoColors.InkSoft
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
        ) {
            MiyoIconLabel(MiyoIcons.Timeline, "Playback Order", iconTint = MiyoColors.Mint, textColor = MiyoColors.TextPrimary)
            preview.timeline.forEachIndexed { index, event ->
                RuntimeEventRow(index = index, event = event)
            }
            SectionHeader("Variables")
            if (preview.variables.isEmpty()) {
                Text("No project variables", color = MiyoColors.TextMuted, style = MaterialTheme.typography.bodySmall)
            } else {
                preview.variables.forEach { variable ->
                    SettingRow(variable.name, variable.value)
                }
            }
        }
    }
}

@Composable
private fun RuntimeEventRow(index: Int, event: MiyoRuntimeEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(MiyoColors.SurfaceRaised)
            .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.md))
            .padding(MiyoSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiyoPill((index + 1).toString(), contentColor = MiyoColors.Petal)
        Spacer(Modifier.width(MiyoSpacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
            Text(event.label, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.labelLarge)
            Text(event.detail, color = MiyoColors.TextMuted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ScriptDiagnosticsPanel(
    diagnostics: List<MiyoScriptDiagnostic>,
    actionCount: Int
) {
    MiyoPanel(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MiyoColors.InkSoft
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)) {
            MiyoIconLabel(
                icon = if (diagnostics.isEmpty()) MiyoIcons.Preview else MiyoIcons.Warning,
                label = if (diagnostics.isEmpty()) "Script OK" else "Script Issues",
                iconTint = if (diagnostics.isEmpty()) MiyoColors.Mint else MiyoColors.Honey,
                textColor = MiyoColors.TextPrimary
            )
            if (diagnostics.isEmpty()) {
                Text(
                    text = "Generated script compiles back to $actionCount actions.",
                    color = MiyoColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                diagnostics.take(5).forEach { diagnostic ->
                    Text(
                        text = "Line ${diagnostic.line}: ${diagnostic.message}",
                        color = MiyoColors.Honey,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportPlanPanel(plan: MiyoExportPlan) {
    MiyoPanel(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MiyoColors.InkSoft
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)) {
            MiyoIconLabel(
                icon = MiyoIcons.Export,
                label = "Package",
                iconTint = if (plan.ready) MiyoColors.Mint else MiyoColors.Honey,
                textColor = MiyoColors.TextPrimary
            )
            SettingRow("Name", plan.packageName)
            SettingRow("Schema", plan.manifest.schemaVersion.toString())
            SettingRow("Locales", plan.manifest.locales.joinToString())
            SettingRow("Assets", plan.manifest.assetCount.toString())
            SettingRow("Files", "${plan.files.count { it.required }} required / ${plan.files.count { !it.required }} optional")
            Text(
                text = plan.diagnostics.firstOrNull()?.message ?: "Ready for package assembly.",
                color = if (plan.ready) MiyoColors.TextMuted else MiyoColors.Honey,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SimpleTabRow(
    selectedTab: SimpleEditorTab,
    onTabSelected: (SimpleEditorTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)
    ) {
        SimpleEditorTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(MiyoRadius.lg))
                    .background(if (selected) MiyoColors.Petal.copy(alpha = 0.16f) else MiyoColors.InkSoft)
                    .border(
                        MiyoStroke.hairline,
                        if (selected) MiyoColors.Petal.copy(alpha = 0.56f) else MiyoColors.Outline,
                        RoundedCornerShape(MiyoRadius.lg)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = MiyoSpacing.sm),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.label,
                    color = if (selected) MiyoColors.Petal else MiyoColors.TextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun TimelineEditor(
    project: MiyoProject,
    selectedSceneId: String?,
    selectedActionId: String?,
    onActionSelected: (String) -> Unit
) {
    val scene = project.story.blocks
        .flatMap { it.scenes }
        .firstOrNull { it.id == selectedSceneId }
        ?: project.selectedScene()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val compact = maxHeight < 340.dp || maxWidth < 760.dp
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(if (compact) MiyoSpacing.sm else MiyoSpacing.md)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (compact) MiyoSpacing.sm else MiyoSpacing.md)
            ) {
                ScenePreview(project = project, scene = scene, height = if (compact) 150.dp else 240.dp)
                if (!compact) {
                    ActionPalette()
                }
            }
            Column(
                modifier = Modifier
                    .width(if (compact) 300.dp else 340.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)
            ) {
                Text(
                    text = scene?.title ?: "No scene selected",
                    color = MiyoColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
                scene?.actions.orEmpty().forEach { action ->
                    ActionRow(
                        project = project,
                        action = action,
                        compact = compact,
                        selected = action.id == selectedActionId,
                        onClick = { onActionSelected(action.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenePreview(project: MiyoProject, scene: StoryScene?, height: Dp = 220.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(Color.Black)
            .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
            .padding(MiyoSpacing.md)
    ) {
        val background = project.findAsset(scene?.backgroundAssetId)
        if (background != null) {
            MiyoPill(
                text = background.displayName,
                icon = MiyoIcons.BackgroundAction,
                contentColor = MiyoColors.Lagoon,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(MiyoRadius.md))
                .background(MiyoColors.Wisteria.copy(alpha = 0.72f))
                .padding(MiyoSpacing.md)
        ) {
            Text(
                text = scene?.actions
                    ?.filterIsInstance<SceneAction.Dialogue>()
                    ?.firstOrNull()
                    ?.text
                    ?.resolve(project.defaultLocale)
                    ?: "Scene preview",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ActionPalette() {
    MiyoPanel(containerColor = MiyoColors.InkSoft, contentPadding = PaddingValues(MiyoSpacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PaletteButton(MiyoIcons.TextAction, "Text", MiyoColors.Petal)
            PaletteButton(MiyoIcons.ChoiceAction, "Choice", MiyoColors.Honey)
            PaletteButton(MiyoIcons.SoundAction, "Audio", MiyoColors.Coral)
            PaletteButton(MiyoIcons.BackgroundAction, "Scene", MiyoColors.Lagoon)
            PaletteButton(MiyoIcons.Add, "More", MiyoColors.Mint)
        }
    }
}

@Composable
private fun PaletteButton(icon: ImageVector, label: String, tint: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(tint.copy(alpha = 0.14f))
            .padding(horizontal = MiyoSpacing.sm, vertical = MiyoSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(MiyoSpacing.xs))
        Text(label, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ActionRow(
    project: MiyoProject,
    action: SceneAction,
    compact: Boolean = false,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = action.tint()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(if (selected) tint.copy(alpha = 0.13f) else MiyoColors.SurfaceRaised)
            .border(
                width = if (selected) MiyoStroke.selected else MiyoStroke.hairline,
                color = if (selected) tint.copy(alpha = 0.62f) else MiyoColors.Outline,
                shape = RoundedCornerShape(MiyoRadius.lg)
            )
            .clickable(onClick = onClick)
            .padding(if (compact) MiyoSpacing.sm else MiyoSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 30.dp else 38.dp)
                .clip(RoundedCornerShape(MiyoRadius.md))
                .background(tint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(action.icon(), contentDescription = null, tint = tint, modifier = Modifier.size(if (compact) 17.dp else 21.dp))
        }
        Spacer(Modifier.width(MiyoSpacing.md))
        Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
            Text(action.label, color = MiyoColors.TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(action.primaryText(project), color = MiyoColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AssetLibraryEditor(project: MiyoProject, assetKind: AssetKind) {
    val assets = project.assets.byKind(assetKind)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiyoIconLabel(assetKind.icon, assetKind.label, iconTint = MiyoColors.Lagoon, textColor = MiyoColors.TextPrimary)
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = MiyoColors.Lagoon, contentColor = MiyoColors.Ink)
            ) {
                Icon(MiyoIcons.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(MiyoSpacing.xs))
                Text("Register")
            }
        }
        if (assets.isEmpty()) {
            EmptyAssetState(assetKind)
        } else {
            assets.forEach { asset -> AssetRow(asset) }
        }
    }
}

@Composable
private fun EmptyAssetState(kind: AssetKind) {
    MiyoPanel(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MiyoColors.InkSoft,
        contentPadding = PaddingValues(MiyoSpacing.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
        ) {
            Icon(kind.icon, contentDescription = null, tint = MiyoColors.TextMuted, modifier = Modifier.size(38.dp))
            Text(kind.emptyPrompt(), color = MiyoColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AssetRow(asset: MiyoAsset) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(MiyoColors.SurfaceRaised)
            .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.lg))
            .padding(MiyoSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(MiyoRadius.md))
                .background(asset.kind.tint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(asset.kind.icon, contentDescription = null, tint = asset.kind.tint)
        }
        Spacer(Modifier.width(MiyoSpacing.md))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
            Text(asset.displayName, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
            Text(asset.relativePath, color = MiyoColors.TextMuted, style = MaterialTheme.typography.bodySmall)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
            MiyoPill(asset.status.label, contentColor = if (asset.status.name == "Ready") MiyoColors.Mint else MiyoColors.Honey)
            Text(asset.metadata.fileSizeLabel, color = MiyoColors.TextMuted, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun VariablesEditor(project: MiyoProject) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiyoIconLabel(MiyoIcons.Settings, "Variables", iconTint = MiyoColors.Honey, textColor = MiyoColors.TextPrimary)
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MiyoColors.Honey, contentColor = MiyoColors.Ink)) {
                Icon(MiyoIcons.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(MiyoSpacing.xs))
                Text("New")
            }
        }
        project.variables.forEach { variable ->
            MiyoPanel(modifier = Modifier.fillMaxWidth(), containerColor = MiyoColors.SurfaceRaised) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
                        Text(variable.name, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
                        Text(variable.type.label, color = MiyoColors.TextMuted, style = MaterialTheme.typography.bodySmall)
                    }
                    MiyoPill(variable.defaultValue.ifEmpty { "empty" }, contentColor = MiyoColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun GuiEditor(project: MiyoProject) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MiyoSpacing.md)
    ) {
        ScenePreview(project = project, scene = project.selectedScene(), height = 220.dp)
        SectionHeader("Message box")
        SettingRow("Font family", project.guiTheme.fontFamily)
        SliderRow("Font size", project.guiTheme.fontSize.toFloat(), 12f..32f)
        ColorSetting("Box color", project.guiTheme.messageBoxColor.toUiColor())
        SectionHeader("Custom boxes")
        ToggleRow("Large message box", true)
        ToggleRow("Input box", project.guiTheme.inputBoxVisible)
        ColorSetting("Choice box", project.guiTheme.choiceBoxColor.toUiColor())
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, color = MiyoColors.Lagoon, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.bodyMedium)
        MiyoPill(value)
    }
}

@Composable
private fun SliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>) {
    Column(verticalArrangement = Arrangement.spacedBy(MiyoSpacing.xs)) {
        Text(label, color = MiyoColors.TextPrimary, style = MaterialTheme.typography.bodyMedium)
        Slider(value = value, onValueChange = {}, valueRange = range)
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MiyoColors.TextPrimary)
        Switch(checked = checked, onCheckedChange = {})
    }
}

@Composable
private fun ColorSetting(label: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MiyoColors.TextPrimary)
        Box(
            modifier = Modifier
                .size(82.dp, 32.dp)
                .clip(RoundedCornerShape(MiyoRadius.md))
                .background(color)
                .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.md))
        )
    }
}

@Composable
private fun WorkspacePanel(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    MiyoPanel(
        modifier = modifier.fillMaxHeight(),
        containerColor = MiyoColors.Surface,
        contentPadding = PaddingValues(MiyoSpacing.sm)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MiyoSpacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiyoIconLabel(icon = icon, label = title, iconTint = tint, textColor = MiyoColors.TextPrimary)
                MiyoPill(subtitle, contentColor = MiyoColors.TextMuted)
            }
            content()
        }
    }
}

@Composable
private fun CodeLine(text: String, color: Color = MiyoColors.TextSecondary) {
    Text(
        text = text,
        color = color,
        fontFamily = FontFamily.Monospace,
        style = MaterialTheme.typography.bodyMedium
    )
}

private fun SceneAction.icon(): ImageVector = when (this) {
    is SceneAction.Dialogue -> MiyoIcons.TextAction
    is SceneAction.Choice -> MiyoIcons.ChoiceAction
    is SceneAction.SetBackground -> MiyoIcons.BackgroundAction
    is SceneAction.ShowCharacter -> MiyoIcons.CharacterAction
    is SceneAction.PlayAudio -> MiyoIcons.SoundAction
    is SceneAction.Wait -> MiyoIcons.Timeline
    is SceneAction.SetVariable -> MiyoIcons.Settings
    is SceneAction.GoTo -> MiyoIcons.Back
    is SceneAction.Cutscene -> MiyoIcons.Preview
    is SceneAction.RequestInput -> MiyoIcons.TextAction
}

private fun SceneAction.tint(): Color = when (this) {
    is SceneAction.Dialogue -> MiyoColors.Petal
    is SceneAction.Choice -> MiyoColors.Honey
    is SceneAction.SetBackground -> MiyoColors.Lagoon
    is SceneAction.ShowCharacter -> MiyoColors.Mint
    is SceneAction.PlayAudio -> if (channel == AudioChannel.Bgm) MiyoColors.Coral else MiyoColors.Wisteria
    is SceneAction.Wait -> MiyoColors.Honey
    is SceneAction.SetVariable -> MiyoColors.Lagoon
    is SceneAction.GoTo -> MiyoColors.TextSecondary
    is SceneAction.Cutscene -> MiyoColors.Wisteria
    is SceneAction.RequestInput -> MiyoColors.Mint
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

private val AssetKind.tint: Color
    get() = when (this) {
        AssetKind.Character -> MiyoColors.Mint
        AssetKind.Scenery -> MiyoColors.Lagoon
        AssetKind.Bgm -> MiyoColors.Coral
        AssetKind.Sfx -> MiyoColors.Wisteria
        AssetKind.Cutscene -> MiyoColors.Honey
        AssetKind.Gui -> MiyoColors.Petal
        AssetKind.Font -> MiyoColors.TextSecondary
        AssetKind.Other -> MiyoColors.TextMuted
    }

private fun String.scriptLineColor(): Color = when {
    startsWith("#") -> MiyoColors.TextMuted
    trimStart().startsWith("block ") -> MiyoColors.Petal
    trimStart().startsWith("scene ") -> MiyoColors.Lagoon
    trimStart().startsWith("choice ") -> MiyoColors.Honey
    trimStart().startsWith("bgm ") || trimStart().startsWith("sfx ") -> MiyoColors.Coral
    trimStart().startsWith("var ") || trimStart().startsWith("input ") -> MiyoColors.Mint
    else -> MiyoColors.TextSecondary
}
