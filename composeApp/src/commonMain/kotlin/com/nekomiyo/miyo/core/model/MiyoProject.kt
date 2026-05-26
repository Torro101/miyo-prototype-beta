package com.nekomiyo.miyo.core.model

const val MiyoSchemaVersion = 1

data class MiyoProject(
    val schemaVersion: Int = MiyoSchemaVersion,
    val projectId: String,
    val title: LocalizedText,
    val defaultLocale: String = "en",
    val locales: List<String> = listOf("en"),
    val settings: ProjectSettings = ProjectSettings(),
    val assets: AssetIndex = AssetIndex(),
    val story: Story = Story(),
    val variables: List<MiyoVariable> = emptyList(),
    val guiTheme: GuiTheme = GuiTheme(),
    val editor: EditorMetadata = EditorMetadata()
) {
    fun displayTitle(): String = title.resolve(defaultLocale)

    fun summary(diagnosticsCount: Int = 0): ProjectSummary {
        val sceneCount = story.blocks.sumOf { it.scenes.size }
        val actionCount = story.blocks.sumOf { block -> block.scenes.sumOf { it.actions.size } }
        return ProjectSummary(
            projectId = projectId,
            title = displayTitle(),
            sceneCount = sceneCount,
            actionCount = actionCount,
            assetCount = assets.assets.size,
            diagnosticsCount = diagnosticsCount,
            autosaveLabel = editor.autosaveLabel
        )
    }
}

data class ProjectSummary(
    val projectId: String,
    val title: String,
    val sceneCount: Int,
    val actionCount: Int,
    val assetCount: Int,
    val diagnosticsCount: Int,
    val autosaveLabel: String
) {
    val meta: String
        get() = "$sceneCount scenes / $actionCount actions / $assetCount assets"
}

data class ProjectSettings(
    val canvasWidth: Int = 1280,
    val canvasHeight: Int = 720,
    val autosaveEnabled: Boolean = true,
    val importImagesFrom: AssetImportSource = AssetImportSource.FilesAndCloud,
    val autoplayBgmInEditor: Boolean = false,
    val showTranslations: Boolean = false
)

enum class AssetImportSource(val label: String) {
    Gallery("Gallery"),
    FilesAndCloud("Files / Cloud")
}

data class LocalizedText(
    val values: Map<String, String>
) {
    fun resolve(locale: String, fallbackLocale: String = "en"): String =
        values[locale]
            ?: values[fallbackLocale]
            ?: values.values.firstOrNull()
            ?: ""

    companion object {
        fun plain(text: String, locale: String = "en"): LocalizedText = LocalizedText(mapOf(locale to text))
    }
}

data class Story(
    val startBlockId: String? = null,
    val blocks: List<StoryBlock> = emptyList()
)

data class StoryBlock(
    val id: String,
    val label: String,
    val color: MiyoColorToken = MiyoColorToken.Petal,
    val scenes: List<StoryScene> = emptyList(),
    val editorPosition: Vec2 = Vec2(80f, 120f)
)

data class StoryScene(
    val id: String,
    val title: String,
    val backgroundAssetId: String? = null,
    val actions: List<SceneAction> = emptyList(),
    val defaultTransition: Transition = Transition.Next
)

sealed class SceneAction {
    abstract val id: String
    abstract val label: String

    data class Dialogue(
        override val id: String,
        val speaker: String?,
        val text: LocalizedText,
        val expression: String? = null
    ) : SceneAction() {
        override val label: String = "Dialogue"
    }

    data class Choice(
        override val id: String,
        val prompt: LocalizedText,
        val options: List<ChoiceOption>
    ) : SceneAction() {
        override val label: String = "Choice"
    }

    data class SetBackground(
        override val id: String,
        val assetId: String
    ) : SceneAction() {
        override val label: String = "Scenery"
    }

    data class ShowCharacter(
        override val id: String,
        val assetId: String,
        val placement: String,
        val expression: String? = null
    ) : SceneAction() {
        override val label: String = "Character"
    }

    data class PlayAudio(
        override val id: String,
        val assetId: String,
        val loop: Boolean,
        val channel: AudioChannel
    ) : SceneAction() {
        override val label: String = channel.label
    }

    data class Wait(
        override val id: String,
        val seconds: Float
    ) : SceneAction() {
        override val label: String = "Wait"
    }

    data class SetVariable(
        override val id: String,
        val variableName: String,
        val operation: VariableOperation,
        val value: String
    ) : SceneAction() {
        override val label: String = "Variable"
    }

    data class GoTo(
        override val id: String,
        val transition: Transition
    ) : SceneAction() {
        override val label: String = "Go to"
    }

    data class Cutscene(
        override val id: String,
        val assetId: String
    ) : SceneAction() {
        override val label: String = "Cutscene"
    }

    data class RequestInput(
        override val id: String,
        val variableName: String,
        val prompt: LocalizedText
    ) : SceneAction() {
        override val label: String = "Request input"
    }
}

data class ChoiceOption(
    val label: LocalizedText,
    val transition: Transition
)

enum class AudioChannel(val label: String) {
    Bgm("BGM"),
    Sfx("SFX")
}

enum class VariableOperation(val label: String) {
    Set("Set"),
    Add("Add"),
    Subtract("Subtract"),
    Toggle("Toggle")
}

sealed class Transition {
    data object None : Transition()
    data object Next : Transition()
    data class Block(val blockId: String) : Transition()
    data class Scene(val blockId: String, val sceneId: String) : Transition()
}

data class MiyoVariable(
    val name: String,
    val type: VariableType,
    val defaultValue: String
)

enum class VariableType(val label: String) {
    Boolean("Boolean"),
    Number("Number"),
    Text("Text")
}

data class GuiTheme(
    val fontFamily: String = "Default",
    val fontSize: Int = 18,
    val messageBoxColor: MiyoColorToken = MiyoColorToken.Wisteria,
    val messageTextColor: MiyoColorToken = MiyoColorToken.White,
    val nameBoxColor: MiyoColorToken = MiyoColorToken.Lagoon,
    val choiceBoxColor: MiyoColorToken = MiyoColorToken.Surface,
    val inputBoxVisible: Boolean = false
)

data class EditorMetadata(
    val autosaveLabel: String = "Autosaved",
    val draftDirty: Boolean = false,
    val selectedBlockId: String? = null,
    val selectedSceneId: String? = null,
    val selectedActionId: String? = null
)

data class Vec2(
    val x: Float,
    val y: Float
)

enum class MiyoColorToken {
    Petal,
    Lagoon,
    Mint,
    Honey,
    Coral,
    Wisteria,
    Surface,
    White
}
