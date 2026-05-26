package com.nekomiyo.miyo.core.model

data class AssetIndex(
    val assets: List<MiyoAsset> = emptyList()
) {
    fun byKind(kind: AssetKind): List<MiyoAsset> = assets.filter { it.kind == kind }

    fun find(assetId: String?): MiyoAsset? = assets.firstOrNull { it.id == assetId }

    fun countsByKind(): Map<AssetKind, Int> = AssetKind.entries.associateWith { kind -> byKind(kind).size }
}

data class MiyoAsset(
    val id: String,
    val displayName: String,
    val relativePath: String,
    val kind: AssetKind,
    val status: AssetStatus = AssetStatus.Ready,
    val locale: String? = null,
    val metadata: AssetMetadata = AssetMetadata()
)

data class AssetMetadata(
    val width: Int? = null,
    val height: Int? = null,
    val durationSeconds: Float? = null,
    val fileSizeLabel: String = "0.00 MB"
)

enum class AssetKind(val label: String) {
    Character("Characters"),
    Scenery("Scenery"),
    Bgm("BGM"),
    Sfx("SFX"),
    Cutscene("Cutscenes"),
    Gui("GUI"),
    Font("Fonts"),
    Other("Other")
}

enum class AssetStatus(val label: String) {
    Ready("Ready"),
    Missing("Missing"),
    Unsupported("Unsupported"),
    PendingImport("Pending import")
}
