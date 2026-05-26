package com.nekomiyo.miyo

import androidx.compose.runtime.Composable
import com.nekomiyo.miyo.ui.editor.EditorShell
import com.nekomiyo.miyo.ui.hub.HubScreen
import com.nekomiyo.miyo.ui.state.MiyoRoute
import com.nekomiyo.miyo.ui.state.rememberMiyoAppState
import com.nekomiyo.miyo.ui.platform.MiyoRouteOrientation
import com.nekomiyo.miyo.ui.theme.MiyoTheme

@Composable
fun MiyoApp() {
    val appState = rememberMiyoAppState()

    MiyoTheme {
        MiyoRouteOrientation(appState.route)
        when (appState.route) {
            MiyoRoute.Hub -> HubScreen(
                projects = appState.projectSummaries,
                selectedTab = appState.hubTab,
                onTabSelected = appState::selectHubTab,
                onCreateProject = appState::createProject,
                onOpenProject = appState::openProject
            )
            MiyoRoute.Editor -> {
                val project = appState.currentProject
                if (project != null) {
                    EditorShell(
                        project = project,
                        diagnostics = appState.currentDiagnostics,
                        selectedMode = appState.editorMode,
                        simpleTab = appState.simpleTab,
                        selectedBlockId = appState.selectedBlockId,
                        selectedSceneId = appState.selectedSceneId,
                        selectedActionId = appState.selectedActionId,
                        selectedAreaId = appState.selectedAreaId,
                        selectedAssetKind = appState.selectedAssetKind,
                        onModeSelected = appState::selectEditorMode,
                        onSimpleTabSelected = appState::selectSimpleTab,
                        onAssetKindSelected = appState::selectAssetKind,
                        onSceneSelected = appState::selectScene,
                        onActionSelected = appState::selectAction,
                        onAreaSelected = appState::selectInteractiveArea,
                        onAddInteractiveArea = appState::addInteractiveArea,
                        onResizeInteractiveArea = appState::resizeInteractiveArea,
                        onBackToHub = appState::openHub
                    )
                }
            }
        }
    }
}
