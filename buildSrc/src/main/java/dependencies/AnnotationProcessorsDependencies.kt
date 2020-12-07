

package dependencies

/**
 * Project annotation processor dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object AnnotationProcessorsDependencies {
    const val DAGGER_HILT = "com.google.dagger:hilt-android-compiler:${BuildDependenciesVersions.DAGGER_HILT}"
    const val ROOM = "androidx.room:room-compiler:${BuildDependenciesVersions.ROOM}"
    const val DAGGER_HILT_VM = "androidx.hilt:hilt-compiler:${BuildDependenciesVersions.DAGGER_HILT_VM}"
}
