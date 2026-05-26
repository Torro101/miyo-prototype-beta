package com.nekomiyo.miyo.core.runtime

import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.SceneAction
import com.nekomiyo.miyo.core.model.StoryBlock
import com.nekomiyo.miyo.core.model.StoryScene
import com.nekomiyo.miyo.core.model.findAsset
import com.nekomiyo.miyo.core.model.selectedBlock
import com.nekomiyo.miyo.core.model.selectedScene

data class MiyoRuntimePreviewState(
    val projectTitle: String,
    val blockTitle: String,
    val sceneTitle: String,
    val backgroundName: String?,
    val speaker: String?,
    val dialogue: String,
    val choices: List<String>,
    val timeline: List<MiyoRuntimeEvent>,
    val variables: List<MiyoRuntimeVariable>
)

data class MiyoRuntimeEvent(
    val label: String,
    val detail: String
)

data class MiyoRuntimeVariable(
    val name: String,
    val value: String
)

fun MiyoProject.toRuntimePreviewState(
    blockId: String? = editor.selectedBlockId,
    sceneId: String? = editor.selectedSceneId
): MiyoRuntimePreviewState {
    val block = selectedBlock(blockId) ?: StoryBlock(id = "empty", label = "No chapter")
    val scene = selectedScene(blockId, sceneId) ?: StoryScene(id = "empty", title = "No scene")
    val dialogueAction = scene.actions.filterIsInstance<SceneAction.Dialogue>().firstOrNull()
    val choiceAction = scene.actions.filterIsInstance<SceneAction.Choice>().firstOrNull()

    return MiyoRuntimePreviewState(
        projectTitle = displayTitle(),
        blockTitle = block.label,
        sceneTitle = scene.title,
        backgroundName = findAsset(scene.backgroundAssetId)?.displayName,
        speaker = dialogueAction?.speaker,
        dialogue = dialogueAction?.text?.resolve(defaultLocale) ?: "No dialogue in this scene.",
        choices = choiceAction?.options?.map { it.label.resolve(defaultLocale) }.orEmpty(),
        timeline = scene.actions.map { action ->
            MiyoRuntimeEvent(label = action.label, detail = action.runtimeDetail(this))
        },
        variables = variables.map { MiyoRuntimeVariable(it.name, it.defaultValue.ifEmpty { "empty" }) }
    )
}

private fun SceneAction.runtimeDetail(project: MiyoProject): String = when (this) {
    is SceneAction.Dialogue -> text.resolve(project.defaultLocale)
    is SceneAction.Choice -> "${options.size} choices"
    is SceneAction.SetBackground -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.ShowCharacter -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.PlayAudio -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.Wait -> "${seconds}s"
    is SceneAction.SetVariable -> "$variableName = $value"
    is SceneAction.GoTo -> "jump"
    is SceneAction.Cutscene -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.RequestInput -> prompt.resolve(project.defaultLocale)
}
