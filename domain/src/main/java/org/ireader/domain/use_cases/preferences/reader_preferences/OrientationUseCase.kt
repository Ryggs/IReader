package org.ireader.domain.use_cases.preferences.reader_preferences

import org.ireader.common_models.FilterType
import org.ireader.common_models.SortType
import org.ireader.core_ui.theme.AppPreferences
import org.ireader.core_ui.theme.OrientationMode
import org.ireader.core_ui.ui.TextAlign
import javax.inject.Inject








class OrientationUseCase(
    private val appPreferences: AppPreferences,
) {
    fun save(orientation: OrientationMode) {
        appPreferences.orientation().set(orientation)
    }

    suspend fun read(): OrientationMode {
        return appPreferences.orientation().get()
    }
}
class TextAlignmentUseCase(
    private val appPreferences: AppPreferences,
) {
    fun save(textAlign: TextAlign) {
        appPreferences.textAlign().set(textAlign)
    }

    suspend fun read(): TextAlign {
        return appPreferences.textAlign().get()
    }
}

class SortersUseCase(
    private val appPreferences: AppPreferences,
) {
    fun save(value: Int) {
        appPreferences.sortLibraryScreen().set(value)
    }

    suspend fun read(): SortType {
        return mapSortType(appPreferences.sortLibraryScreen().get())
    }
}

class SortersDescUseCase(
    private val appPreferences: AppPreferences,
) {
    fun save(value: Boolean) {
        appPreferences.sortDescLibraryScreen().set(value)
    }

    suspend fun read(): Boolean {
        return appPreferences.sortDescLibraryScreen().get()
    }
}

fun mapSortType(input: Int): SortType {
    return when (input) {
        0 -> {
            SortType.Alphabetically
        }
        1 -> {
            SortType.LastRead
        }
        2 -> {
            SortType.LastChecked
        }
        3 -> {
            SortType.TotalChapters
        }
        4 -> {
            SortType.LatestChapter
        }
        5 -> {
            SortType.DateFetched
        }
        6 -> {
            SortType.DateAdded
        }
        else -> {
            SortType.LastRead
        }
    }
}

fun mapFilterType(input: Int): FilterType {
    return when (input) {
        0 -> {
            FilterType.Disable
        }
        else -> {
            FilterType.Unread
        }
    }
}

class TextReaderPrefUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    fun savePitch(value: Float) {
        appPreferences.speechPitch().set(value)
    }

    suspend  fun readPitch(): Float {
        return appPreferences.speechPitch().get()
    }

    fun saveRate(value: Float) {
        appPreferences.speechRate().set(value)
    }

    suspend fun readRate(): Float {
        return appPreferences.speechRate().get()
    }

    fun saveLanguage(value: String) {
        appPreferences.speechLanguage().set(value)
    }

    suspend fun readLanguage(): String {
        return appPreferences.speechLanguage().get()
    }

    fun saveVoice(value: String) {
        appPreferences.speechVoice().set(value)
    }

    suspend  fun readVoice(): String {
        return appPreferences.speechVoice().get()
    }

    fun saveAutoNext(value: Boolean) {
        appPreferences.readerAutoNext().set(value)
    }

    suspend  fun readAutoNext(): Boolean {
        return appPreferences.readerAutoNext().get()
    }
}
