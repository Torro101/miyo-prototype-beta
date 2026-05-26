package com.nekomiyo.miyo.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nekomiyo.miyo.core.model.AssetKind
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.selectedBlock
import com.nekomiyo.miyo.core.model.selectedScene
import com.nekomiyo.miyo.core.storage.MiyoSampleProjects
import com.nekomiyo.miyo.core.validation.MiyoDiagnostic
import com.nekomiyo.miyo.core.validation.validateProject

enum class MiyoRoute {
    Hub,
    Editor
}

enum class EditorMode(val label: String) {
    Simple("Simple"),
    Preview("Preview"),
    NodeConnect("Node Connect"),
    Code("Code")
}

enum class SimpleEditorTab(val label: String, val assetKind: AssetKind? = null) {
    Timeline("Timeline"),
    Characters("Characters", AssetKind.Character),
    Scenery("Scenery", AssetKind.Scenery),
    Bgm("BGM", AssetKind.Bgm),
    Variables("Variables"),
    Sfx("SFX", AssetKind.Sfx),
    Cutscenes("Cutscenes", AssetKind.Cutscene),
    Gui("GUI", AssetKind.Gui);

    companion object {
        fun fromAssetKind(kind: AssetKind): SimpleEditorTab =
            entries.firstOrNull { it.assetKind == kind } ?: Timeline
    }
}

class MiyoAppState(
    initialRoute: MiyoRoute = MiyoRoute.Hub,
    initialEditorMode: EditorMode = EditorMode.Simple,
    initialProjects: List<MiyoProject> = MiyoSampleProjects.initialProjects()
) {
    private val projectState = mutableStateListOf<MiyoProject>().apply {
        addAll(initialProjects)
    }

    var route by mutableStateOf(initialRoute)
        private set

    var editorMode by mutableStateOf(initialEditorMode)
        private set

    var simpleTab by mutableStateOf(SimpleEditorTab.Timeline)
        private set

    var selectedProjectId by mutableStateOf(projectState.firstOrNull()?.projectId)
        private set

    var selectedBlockId by mutableStateOf(projectState.firstOrNull()?.editor?.selectedBlockId)
        private set

    var selectedSceneId by mutableStateOf(projectState.firstOrNull()?.editor?.selectedSceneId)
        private set

    var selectedActionId by mutableStateOf(projectState.firstOrNull()?.editor?.selectedActionId)
        private set

    var selectedAssetKind by mutableStateOf(AssetKind.Character)
        private set

    val projects: List<MiyoProject>
        get() = projectState.toList()

    val currentProject: MiyoProject?
        get() = projectState.firstOrNull { it.projectId == selectedProjectId } ?: projectState.firstOrNull()

    val currentDiagnostics: List<MiyoDiagnostic>
        get() = currentProject?.let(::validateProject).orEmpty()

    val projectSummaries
        get() = projectState.map { project -> project.summary(validateProject(project).size) }

    fun openHub() {
        route = MiyoRoute.Hub
    }

    fun openProject(projectId: String) {
        val project = projectState.firstOrNull { it.projectId == projectId } ?: return
        selectedProjectId = project.projectId
        selectedBlockId = project.editor.selectedBlockId ?: project.selectedBlock()?.id
        selectedSceneId = project.editor.selectedSceneId ?: project.selectedScene(selectedBlockId)?.id
        selectedActionId = project.editor.selectedActionId ?: project.selectedScene(selectedBlockId, selectedSceneId)?.actions?.firstOrNull()?.id
        editorMode = EditorMode.Simple
        simpleTab = SimpleEditorTab.Timeline
        route = MiyoRoute.Editor
    }

    fun createProject() {
        val nextIndex = (projectState.size + 1).coerceAtLeast(1)
        val project = MiyoSampleProjects.blankProject(nextIndex)
        projectState.add(0, project)
        openProject(project.projectId)
    }

    fun selectEditorMode(mode: EditorMode) {
        editorMode = mode
    }

    fun selectSimpleTab(tab: SimpleEditorTab) {
        simpleTab = tab
        tab.assetKind?.let { selectedAssetKind = it }
    }

    fun selectAssetKind(kind: AssetKind) {
        selectedAssetKind = kind
        simpleTab = SimpleEditorTab.fromAssetKind(kind)
    }

    fun selectScene(blockId: String, sceneId: String) {
        selectedBlockId = blockId
        selectedSceneId = sceneId
        selectedActionId = currentProject
            ?.findSceneActionCandidate(blockId = blockId, sceneId = sceneId)
    }

    fun selectAction(actionId: String) {
        selectedActionId = actionId
    }

    private fun MiyoProject.findSceneActionCandidate(blockId: String, sceneId: String): String? =
        selectedScene(blockId = blockId, sceneId = sceneId)?.actions?.firstOrNull()?.id
}

@Composable
fun rememberMiyoAppState(): MiyoAppState = remember { MiyoAppState() }
