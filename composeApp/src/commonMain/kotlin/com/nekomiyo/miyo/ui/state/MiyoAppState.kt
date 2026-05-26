package com.nekomiyo.miyo.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nekomiyo.miyo.core.model.AssetKind
import com.nekomiyo.miyo.core.model.InteractiveArea
import com.nekomiyo.miyo.core.model.InteractiveAreaFrame
import com.nekomiyo.miyo.core.model.InteractiveAreaShape
import com.nekomiyo.miyo.core.model.InteractiveAreaTrigger
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.Transition
import com.nekomiyo.miyo.core.model.selectedBlock
import com.nekomiyo.miyo.core.model.selectedScene
import com.nekomiyo.miyo.core.storage.MiyoSampleProjects
import com.nekomiyo.miyo.core.validation.MiyoDiagnostic
import com.nekomiyo.miyo.core.validation.validateProject

enum class MiyoRoute {
    Hub,
    Editor
}

enum class HubTab(val label: String) {
    Home("Home"),
    Library("Library"),
    Explore("Explore"),
    Settings("Settings")
}

enum class EditorMode(val label: String) {
    Edit("Edit"),
    Preview("Preview")
}

enum class SimpleEditorTab(val label: String, val assetKind: AssetKind? = null) {
    Timeline("Scenes"),
    Files("Files"),
    Characters("Characters", AssetKind.Character),
    Scenery("Scenery", AssetKind.Scenery),
    Bgm("BGM", AssetKind.Bgm),
    Variables("Variables"),
    Conditions("Conditions"),
    Areas("Interactive areas"),
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
    initialHubTab: HubTab = HubTab.Home,
    initialEditorMode: EditorMode = EditorMode.Edit,
    initialProjects: List<MiyoProject> = MiyoSampleProjects.initialProjects()
) {
    private val projectState = mutableStateListOf<MiyoProject>().apply {
        addAll(initialProjects)
    }

    var route by mutableStateOf(initialRoute)
        private set

    var hubTab by mutableStateOf(initialHubTab)
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

    var selectedAreaId by mutableStateOf(projectState.firstOrNull()?.selectedScene()?.interactiveAreas?.firstOrNull()?.id)
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
        selectedAreaId = project.selectedScene(selectedBlockId, selectedSceneId)?.interactiveAreas?.firstOrNull()?.id
        editorMode = EditorMode.Edit
        simpleTab = SimpleEditorTab.Timeline
        route = MiyoRoute.Editor
    }

    fun createProject() {
        val nextIndex = (projectState.size + 1).coerceAtLeast(1)
        val project = MiyoSampleProjects.blankProject(nextIndex)
        projectState.add(0, project)
        openProject(project.projectId)
    }

    fun selectHubTab(tab: HubTab) {
        hubTab = tab
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
        selectedAreaId = currentProject
            ?.selectedScene(blockId = blockId, sceneId = sceneId)
            ?.interactiveAreas
            ?.firstOrNull()
            ?.id
    }

    fun selectAction(actionId: String) {
        selectedActionId = actionId
    }

    fun selectInteractiveArea(areaId: String) {
        selectedAreaId = areaId
    }

    fun addInteractiveArea(name: String, shape: InteractiveAreaShape) {
        val project = currentProject ?: return
        val blockId = selectedBlockId ?: project.selectedBlock()?.id ?: return
        val sceneId = selectedSceneId ?: project.selectedScene(blockId)?.id ?: return
        val cleanName = name.trim().ifEmpty { "${shape.label} area" }
        val existingCount = project.selectedScene(blockId, sceneId)?.interactiveAreas?.size ?: 0
        val areaId = "area-${cleanName.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty { shape.name.lowercase() }}-${existingCount + 1}"
        val area = InteractiveArea(
            id = areaId,
            name = cleanName,
            shape = shape,
            frame = InteractiveAreaFrame(
                x = (220f + existingCount * 46f).coerceAtMost(880f),
                y = (180f + existingCount * 28f).coerceAtMost(420f),
                width = if (shape == InteractiveAreaShape.Circle) 170f else 240f,
                height = if (shape == InteractiveAreaShape.Circle) 170f else 150f
            ),
            trigger = InteractiveAreaTrigger.OnTap,
            transition = Transition.Next
        )
        replaceProject(
            project.copy(
                story = project.story.copy(
                    blocks = project.story.blocks.map { block ->
                        if (block.id != blockId) {
                            block
                        } else {
                            block.copy(
                                scenes = block.scenes.map { scene ->
                                    if (scene.id == sceneId) scene.copy(interactiveAreas = scene.interactiveAreas + area) else scene
                                }
                            )
                        }
                    }
                )
            )
        )
        selectedAreaId = areaId
        simpleTab = SimpleEditorTab.Areas
    }

    private fun MiyoProject.findSceneActionCandidate(blockId: String, sceneId: String): String? =
        selectedScene(blockId = blockId, sceneId = sceneId)?.actions?.firstOrNull()?.id

    private fun replaceProject(project: MiyoProject) {
        val index = projectState.indexOfFirst { it.projectId == project.projectId }
        if (index >= 0) {
            projectState[index] = project
        }
    }
}

@Composable
fun rememberMiyoAppState(): MiyoAppState = remember { MiyoAppState() }
