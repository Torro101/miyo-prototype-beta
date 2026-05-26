package com.nekomiyo.miyo.core.storage

import com.nekomiyo.miyo.core.model.AssetIndex
import com.nekomiyo.miyo.core.model.MiyoProject

interface MiyoProjectRepository {
    fun listProjects(): List<MiyoProject>
    fun createBlankProject(title: String): MiyoProject
    fun saveProject(project: MiyoProject): ProjectSaveResult
    fun registerAssets(projectId: String, assets: AssetIndex): ProjectSaveResult
}

data class ProjectSaveResult(
    val projectId: String,
    val checkpointId: String,
    val dirty: Boolean,
    val message: String
)

data class NekomiyoPackageLayout(
    val manifestPath: String = "manifest.json",
    val projectPath: String = "project.nekomiyo.json",
    val assetsPath: String = "assets/",
    val nodeLayoutPath: String = "node-layout.json",
    val scriptsPath: String = "scripts/",
    val engineVersionKey: String = "engine-version"
)

data class AutosavePolicy(
    val enabledByDefault: Boolean = true,
    val checkpointLabel: String = "autosave",
    val coalesceWindowMillis: Long = 650L,
    val maxCheckpointsPerProject: Int = 24
)
