package com.nekomiyo.miyo.core.validation

import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.MiyoSchemaVersion
import com.nekomiyo.miyo.core.model.SceneAction
import com.nekomiyo.miyo.core.model.Transition
import com.nekomiyo.miyo.core.model.AssetStatus

data class MiyoDiagnostic(
    val severity: DiagnosticSeverity,
    val path: String,
    val message: String
)

enum class DiagnosticSeverity {
    Info,
    Warning,
    Error
}

fun validateProject(project: MiyoProject): List<MiyoDiagnostic> {
    val diagnostics = mutableListOf<MiyoDiagnostic>()
    val blockIds = project.story.blocks.map { it.id }.toSet()
    val sceneIdsByBlock = project.story.blocks.associate { it.id to it.scenes.map { scene -> scene.id }.toSet() }
    val assetIds = project.assets.assets.map { it.id }.toSet()
    val variableNames = project.variables.map { it.name }.toSet()

    if (project.schemaVersion != MiyoSchemaVersion) {
        diagnostics += MiyoDiagnostic(
            severity = DiagnosticSeverity.Error,
            path = "project.schemaVersion",
            message = "Schema ${project.schemaVersion} is not supported by this build."
        )
    }

    if (project.locales.isEmpty()) {
        diagnostics += MiyoDiagnostic(
            severity = DiagnosticSeverity.Error,
            path = "project.locales",
            message = "Project must include at least one locale."
        )
    }

    project.story.startBlockId?.let { startBlockId ->
        if (startBlockId !in blockIds) {
            diagnostics += MiyoDiagnostic(
                severity = DiagnosticSeverity.Error,
                path = "story.startBlockId",
                message = "Start block '$startBlockId' does not exist."
            )
        }
    }

    if (blockIds.size != project.story.blocks.size) {
        diagnostics += MiyoDiagnostic(
            severity = DiagnosticSeverity.Error,
            path = "story.blocks",
            message = "Block IDs must be unique inside a project."
        )
    }

    project.story.blocks.forEach { block ->
        val sceneIds = block.scenes.map { it.id }.toSet()
        block.scenes.forEach { scene ->
            scene.backgroundAssetId?.let { assetId ->
                if (assetId !in assetIds) {
                    diagnostics += missingAsset("story.${block.id}.${scene.id}.backgroundAssetId", assetId)
                }
            }
            validateTransition(
                transition = scene.defaultTransition,
                path = "story.${block.id}.${scene.id}.defaultTransition",
                blockIds = blockIds,
                sceneIdsByBlock = sceneIdsByBlock,
                diagnostics = diagnostics
            )
            scene.actions.forEach { action ->
                when (action) {
                    is SceneAction.SetBackground -> {
                        if (action.assetId !in assetIds) diagnostics += missingAsset("action.${action.id}.assetId", action.assetId)
                    }
                    is SceneAction.ShowCharacter -> {
                        if (action.assetId !in assetIds) diagnostics += missingAsset("action.${action.id}.assetId", action.assetId)
                    }
                    is SceneAction.PlayAudio -> {
                        if (action.assetId !in assetIds) diagnostics += missingAsset("action.${action.id}.assetId", action.assetId)
                    }
                    is SceneAction.Cutscene -> {
                        if (action.assetId !in assetIds) diagnostics += missingAsset("action.${action.id}.assetId", action.assetId)
                    }
                    is SceneAction.Choice -> {
                        action.options.forEachIndexed { index, option ->
                            validateTransition(
                                transition = option.transition,
                                path = "action.${action.id}.options[$index].transition",
                                blockIds = blockIds,
                                sceneIdsByBlock = sceneIdsByBlock,
                                diagnostics = diagnostics
                            )
                        }
                    }
                    is SceneAction.GoTo -> {
                        validateTransition(
                            transition = action.transition,
                            path = "action.${action.id}.transition",
                            blockIds = blockIds,
                            sceneIdsByBlock = sceneIdsByBlock,
                            diagnostics = diagnostics
                        )
                    }
                    else -> Unit
                }
            }
            if (scene.interactiveAreas.map { it.id }.toSet().size != scene.interactiveAreas.size) {
                diagnostics += MiyoDiagnostic(
                    severity = DiagnosticSeverity.Error,
                    path = "story.${block.id}.${scene.id}.interactiveAreas",
                    message = "Interactive area IDs must be unique inside a scene."
                )
            }
            scene.interactiveAreas.forEach { area ->
                if (area.name.isBlank()) {
                    diagnostics += MiyoDiagnostic(
                        severity = DiagnosticSeverity.Error,
                        path = "interactiveArea.${area.id}.name",
                        message = "Interactive areas must have a name."
                    )
                }
                if (area.frame.width <= 0f || area.frame.height <= 0f) {
                    diagnostics += MiyoDiagnostic(
                        severity = DiagnosticSeverity.Error,
                        path = "interactiveArea.${area.id}.frame",
                        message = "Interactive area '${area.name}' must have positive width and height."
                    )
                }
                if (
                    area.frame.x < 0f ||
                    area.frame.y < 0f ||
                    area.frame.x + area.frame.width > project.settings.canvasWidth ||
                    area.frame.y + area.frame.height > project.settings.canvasHeight
                ) {
                    diagnostics += MiyoDiagnostic(
                        severity = DiagnosticSeverity.Warning,
                        path = "interactiveArea.${area.id}.frame",
                        message = "Interactive area '${area.name}' extends outside the ${project.settings.canvasWidth} x ${project.settings.canvasHeight} canvas."
                    )
                }
                validateTransition(
                    transition = area.transition,
                    path = "interactiveArea.${area.id}.transition",
                    blockIds = blockIds,
                    sceneIdsByBlock = sceneIdsByBlock,
                    diagnostics = diagnostics
                )
                area.variableName?.let { variableName ->
                    if (variableName !in variableNames) {
                        diagnostics += MiyoDiagnostic(
                            severity = DiagnosticSeverity.Warning,
                            path = "interactiveArea.${area.id}.variableName",
                            message = "Interactive area '${area.name}' references unknown variable '$variableName'."
                        )
                    }
                }
                area.condition?.let { condition ->
                    if (condition.variableName !in variableNames) {
                        diagnostics += MiyoDiagnostic(
                            severity = DiagnosticSeverity.Warning,
                            path = "interactiveArea.${area.id}.condition",
                            message = "Interactive area '${area.name}' condition references unknown variable '${condition.variableName}'."
                        )
                    }
                }
            }
        }

        if (sceneIds.size != block.scenes.size) {
            diagnostics += MiyoDiagnostic(
                severity = DiagnosticSeverity.Error,
                path = "story.${block.id}.scenes",
                message = "Scene IDs must be unique inside a block."
            )
        }
    }

    project.assets.assets
        .filter { it.status != AssetStatus.Ready }
        .forEach { asset ->
            diagnostics += MiyoDiagnostic(
                severity = DiagnosticSeverity.Warning,
                path = "assets.${asset.id}",
                message = "${asset.displayName} is ${asset.status.label.lowercase()}."
            )
        }

    return diagnostics
}

private fun validateTransition(
    transition: Transition,
    path: String,
    blockIds: Set<String>,
    sceneIdsByBlock: Map<String, Set<String>>,
    diagnostics: MutableList<MiyoDiagnostic>
) {
    when (transition) {
        Transition.None,
        Transition.Next -> Unit
        is Transition.Block -> if (transition.blockId !in blockIds) {
            diagnostics += MiyoDiagnostic(DiagnosticSeverity.Error, path, "Target block '${transition.blockId}' does not exist.")
        }
        is Transition.Scene -> {
            val sceneIds = sceneIdsByBlock[transition.blockId]
            if (sceneIds == null) {
                diagnostics += MiyoDiagnostic(DiagnosticSeverity.Error, path, "Target block '${transition.blockId}' does not exist.")
            } else if (transition.sceneId !in sceneIds) {
                diagnostics += MiyoDiagnostic(DiagnosticSeverity.Error, path, "Target scene '${transition.sceneId}' does not exist.")
            }
        }
    }
}

private fun missingAsset(path: String, assetId: String): MiyoDiagnostic =
    MiyoDiagnostic(
        severity = DiagnosticSeverity.Warning,
        path = path,
        message = "Referenced asset '$assetId' is not registered in the project asset index."
    )
