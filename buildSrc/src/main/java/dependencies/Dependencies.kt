/**
 * Created by Dimi on 10/22/20 1:39 PM
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 10/22/20 1:10 AM
 */

package dependencies

/**
 * Project dependencies, makes it easy to include external binaries or
 * other library modules to build.
 */
object Dependencies {
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${BuildDependenciesVersions.KOTLIN}"
    const val APPCOMPAT = "androidx.appcompat:appcompat:${BuildDependenciesVersions.APPCOMPAT}"
    const val MATERIAL = "com.google.android.material:material:${BuildDependenciesVersions.MATERIAL}"
    const val CORE_KTX = "androidx.core:core-ktx:${BuildDependenciesVersions.CORE_KTX}"
    const val CONSTRAIN_LAYOUT = "androidx.constraintlayout:constraintlayout:${BuildDependenciesVersions.CONSTRAIN_LAYOUT}"
    const val NAVIGATION_FRAGMENT = "androidx.navigation:navigation-fragment-ktx:${BuildDependenciesVersions.NAVIGATION}"
    const val NAVIGATION_UI = "androidx.navigation:navigation-ui-ktx:${BuildDependenciesVersions.NAVIGATION}"
    const val DAGGER_HILT = "com.google.dagger:hilt-android:${BuildDependenciesVersions.DAGGER_HILT}"
    const val ROOM = "androidx.room:room-runtime:${BuildDependenciesVersions.ROOM}"
    const val ROOM_KTX = "androidx.room:room-ktx:${BuildDependenciesVersions.ROOM}"

    const val DATASTORE_PREF = "androidx.datastore:datastore-preferences:${BuildDependenciesVersions.DATASTORE_PREF}"

    const val LIFECYCLE_EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:${BuildDependenciesVersions.LIFECYCLE}"
    const val LIFECYCLE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${BuildDependenciesVersions.LIFECYCLE}"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${BuildDependenciesVersions.FRAGMENT_KTX}"
    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${BuildDependenciesVersions.COROUTINES}"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${BuildDependenciesVersions.COROUTINES}"
    const val DAGGER_HILT_VM = "androidx.hilt:hilt-lifecycle-viewmodel:${BuildDependenciesVersions.DAGGER_HILT_VM}"

    const val SWIPE_REFRESH_LAYOUT = "androidx.swiperefreshlayout:swiperefreshlayout:${BuildDependenciesVersions.SWIPE_REFRESH_LAYOUT}"
    const val RECYCLE_VIEW = "androidx.recyclerview:recyclerview:${BuildDependenciesVersions.RECYCLE_VIEW}"

    const val MATERIAL_DIALOG_CORE = "com.afollestad.material-dialogs:core:${BuildDependenciesVersions.M_DIALOG}"
    const val MATERIAL_DIALOG_COLORS = "com.afollestad.material-dialogs:color:${BuildDependenciesVersions.M_DIALOG}"
    const val MATERIAL_DIALOG_DATE_TIME = "com.afollestad.material-dialogs:datetime:${BuildDependenciesVersions.M_DIALOG}"
    const val MATERIAL_DIALOG_BOTTOM_SHEETS = "com.afollestad.material-dialogs:bottomsheets:${BuildDependenciesVersions.M_DIALOG}"

    const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:${BuildDependenciesVersions.LIVEDATA_KTX}"

    const val PAGING = "androidx.paging:paging-runtime-ktx:${BuildDependenciesVersions.PAGING}"
}
