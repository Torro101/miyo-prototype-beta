package com.nekomiyo.miyo.nodeconnect

import androidx.compose.runtime.Composable
import com.nekomiyo.miyo.core.model.MiyoColorToken
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.SceneAction
import com.nekomiyo.miyo.core.model.Transition

const val MiyoNodeBridgeSchemaVersion = 1

/**
 * Shared placeholder envelope for local Node Connect WebView messages.
 * Keep this small until serialization is wired through kotlinx.serialization.
 */
data class MiyoNodeBridgeMessage(
    val schemaVersion: Int = MiyoNodeBridgeSchemaVersion,
    val type: String,
    val payloadJson: String,
    val sentAt: String? = null,
)

interface MiyoNodeBridgeHost {
    fun onNodeBridgeMessage(messageJson: String)
}

@Composable
expect fun MiyoNodeConnectView(
    bridgeHost: MiyoNodeBridgeHost,
    hostSnapshotJson: String,
)

data class MiyoGraphSnapshot(
    val projectId: String,
    val projectTitle: String,
    val nodes: List<MiyoGraphNode>,
    val edges: List<MiyoGraphEdge>,
    val diagnosticsCount: Int = 0
) {
    fun toHostLoadGraphEnvelopeJson(): String =
        buildJsonObject {
            prop("schemaVersion", MiyoNodeBridgeSchemaVersion)
            prop("type", "host.loadGraph")
            raw("payload", toPayloadJson())
        }

    private fun toPayloadJson(): String =
        buildJsonObject {
            prop("schemaVersion", MiyoNodeBridgeSchemaVersion)
            prop("projectId", projectId)
            prop("projectTitle", projectTitle)
            array("nodes", nodes) { it.toJson() }
            array("edges", edges) { it.toJson() }
            raw("viewport", """{"x":0,"y":0,"zoom":1}""")
            prop("diagnosticsCount", diagnosticsCount)
        }
}

data class MiyoGraphNode(
    val id: String,
    val type: MiyoGraphNodeType,
    val title: String,
    val subtitle: String,
    val position: MiyoGraphPosition,
    val colorToken: MiyoColorToken,
    val badges: List<String> = emptyList()
) {
    fun toJson(): String =
        buildJsonObject {
            prop("id", id)
            prop("type", type.webType)
            prop("title", title)
            prop("subtitle", subtitle)
            raw("position", """{"x":${position.x},"y":${position.y}}""")
            prop("color", colorToken.webColor)
            array("badges", badges) { jsonString(it) }
        }
}

data class MiyoGraphEdge(
    val id: String,
    val source: String,
    val target: String,
    val label: String,
    val ownerPath: String,
    val colorToken: MiyoColorToken
) {
    fun toJson(): String =
        buildJsonObject {
            prop("id", id)
            prop("source", source)
            prop("target", target)
            prop("label", label)
            prop("ownerPath", ownerPath)
            prop("color", colorToken.webColor)
        }
}

data class MiyoGraphPosition(
    val x: Int,
    val y: Int
)

enum class MiyoGraphNodeType(val webType: String) {
    Start("start"),
    Block("block"),
    Scene("scene"),
    Dialogue("dialogue"),
    Choice("choice"),
    Audio("audio"),
    Asset("asset"),
    Utility("utility")
}

fun MiyoProject.toGraphSnapshot(diagnosticsCount: Int = 0): MiyoGraphSnapshot {
    val nodes = mutableListOf<MiyoGraphNode>()
    val edges = mutableListOf<MiyoGraphEdge>()

    nodes += MiyoGraphNode(
        id = "project-start",
        type = MiyoGraphNodeType.Start,
        title = "Start",
        subtitle = displayTitle(),
        position = MiyoGraphPosition(48, 96),
        colorToken = MiyoColorToken.Mint,
        badges = listOf(defaultLocale)
    )

    story.blocks.forEachIndexed { blockIndex, block ->
        val blockNodeId = block.nodeId()
        val blockY = block.editorPosition.y.toInt().coerceAtLeast(72)
        nodes += MiyoGraphNode(
            id = blockNodeId,
            type = MiyoGraphNodeType.Block,
            title = block.label,
            subtitle = "${block.scenes.size} scenes",
            position = MiyoGraphPosition(block.editorPosition.x.toInt().coerceAtLeast(260), blockY),
            colorToken = block.color,
            badges = if (story.startBlockId == block.id) listOf("start block") else emptyList()
        )

        if (story.startBlockId == block.id) {
            edges += MiyoGraphEdge(
                id = "edge-project-start-$blockNodeId",
                source = "project-start",
                target = blockNodeId,
                label = "start",
                ownerPath = "story.startBlockId",
                colorToken = MiyoColorToken.Mint
            )
        }

        block.scenes.forEachIndexed { sceneIndex, scene ->
            val sceneNodeId = scene.nodeId(block.id)
            nodes += MiyoGraphNode(
                id = sceneNodeId,
                type = MiyoGraphNodeType.Scene,
                title = scene.title,
                subtitle = "${scene.actions.size} actions",
                position = MiyoGraphPosition(560 + sceneIndex * 260, blockY + blockIndex * 24),
                colorToken = block.color,
                badges = scene.backgroundAssetId?.let { listOf("scenery") }.orEmpty()
            )

            edges += MiyoGraphEdge(
                id = "edge-$blockNodeId-$sceneNodeId",
                source = blockNodeId,
                target = sceneNodeId,
                label = "contains",
                ownerPath = "story.${block.id}.scenes[${sceneIndex}]",
                colorToken = block.color
            )

            var previousNodeId = sceneNodeId
            scene.actions.forEachIndexed { actionIndex, action ->
                val actionNodeId = action.nodeId(scene.id)
                val actionType = action.graphNodeType()
                nodes += MiyoGraphNode(
                    id = actionNodeId,
                    type = actionType,
                    title = action.label,
                    subtitle = action.graphSubtitle(this),
                    position = MiyoGraphPosition(860 + actionIndex * 230, blockY + sceneIndex * 132),
                    colorToken = action.graphColorToken(),
                    badges = action.graphBadges()
                )

                edges += MiyoGraphEdge(
                    id = "edge-$previousNodeId-$actionNodeId",
                    source = previousNodeId,
                    target = actionNodeId,
                    label = if (previousNodeId == sceneNodeId) "first" else "next",
                    ownerPath = "story.${block.id}.${scene.id}.actions[${actionIndex}]",
                    colorToken = action.graphColorToken()
                )

                when (action) {
                    is SceneAction.Choice -> {
                        action.options.forEachIndexed { optionIndex, option ->
                            option.transition.targetNodeId()?.let { targetNodeId ->
                                edges += MiyoGraphEdge(
                                    id = "edge-${actionNodeId}-choice-$optionIndex",
                                    source = actionNodeId,
                                    target = targetNodeId,
                                    label = option.label.resolve(defaultLocale),
                                    ownerPath = "action.${action.id}.options[$optionIndex].transition",
                                    colorToken = MiyoColorToken.Honey
                                )
                            }
                        }
                    }
                    is SceneAction.GoTo -> {
                        action.transition.targetNodeId()?.let { targetNodeId ->
                            edges += MiyoGraphEdge(
                                id = "edge-${actionNodeId}-goto",
                                source = actionNodeId,
                                target = targetNodeId,
                                label = "go to",
                                ownerPath = "action.${action.id}.transition",
                                colorToken = MiyoColorToken.Lagoon
                            )
                        }
                    }
                    else -> Unit
                }

                previousNodeId = actionNodeId
            }
        }
    }

    return MiyoGraphSnapshot(
        projectId = projectId,
        projectTitle = displayTitle(),
        nodes = nodes,
        edges = edges.distinctBy { it.id },
        diagnosticsCount = diagnosticsCount
    )
}

private fun com.nekomiyo.miyo.core.model.StoryBlock.nodeId(): String = "block-$id"

private fun com.nekomiyo.miyo.core.model.StoryScene.nodeId(blockId: String): String = "scene-$blockId-$id"

private fun SceneAction.nodeId(sceneId: String): String = "action-$sceneId-$id"

private fun Transition.targetNodeId(): String? = when (this) {
    Transition.None,
    Transition.Next -> null
    is Transition.Block -> "block-$blockId"
    is Transition.Scene -> "scene-$blockId-$sceneId"
}

private fun SceneAction.graphNodeType(): MiyoGraphNodeType = when (this) {
    is SceneAction.Dialogue -> MiyoGraphNodeType.Dialogue
    is SceneAction.Choice -> MiyoGraphNodeType.Choice
    is SceneAction.SetBackground,
    is SceneAction.ShowCharacter,
    is SceneAction.Cutscene -> MiyoGraphNodeType.Asset
    is SceneAction.PlayAudio -> MiyoGraphNodeType.Audio
    is SceneAction.Wait,
    is SceneAction.SetVariable,
    is SceneAction.GoTo,
    is SceneAction.RequestInput -> MiyoGraphNodeType.Utility
}

private fun SceneAction.graphColorToken(): MiyoColorToken = when (this) {
    is SceneAction.Dialogue -> MiyoColorToken.Petal
    is SceneAction.Choice -> MiyoColorToken.Honey
    is SceneAction.SetBackground -> MiyoColorToken.Lagoon
    is SceneAction.ShowCharacter -> MiyoColorToken.Mint
    is SceneAction.PlayAudio -> MiyoColorToken.Coral
    is SceneAction.Wait -> MiyoColorToken.Honey
    is SceneAction.SetVariable -> MiyoColorToken.Lagoon
    is SceneAction.GoTo -> MiyoColorToken.Wisteria
    is SceneAction.Cutscene -> MiyoColorToken.Wisteria
    is SceneAction.RequestInput -> MiyoColorToken.Mint
}

private fun SceneAction.graphSubtitle(project: MiyoProject): String = when (this) {
    is SceneAction.Dialogue -> text.resolve(project.defaultLocale)
    is SceneAction.Choice -> "${options.size} branches"
    is SceneAction.SetBackground -> project.assets.find(assetId)?.displayName ?: assetId
    is SceneAction.ShowCharacter -> project.assets.find(assetId)?.displayName ?: assetId
    is SceneAction.PlayAudio -> project.assets.find(assetId)?.displayName ?: assetId
    is SceneAction.Wait -> "${seconds}s"
    is SceneAction.SetVariable -> "$variableName ${operation.label.lowercase()} $value"
    is SceneAction.GoTo -> "jump"
    is SceneAction.Cutscene -> project.assets.find(assetId)?.displayName ?: assetId
    is SceneAction.RequestInput -> variableName
}

private fun SceneAction.graphBadges(): List<String> = when (this) {
    is SceneAction.Dialogue -> speaker?.let { listOf(it) }.orEmpty()
    is SceneAction.PlayAudio -> listOf(channel.label)
    is SceneAction.ShowCharacter -> listOf(placement)
    else -> emptyList()
}

private val MiyoColorToken.webColor: String
    get() = when (this) {
        MiyoColorToken.Petal -> "#ff6f9d"
        MiyoColorToken.Lagoon -> "#4bc3c7"
        MiyoColorToken.Mint -> "#8fe3b0"
        MiyoColorToken.Honey -> "#f0c96a"
        MiyoColorToken.Coral -> "#ff8c6a"
        MiyoColorToken.Wisteria -> "#8b7cf6"
        MiyoColorToken.Surface -> "#252b35"
        MiyoColorToken.White -> "#f5f1ea"
    }

private class JsonBuilder {
    private val entries = mutableListOf<String>()

    fun prop(name: String, value: String) {
        entries += "${jsonString(name)}:${jsonString(value)}"
    }

    fun prop(name: String, value: Int) {
        entries += "${jsonString(name)}:$value"
    }

    fun raw(name: String, value: String) {
        entries += "${jsonString(name)}:$value"
    }

    fun <T> array(name: String, values: List<T>, itemJson: (T) -> String) {
        entries += "${jsonString(name)}:${values.joinToString(prefix = "[", postfix = "]") { itemJson(it) }}"
    }

    fun build(): String = entries.joinToString(prefix = "{", postfix = "}")
}

private fun buildJsonObject(block: JsonBuilder.() -> Unit): String =
    JsonBuilder().apply(block).build()

private fun jsonString(value: String): String =
    buildString {
        append('"')
        value.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
        append('"')
    }
