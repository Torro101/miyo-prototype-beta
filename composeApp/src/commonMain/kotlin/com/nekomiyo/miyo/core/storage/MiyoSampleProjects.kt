package com.nekomiyo.miyo.core.storage

import com.nekomiyo.miyo.core.model.AssetIndex
import com.nekomiyo.miyo.core.model.AssetKind
import com.nekomiyo.miyo.core.model.AssetMetadata
import com.nekomiyo.miyo.core.model.AssetStatus
import com.nekomiyo.miyo.core.model.AudioChannel
import com.nekomiyo.miyo.core.model.ChoiceOption
import com.nekomiyo.miyo.core.model.EditorMetadata
import com.nekomiyo.miyo.core.model.GuiTheme
import com.nekomiyo.miyo.core.model.InteractiveArea
import com.nekomiyo.miyo.core.model.InteractiveAreaCondition
import com.nekomiyo.miyo.core.model.InteractiveAreaFrame
import com.nekomiyo.miyo.core.model.InteractiveAreaShape
import com.nekomiyo.miyo.core.model.InteractiveAreaTrigger
import com.nekomiyo.miyo.core.model.LocalizedText
import com.nekomiyo.miyo.core.model.MiyoAsset
import com.nekomiyo.miyo.core.model.MiyoColorToken
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.MiyoVariable
import com.nekomiyo.miyo.core.model.ProjectSettings
import com.nekomiyo.miyo.core.model.SceneAction
import com.nekomiyo.miyo.core.model.Story
import com.nekomiyo.miyo.core.model.StoryBlock
import com.nekomiyo.miyo.core.model.StoryScene
import com.nekomiyo.miyo.core.model.Transition
import com.nekomiyo.miyo.core.model.VariableType
import com.nekomiyo.miyo.core.model.Vec2

object MiyoSampleProjects {
    fun initialProjects(): List<MiyoProject> = listOf(
        moonlitRehearsal(),
        seaGlassStation()
    )

    fun blankProject(projectNumber: Int): MiyoProject {
        val projectId = "project-$projectNumber"
        val blockId = "$projectId-block-opening"
        val sceneId = "$projectId-scene-1"
        return MiyoProject(
            projectId = projectId,
            title = LocalizedText.plain("Untitled #$projectNumber"),
            settings = ProjectSettings(),
            assets = AssetIndex(),
            story = Story(
                startBlockId = blockId,
                blocks = listOf(
                    StoryBlock(
                        id = blockId,
                        label = "Chapter #1",
                        color = MiyoColorToken.Petal,
                        editorPosition = Vec2(96f, 96f),
                        scenes = listOf(
                            StoryScene(
                                id = sceneId,
                                title = "Scene #1",
                                actions = listOf(
                                    SceneAction.Dialogue(
                                        id = "$projectId-action-1",
                                        speaker = null,
                                        text = LocalizedText.plain("Start writing...")
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            editor = EditorMetadata(
                selectedBlockId = blockId,
                selectedSceneId = sceneId,
                selectedActionId = "$projectId-action-1"
            )
        )
    }

    private fun moonlitRehearsal(): MiyoProject {
        val assets = AssetIndex(
            assets = listOf(
                MiyoAsset(
                    id = "scenery-platform-dusk",
                    displayName = "Platform at dusk",
                    relativePath = "assets/scenery/platform_dusk.png",
                    kind = AssetKind.Scenery,
                    metadata = AssetMetadata(width = 1280, height = 720, fileSizeLabel = "1.42 MB")
                ),
                MiyoAsset(
                    id = "character-miyo-soft",
                    displayName = "Miyo / soft smile",
                    relativePath = "assets/characters/miyo_soft.png",
                    kind = AssetKind.Character,
                    metadata = AssetMetadata(width = 768, height = 1024, fileSizeLabel = "0.86 MB")
                ),
                MiyoAsset(
                    id = "bgm-evening-loop",
                    displayName = "Station evening loop",
                    relativePath = "assets/audio/station_evening_loop.ogg",
                    kind = AssetKind.Bgm,
                    metadata = AssetMetadata(durationSeconds = 94f, fileSizeLabel = "2.10 MB")
                ),
                MiyoAsset(
                    id = "sfx-lantern",
                    displayName = "Lantern switch",
                    relativePath = "assets/audio/lantern_switch.wav",
                    kind = AssetKind.Sfx,
                    metadata = AssetMetadata(durationSeconds = 1.4f, fileSizeLabel = "0.12 MB")
                ),
                MiyoAsset(
                    id = "cutscene-train-arrival",
                    displayName = "Train arrival",
                    relativePath = "assets/cutscenes/train_arrival.mp4",
                    kind = AssetKind.Cutscene,
                    status = AssetStatus.PendingImport,
                    metadata = AssetMetadata(durationSeconds = 12f, fileSizeLabel = "0.00 MB")
                )
            )
        )

        val openingScene = StoryScene(
            id = "scene-arrival",
            title = "Arrival",
            backgroundAssetId = "scenery-platform-dusk",
            actions = listOf(
                SceneAction.SetBackground("action-bg", "scenery-platform-dusk"),
                SceneAction.ShowCharacter("action-character", "character-miyo-soft", placement = "left", expression = "soft smile"),
                SceneAction.PlayAudio("action-bgm", "bgm-evening-loop", loop = true, channel = AudioChannel.Bgm),
                SceneAction.Dialogue(
                    id = "action-dialogue-1",
                    speaker = "Miyo",
                    text = LocalizedText.plain("The lanterns turn on one by one."),
                    expression = "soft smile"
                ),
                SceneAction.Choice(
                    id = "action-choice-1",
                    prompt = LocalizedText.plain("What should Miyo do?"),
                    options = listOf(
                        ChoiceOption(LocalizedText.plain("Follow the voice"), Transition.Scene("block-opening", "scene-voice")),
                        ChoiceOption(LocalizedText.plain("Wait outside"), Transition.Scene("block-opening", "scene-wait"))
                    )
                )
            ),
            interactiveAreas = listOf(
                InteractiveArea(
                    id = "area-ticket-gate",
                    name = "Ticket gate tap",
                    shape = InteractiveAreaShape.Box,
                    frame = InteractiveAreaFrame(x = 760f, y = 238f, width = 260f, height = 210f),
                    trigger = InteractiveAreaTrigger.OnTap,
                    transition = Transition.Scene("block-opening", "scene-voice"),
                    condition = InteractiveAreaCondition(variableName = "met_voice", value = "false")
                )
            ),
            defaultTransition = Transition.Next
        )

        val voiceScene = StoryScene(
            id = "scene-voice",
            title = "Follow the voice",
            actions = listOf(
                SceneAction.PlayAudio("action-sfx-lantern", "sfx-lantern", loop = false, channel = AudioChannel.Sfx),
                SceneAction.Dialogue(
                    id = "action-dialogue-2",
                    speaker = "Miyo",
                    text = LocalizedText.plain("Someone is calling from behind the ticket gate.")
                )
            )
        )

        val waitScene = StoryScene(
            id = "scene-wait",
            title = "Wait outside",
            actions = listOf(
                SceneAction.Wait("action-wait", seconds = 1.5f),
                SceneAction.Dialogue(
                    id = "action-dialogue-3",
                    speaker = "Miyo",
                    text = LocalizedText.plain("No. I should listen before stepping in.")
                )
            )
        )

        return MiyoProject(
            projectId = "moonlit-rehearsal",
            title = LocalizedText.plain("Moonlit Rehearsal"),
            assets = assets,
            story = Story(
                startBlockId = "block-opening",
                blocks = listOf(
                    StoryBlock(
                        id = "block-opening",
                        label = "Opening chapter",
                        color = MiyoColorToken.Petal,
                        editorPosition = Vec2(96f, 128f),
                        scenes = listOf(openingScene, voiceScene, waitScene)
                    ),
                    StoryBlock(
                        id = "block-credits",
                        label = "Credits",
                        color = MiyoColorToken.Lagoon,
                        editorPosition = Vec2(420f, 360f),
                        scenes = listOf(
                            StoryScene(
                                id = "scene-credits",
                                title = "Credits",
                                actions = listOf(
                                    SceneAction.Dialogue(
                                        id = "action-credits",
                                        speaker = null,
                                        text = LocalizedText.plain("Thank you for playing.")
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            variables = listOf(
                MiyoVariable("met_voice", VariableType.Boolean, "false"),
                MiyoVariable("lantern_count", VariableType.Number, "0"),
                MiyoVariable("player_name", VariableType.Text, "")
            ),
            guiTheme = GuiTheme(),
            editor = EditorMetadata(
                selectedBlockId = "block-opening",
                selectedSceneId = "scene-arrival",
                selectedActionId = "action-dialogue-1"
            )
        )
    }

    private fun seaGlassStation(): MiyoProject =
        blankProject(2).copy(
            projectId = "sea-glass-station",
            title = LocalizedText.plain("Sea Glass Station"),
            assets = AssetIndex(
                listOf(
                    MiyoAsset(
                        id = "missing-harbor",
                        displayName = "Harbor sunset",
                        relativePath = "assets/scenery/harbor_sunset.png",
                        kind = AssetKind.Scenery,
                        status = AssetStatus.Missing
                    )
                )
            ),
            editor = EditorMetadata(
                autosaveLabel = "Draft",
                selectedBlockId = "project-2-block-opening",
                selectedSceneId = "project-2-scene-1",
                selectedActionId = "project-2-action-1"
            )
        )
}
