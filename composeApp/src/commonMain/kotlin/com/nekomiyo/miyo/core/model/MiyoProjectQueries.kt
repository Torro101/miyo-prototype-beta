package com.nekomiyo.miyo.core.model

fun MiyoProject.selectedBlock(blockId: String? = editor.selectedBlockId): StoryBlock? =
    story.blocks.firstOrNull { it.id == blockId } ?: story.blocks.firstOrNull()

fun MiyoProject.selectedScene(
    blockId: String? = editor.selectedBlockId,
    sceneId: String? = editor.selectedSceneId
): StoryScene? {
    val block = selectedBlock(blockId) ?: return null
    return block.scenes.firstOrNull { it.id == sceneId } ?: block.scenes.firstOrNull()
}

fun MiyoProject.findScene(blockId: String?, sceneId: String?): StoryScene? {
    val block = story.blocks.firstOrNull { it.id == blockId } ?: return null
    return block.scenes.firstOrNull { it.id == sceneId }
}

fun MiyoProject.findAction(actionId: String?): SceneAction? {
    if (actionId == null) return null
    return story.blocks
        .flatMap { it.scenes }
        .flatMap { it.actions }
        .firstOrNull { it.id == actionId }
}

fun MiyoProject.findAsset(assetId: String?): MiyoAsset? = assets.find(assetId)

fun SceneAction.primaryText(project: MiyoProject): String = when (this) {
    is SceneAction.Dialogue -> text.resolve(project.defaultLocale)
    is SceneAction.Choice -> prompt.resolve(project.defaultLocale)
    is SceneAction.SetBackground -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.ShowCharacter -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.PlayAudio -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.Wait -> "${seconds}s"
    is SceneAction.SetVariable -> "$variableName ${operation.label.lowercase()} $value"
    is SceneAction.GoTo -> transition.label(project)
    is SceneAction.Cutscene -> project.findAsset(assetId)?.displayName ?: assetId
    is SceneAction.RequestInput -> prompt.resolve(project.defaultLocale)
}

fun Transition.label(project: MiyoProject): String = when (this) {
    Transition.None -> "No transition"
    Transition.Next -> "Next action"
    is Transition.Block -> project.story.blocks.firstOrNull { it.id == blockId }?.label ?: blockId
    is Transition.Scene -> project.findScene(blockId, sceneId)?.title ?: "$blockId / $sceneId"
}

fun InteractiveArea.targetLabel(project: MiyoProject): String = when (transition) {
    Transition.None -> variableName?.let { "Set $it" } ?: "No action"
    else -> transition.label(project)
}

fun InteractiveArea.conditionLabel(): String =
    condition?.let { "${it.variableName} ${it.operator.label} ${it.value}" } ?: "Always active"

fun AssetKind.emptyPrompt(): String = when (this) {
    AssetKind.Character -> "Add character art for dialogue sprites."
    AssetKind.Scenery -> "Add scenery backgrounds for scenes."
    AssetKind.Bgm -> "Add BGM tracks for scene playback."
    AssetKind.Sfx -> "Add SFX clips for actions."
    AssetKind.Cutscene -> "Add video cutscenes."
    AssetKind.Gui -> "Add GUI skins and message boxes."
    AssetKind.Font -> "Add project fonts."
    AssetKind.Other -> "Add other project assets."
}
