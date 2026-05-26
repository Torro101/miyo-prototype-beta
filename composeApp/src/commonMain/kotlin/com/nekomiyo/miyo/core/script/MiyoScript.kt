package com.nekomiyo.miyo.core.script

import com.nekomiyo.miyo.core.model.AudioChannel
import com.nekomiyo.miyo.core.model.LocalizedText
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.SceneAction
import com.nekomiyo.miyo.core.model.Transition
import com.nekomiyo.miyo.core.model.VariableOperation

data class MiyoScriptDiagnostic(
    val line: Int,
    val column: Int,
    val message: String
)

data class MiyoScriptCompileResult(
    val actions: List<SceneAction>,
    val diagnostics: List<MiyoScriptDiagnostic>
) {
    val success: Boolean
        get() = diagnostics.isEmpty()
}

object MiyoScriptFormatter {
    fun format(project: MiyoProject): String =
        buildString {
            appendLine("# ${project.displayTitle()}")
            project.story.blocks.forEach { block ->
                appendLine("block \"${block.label.escapeScript()}\" {")
                block.scenes.forEach { scene ->
                    appendLine("  scene \"${scene.title.escapeScript()}\" {")
                    scene.actions.forEach { action ->
                        appendLine("    ${action.toScriptLine(project)}")
                    }
                    appendLine("  }")
                }
                appendLine("}")
            }
        }.trimEnd()

    private fun SceneAction.toScriptLine(project: MiyoProject): String = when (this) {
        is SceneAction.Dialogue -> {
            val speakerToken = speaker?.let { " ${it.escapeIdentifier()}" }.orEmpty()
            "say$speakerToken \"${text.resolve(project.defaultLocale).escapeScript()}\""
        }
        is SceneAction.Choice -> "choice \"${prompt.resolve(project.defaultLocale).escapeScript()}\""
        is SceneAction.SetBackground -> "bg \"${assetName(project, assetId).escapeScript()}\""
        is SceneAction.ShowCharacter -> "show \"${assetName(project, assetId).escapeScript()}\" at ${placement.escapeIdentifier()}"
        is SceneAction.PlayAudio -> "${channel.scriptCommand()} \"${assetName(project, assetId).escapeScript()}\"${if (loop) " loop" else ""}"
        is SceneAction.Wait -> "wait $seconds"
        is SceneAction.SetVariable -> "var ${variableName.escapeIdentifier()} ${operation.label.lowercase()} \"${value.escapeScript()}\""
        is SceneAction.GoTo -> "goto ${transition.toScriptTarget()}"
        is SceneAction.Cutscene -> "cutscene \"${assetName(project, assetId).escapeScript()}\""
        is SceneAction.RequestInput -> "input ${variableName.escapeIdentifier()} \"${prompt.resolve(project.defaultLocale).escapeScript()}\""
    }

    private fun assetName(project: MiyoProject, assetId: String): String =
        project.assets.find(assetId)?.displayName ?: assetId

    private fun Transition.toScriptTarget(): String = when (this) {
        Transition.None -> "none"
        Transition.Next -> "next"
        is Transition.Block -> "block \"${blockId.escapeScript()}\""
        is Transition.Scene -> "scene \"${blockId.escapeScript()}\" \"${sceneId.escapeScript()}\""
    }
}

object MiyoScriptCompiler {
    fun compileActions(source: String): MiyoScriptCompileResult {
        val diagnostics = mutableListOf<MiyoScriptDiagnostic>()
        val actions = mutableListOf<SceneAction>()

        source.lineSequence().forEachIndexed { index, rawLine ->
            val lineNumber = index + 1
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#") || line == "{" || line == "}") return@forEachIndexed
            if (line.startsWith("block ") || line.startsWith("scene ")) return@forEachIndexed

            val action = parseActionLine(line, lineNumber, diagnostics)
            if (action != null) {
                actions += action
            }
        }

        return MiyoScriptCompileResult(actions = actions, diagnostics = diagnostics)
    }

    private fun parseActionLine(
        line: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? {
        val id = "script-$lineNumber"
        return when {
            line.startsWith("say ") || line == "say" -> parseSay(id, line, lineNumber, diagnostics)
            line.startsWith("choice ") -> parseQuoted(line, "choice", lineNumber, diagnostics) {
                SceneAction.Choice(id = id, prompt = LocalizedText.plain(it), options = emptyList())
            }
            line.startsWith("bg ") -> parseQuoted(line, "bg", lineNumber, diagnostics) {
                SceneAction.SetBackground(id = id, assetId = it.toAssetId())
            }
            line.startsWith("show ") -> parseShow(id, line, lineNumber, diagnostics)
            line.startsWith("bgm ") -> parseAudio(id, line, AudioChannel.Bgm, lineNumber, diagnostics)
            line.startsWith("sfx ") -> parseAudio(id, line, AudioChannel.Sfx, lineNumber, diagnostics)
            line.startsWith("wait ") -> parseWait(id, line, lineNumber, diagnostics)
            line.startsWith("var ") -> parseVariable(id, line, lineNumber, diagnostics)
            line.startsWith("goto ") -> parseGoto(id, line, lineNumber, diagnostics)
            line.startsWith("cutscene ") -> parseQuoted(line, "cutscene", lineNumber, diagnostics) {
                SceneAction.Cutscene(id = id, assetId = it.toAssetId())
            }
            line.startsWith("input ") -> parseInput(id, line, lineNumber, diagnostics)
            else -> {
                diagnostics += MiyoScriptDiagnostic(lineNumber, 1, "Unknown MiyoScript command.")
                null
            }
        }
    }

    private fun parseSay(
        id: String,
        line: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? {
        val quoteStart = line.indexOf('"')
        val quoteEnd = line.lastIndexOf('"')
        if (quoteStart < 0 || quoteEnd <= quoteStart) {
            diagnostics += MiyoScriptDiagnostic(lineNumber, line.length, "Dialogue requires quoted text.")
            return null
        }
        val speaker = line.substringAfter("say").substringBefore('"').trim().ifEmpty { null }
        return SceneAction.Dialogue(
            id = id,
            speaker = speaker,
            text = LocalizedText.plain(line.substring(quoteStart + 1, quoteEnd).unescapeScript())
        )
    }

    private fun parseShow(
        id: String,
        line: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? {
        val value = quotedValue(line)
        if (value == null) {
            diagnostics += MiyoScriptDiagnostic(lineNumber, line.length, "Show requires a quoted asset name.")
            return null
        }
        val placement = line.substringAfterLast(" at ", missingDelimiterValue = "center").trim().ifEmpty { "center" }
        return SceneAction.ShowCharacter(id = id, assetId = value.toAssetId(), placement = placement)
    }

    private fun parseAudio(
        id: String,
        line: String,
        channel: AudioChannel,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? =
        parseQuoted(line, channel.scriptCommand(), lineNumber, diagnostics) {
            SceneAction.PlayAudio(id = id, assetId = it.toAssetId(), loop = line.endsWith(" loop"), channel = channel)
        }

    private fun parseWait(
        id: String,
        line: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? {
        val seconds = line.removePrefix("wait").trim().toFloatOrNull()
        if (seconds == null || seconds < 0f) {
            diagnostics += MiyoScriptDiagnostic(lineNumber, 6, "Wait requires a non-negative number.")
            return null
        }
        return SceneAction.Wait(id = id, seconds = seconds)
    }

    private fun parseVariable(
        id: String,
        line: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? {
        val tokens = line.removePrefix("var").trim().split(Regex("\\s+"), limit = 3)
        if (tokens.size < 2) {
            diagnostics += MiyoScriptDiagnostic(lineNumber, 5, "Variable requires a name, operation, and value.")
            return null
        }
        val operation = VariableOperation.entries.firstOrNull { it.label.equals(tokens[1], ignoreCase = true) }
        if (operation == null) {
            diagnostics += MiyoScriptDiagnostic(lineNumber, line.indexOf(tokens[1]) + 1, "Unknown variable operation '${tokens[1]}'.")
            return null
        }
        val value = quotedValue(line) ?: tokens.getOrNull(2)?.orEmpty().orEmpty()
        return SceneAction.SetVariable(id = id, variableName = tokens[0], operation = operation, value = value)
    }

    private fun parseGoto(
        id: String,
        line: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? {
        val body = line.removePrefix("goto").trim()
        val quotedValues = quotedValues(line)
        val transition = when {
            body.equals("next", ignoreCase = true) -> Transition.Next
            body.equals("none", ignoreCase = true) -> Transition.None
            body.startsWith("block ", ignoreCase = true) && quotedValues.isNotEmpty() -> Transition.Block(quotedValues[0])
            body.startsWith("scene ", ignoreCase = true) && quotedValues.size >= 2 -> Transition.Scene(quotedValues[0], quotedValues[1])
            else -> {
                diagnostics += MiyoScriptDiagnostic(lineNumber, 6, "Go to requires next, none, block \"id\", or scene \"block\" \"scene\".")
                return null
            }
        }
        return SceneAction.GoTo(id = id, transition = transition)
    }

    private fun parseInput(
        id: String,
        line: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>
    ): SceneAction? {
        val variableName = line.removePrefix("input").trim().substringBefore(' ').ifEmpty {
            diagnostics += MiyoScriptDiagnostic(lineNumber, 7, "Input requires a variable name.")
            return null
        }
        val prompt = quotedValue(line) ?: "Enter value"
        return SceneAction.RequestInput(id = id, variableName = variableName, prompt = LocalizedText.plain(prompt))
    }

    private fun parseQuoted(
        line: String,
        command: String,
        lineNumber: Int,
        diagnostics: MutableList<MiyoScriptDiagnostic>,
        factory: (String) -> SceneAction
    ): SceneAction? {
        val value = quotedValue(line)
        if (value == null) {
            diagnostics += MiyoScriptDiagnostic(lineNumber, command.length + 2, "$command requires a quoted value.")
            return null
        }
        return factory(value)
    }

    private fun quotedValue(line: String): String? {
        val quoteStart = line.indexOf('"')
        val quoteEnd = line.lastIndexOf('"')
        return if (quoteStart >= 0 && quoteEnd > quoteStart) {
            line.substring(quoteStart + 1, quoteEnd).unescapeScript()
        } else {
            null
        }
    }

    private fun quotedValues(line: String): List<String> {
        val values = mutableListOf<String>()
        var index = 0
        while (index < line.length) {
            val quoteStart = line.indexOf('"', startIndex = index)
            if (quoteStart < 0) break
            val quoteEnd = line.indexOf('"', startIndex = quoteStart + 1)
            if (quoteEnd < 0) break
            values += line.substring(quoteStart + 1, quoteEnd).unescapeScript()
            index = quoteEnd + 1
        }
        return values
    }
}

private fun AudioChannel.scriptCommand(): String = when (this) {
    AudioChannel.Bgm -> "bgm"
    AudioChannel.Sfx -> "sfx"
}

private fun String.toAssetId(): String =
    lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifEmpty { "asset" }

private fun String.escapeIdentifier(): String =
    replace(Regex("[^A-Za-z0-9_]+"), "_").trim('_').ifEmpty { "value" }

private fun String.escapeScript(): String =
    replace("\\", "\\\\").replace("\"", "\\\"")

private fun String.unescapeScript(): String =
    replace("\\\"", "\"").replace("\\\\", "\\")
