package versioning

fun getBuildNumber(): Int {
    return System.getenv("buildNumber")?.toIntOrNull() ?: 0
}

/**
 * Generates versionCode in format: MMMmmppbbb
 * MMM = major, mm = minor, pp = patch, bbb = build number
 */
fun generateVersionCode(versionMajor: Int, versionMinor: Int, versionPatch: Int): Int {
    val versionCode = versionMajor * 10000000 +
            versionMinor * 100000 +
            versionPatch * 1000 +
            (getBuildNumber() % 1000)
    println("Version code: $versionCode")
    return versionCode
}

/**
 * Generates versionName in format: <major>.<minor>.<patch>[-<classifier>]
 */
fun generateVersionName(
    versionMajor: Int,
    versionMinor: Int,
    versionPatch: Int,
    versionClassifier: String?
): String {
    val base = "$versionMajor.$versionMinor.$versionPatch"
    val versionName = if (!versionClassifier.isNullOrBlank()) "$base-$versionClassifier" else base
    println("Version name: $versionName")
    return versionName
}