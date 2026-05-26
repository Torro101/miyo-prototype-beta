package com.nekomiyo.miyo.core.exporting

import com.nekomiyo.miyo.core.model.AssetStatus
import com.nekomiyo.miyo.core.model.MiyoProject
import com.nekomiyo.miyo.core.model.MiyoSchemaVersion
import com.nekomiyo.miyo.core.storage.NekomiyoPackageLayout
import com.nekomiyo.miyo.core.validation.DiagnosticSeverity
import com.nekomiyo.miyo.core.validation.MiyoDiagnostic
import com.nekomiyo.miyo.core.validation.validateProject

data class MiyoExportPlan(
    val packageName: String,
    val manifest: MiyoPackageManifest,
    val files: List<MiyoPackageFile>,
    val diagnostics: List<MiyoDiagnostic>,
    val ready: Boolean
)

data class MiyoPackageManifest(
    val schemaVersion: Int,
    val projectId: String,
    val title: String,
    val defaultLocale: String,
    val locales: List<String>,
    val assetCount: Int,
    val engineVersion: String
)

data class MiyoPackageFile(
    val sourcePath: String,
    val packagePath: String,
    val required: Boolean
)

data class MiyoImportInspection(
    val manifest: MiyoPackageManifest?,
    val packageFiles: List<String>,
    val diagnostics: List<MiyoDiagnostic>,
    val accepted: Boolean
)

object MiyoExportPlanner {
    fun plan(project: MiyoProject, engineVersion: String = "0.1.0"): MiyoExportPlan {
        val layout = NekomiyoPackageLayout()
        val diagnostics = validateProject(project)
        val packageName = project.displayTitle()
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .ifEmpty { project.projectId }
            .plus(".nekomiyo")

        val files = buildList {
            add(MiyoPackageFile(sourcePath = "generated/manifest.json", packagePath = layout.manifestPath, required = true))
            add(MiyoPackageFile(sourcePath = "generated/project.nekomiyo.json", packagePath = layout.projectPath, required = true))
            add(MiyoPackageFile(sourcePath = "generated/node-layout.json", packagePath = layout.nodeLayoutPath, required = false))
            add(MiyoPackageFile(sourcePath = "generated/story.miyo", packagePath = "${layout.scriptsPath}story.miyo", required = true))
            project.assets.assets.forEach { asset ->
                add(
                    MiyoPackageFile(
                        sourcePath = asset.relativePath,
                        packagePath = "${layout.assetsPath}${asset.relativePath.removePrefix("assets/")}",
                        required = asset.status == AssetStatus.Ready
                    )
                )
            }
        }

        return MiyoExportPlan(
            packageName = packageName,
            manifest = MiyoPackageManifest(
                schemaVersion = project.schemaVersion,
                projectId = project.projectId,
                title = project.displayTitle(),
                defaultLocale = project.defaultLocale,
                locales = project.locales,
                assetCount = project.assets.assets.size,
                engineVersion = engineVersion
            ),
            files = files,
            diagnostics = diagnostics,
            ready = diagnostics.none { it.severity == DiagnosticSeverity.Error }
        )
    }
}

object MiyoImportInspector {
    fun inspectPackage(
        manifest: MiyoPackageManifest?,
        packageFiles: List<String>,
        supportedSchemaVersion: Int = MiyoSchemaVersion
    ): MiyoImportInspection {
        val layout = NekomiyoPackageLayout()
        val diagnostics = mutableListOf<MiyoDiagnostic>()

        if (manifest == null) {
            diagnostics += MiyoDiagnostic(
                severity = DiagnosticSeverity.Error,
                path = layout.manifestPath,
                message = "Package manifest is missing or unreadable."
            )
        } else {
            if (manifest.schemaVersion != supportedSchemaVersion) {
                diagnostics += MiyoDiagnostic(
                    severity = DiagnosticSeverity.Error,
                    path = layout.manifestPath,
                    message = "Package schema ${manifest.schemaVersion} is not supported by this build."
                )
            }
            if (manifest.locales.isEmpty()) {
                diagnostics += MiyoDiagnostic(
                    severity = DiagnosticSeverity.Error,
                    path = layout.manifestPath,
                    message = "Package must declare at least one locale."
                )
            }
        }

        listOf(layout.manifestPath, layout.projectPath).forEach { requiredPath ->
            if (requiredPath !in packageFiles) {
                diagnostics += MiyoDiagnostic(
                    severity = DiagnosticSeverity.Error,
                    path = requiredPath,
                    message = "Required package file '$requiredPath' is missing."
                )
            }
        }

        return MiyoImportInspection(
            manifest = manifest,
            packageFiles = packageFiles,
            diagnostics = diagnostics,
            accepted = diagnostics.none { it.severity == DiagnosticSeverity.Error }
        )
    }
}
