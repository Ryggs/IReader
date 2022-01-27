package ir.kazemcodes.infinity.feature_reader.presentation.reader


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.WindowManager
import android.webkit.WebView
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zhuinden.simplestack.ScopedServices
import ir.kazemcodes.infinity.core.data.network.models.Source
import ir.kazemcodes.infinity.core.data.network.utils.launchIO
import ir.kazemcodes.infinity.core.data.network.utils.launchUI
import ir.kazemcodes.infinity.core.domain.models.Book
import ir.kazemcodes.infinity.core.domain.models.Chapter
import ir.kazemcodes.infinity.core.domain.models.FontType
import ir.kazemcodes.infinity.core.domain.use_cases.local.DeleteUseCase
import ir.kazemcodes.infinity.core.domain.use_cases.local.LocalGetBookUseCases
import ir.kazemcodes.infinity.core.domain.use_cases.local.LocalGetChapterUseCase
import ir.kazemcodes.infinity.core.domain.use_cases.local.LocalInsertUseCases
import ir.kazemcodes.infinity.core.domain.use_cases.preferences.PreferencesUseCase
import ir.kazemcodes.infinity.core.domain.use_cases.remote.RemoteUseCases
import ir.kazemcodes.infinity.core.presentation.theme.fonts
import ir.kazemcodes.infinity.core.presentation.theme.readerScreenBackgroundColors
import ir.kazemcodes.infinity.core.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jsoup.Jsoup
import uy.kohesive.injekt.injectLazy

/**
 * the order of this screen is
 * first we need to get the book from room then
 * we use the areReversedChapter to understanding the order of
 * chapters for chapterList slider then get Chapters using pagination for chapter drawer.
 */
class ReaderScreenViewModel(
    private val preferencesUseCase: PreferencesUseCase,
    private val source: Source,
    private val bookId: Int,
    private val chapterId: Int,
    private val getBookUseCases: LocalGetBookUseCases,
    private val getChapterUseCase: LocalGetChapterUseCase,
    private val remoteUseCases: RemoteUseCases,
    private val insertUseCases: LocalInsertUseCases,
    private val deleteUseCase: DeleteUseCase,
) : ScopedServices.Registered {
    private val _state = mutableStateOf(ReaderScreenState(source = source))
    val state: State<ReaderScreenState> = _state


    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _chapters = MutableStateFlow<PagingData<Chapter>>(PagingData.empty())
    val chapters = _chapters


    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val webView by injectLazy<WebView>()

    override fun onServiceRegistered() {
        val initState = state.value
        _state.value = state.value.copy(
            book = initState.book.copy(id = bookId),
            chapter = initState.chapter.copy(chapterId = chapterId),
            source = source
        )
        getLocalBookByName()

        getLocalChaptersByPaging()
        readPreferences()
    }


    private fun readPreferences() {
        readSelectedFontState()
        readFontSize()
        readBackgroundColor()
        readFontHeight()
        readParagraphDistance()
        readParagraphIndent()
    }

    fun onEvent(event: ReaderEvent) {
        when (event) {

            is ReaderEvent.ChangeBrightness -> {
                saveBrightness(event.brightness, event.context)
            }
            is ReaderEvent.ChangeFontSize -> {
                saveFontSize(event.fontSizeEvent)
            }
            is ReaderEvent.ChangeFont -> {
                saveFont(event.fontType)
            }
            is ReaderEvent.ToggleReaderMode -> {
                toggleReaderMode(event.enable)
            }
        }
    }


    private fun toggleReaderMode(enable: Boolean? = null) {
        _state.value =
            state.value.copy(isReaderModeEnable = enable ?: !state.value.isReaderModeEnable,
                isMainBottomModeEnable = true,
                isSettingModeEnable = false)
    }

    fun toggleSettingMode(enable: Boolean, returnToMain: Boolean? = null) {
        if (returnToMain.isNull()) {
            _state.value =
                state.value.copy(isSettingModeEnable = enable, isMainBottomModeEnable = false)

        } else {
            _state.value =
                state.value.copy(isSettingModeEnable = false, isMainBottomModeEnable = true)
        }
    }

    private fun getChapters() {
        coroutineScope.launchIO {
            getChapterUseCase.getChaptersByBookId(bookId = bookId,
                isAsc = state.value.book.areChaptersReversed)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            if (result.data != null) {
                                _state.value = state.value.copy(
                                    chapters = result.data,
                                    isChapterLoaded = true,
                                )
                                _state.value =
                                    state.value.copy(currentChapterIndex = result.data.indexOfFirst { state.value.chapter.chapterId == it.chapterId })
                            }
                        }
                        is Resource.Error -> {
                        }
                    }
                }
        }

    }

    fun getChapter(chapter: Chapter) {
        _state.value = state.value.copy(chapter = chapter)
        _state.value = state.value.copy(
            isLoading = true,
            error = UiText.noError(),
            isLoaded = false,
        )
        getChapterUseCase.getOneChapterById(chapterId = chapter.chapterId)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) {
                            _state.value = state.value.copy(
                                chapter = result.data,
                                isLoading = false,
                                isLoaded = true,
                                error = UiText.noError()
                            )
                            toggleLastReadAndUpdateChapterContent(result.data)
                            if (state.value.chapter.content.joinToString().isBlank()) {
                                getReadingContentRemotely()
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.value =
                            state.value.copy(
                                isLoading = false,
                                isLoaded = false,
                            )
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.uiText ?: UiText.unknownError()
                            .asString()))
                        getReadingContentRemotely()
                    }
                }

            }.launchIn(coroutineScope)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFromWebView() {
        val webView by injectLazy<WebView>()
        coroutineScope.launch {
            _eventFlow.emit(UiEvent.ShowSnackbar(
                uiText = UiText.DynamicString("Trying to fetch chapter's content").asString()
            ))

            val chapter = source.contentFromElementParse(Jsoup.parse(webView.getHtml()))
            if (!chapter.content.isNullOrEmpty() && state.value.isBookLoaded && state.value.isChapterLoaded && webView.originalUrl == state.value.chapter.link) {
                _state.value = state.value.copy(isLoading = false,
                    error = UiText.noError(),
                    chapter = state.value.chapter.copy(content = chapter.content))
                toggleLastReadAndUpdateChapterContent(state.value.chapter.copy(content = chapter.content))

                _eventFlow.emit(UiEvent.ShowSnackbar(
                    uiText = UiText.DynamicString("${state.value.chapter.title} of ${state.value.chapter.bookName} was Fetched")
                        .asString()
                ))
                if (state.value.chapter.content.size > 10) {
                    _state.value = state.value.copy(isLoaded = true)
                }
                _state.value = state.value
            } else {
                _eventFlow.emit(UiEvent.ShowSnackbar(
                    uiText = UiText.DynamicString("Failed to to get the content").asString()
                ))
            }
        }

    }

    fun getReadingContentRemotely() {
        _state.value = state.value.copy(
            isLoading = true,
            error = UiText.noError(),
            isLoaded = false,
        )
        remoteUseCases.getRemoteReadingContent(state.value.chapter, source = source)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) {
                            _state.value = state.value
                                .copy(
                                    chapter = state.value.chapter.copy(content = result.data.content),
                                    isLoading = false,
                                    error = UiText.noError(),
                                    isLoaded = true,
                                )
                            toggleLastReadAndUpdateChapterContent(state.value.chapter.copy(
                                haveBeenRead = true))
                        }
                    }
                    is Resource.Error -> {
                        _state.value =
                            state.value.copy(
                                isLoading = false,
                                isLoaded = false,
                            )
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.uiText ?: UiText.unknownError()
                            .asString()))
                    }
                }
            }.launchIn(coroutineScope)
    }

    @Suppress()
    private fun getLocalBookByName() {
        coroutineScope.launchIO {
            getBookUseCases.getBookById(id = bookId).first { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null && result.data != Book.create()) {
                            _state.value = state.value.copy(
                                book = result.data,
                                isBookLoaded = true,
                                isChaptersReversed = result.data.areChaptersReversed
                            )
                            insertUseCases.insertBook(book = result.data.copy(lastRead = System.currentTimeMillis(),
                                unread = !result.data.unread))
                            getChapters()
                            getChapter(state.value.chapter)
                            true
                        } else {
                            false
                        }
                    }
                    is Resource.Error -> {
                        false
                    }
                }
            }
        }

    }

    private fun toggleLastReadAndUpdateChapterContent(chapter: Chapter) {
        coroutineScope.launch(Dispatchers.IO) {
            deleteUseCase.deleteChapterByChapter(chapter)
            state.value.chapters.filter {
                it.lastRead
            }.forEach {
                insertUseCases.insertChapter(it.copy(lastRead = false))
            }
            insertUseCases.insertChapter(chapter.copy(haveBeenRead = true, lastRead = true))
        }
    }

    fun reverseChapters() {
        _state.value = state.value.copy(isAsc = !state.value.isAsc)
    }

    fun getLocalChaptersByPaging() {
        coroutineScope.launch(Dispatchers.IO) {
            getChapterUseCase.getLocalChaptersByPaging(bookId = bookId, isAsc = state.value.isAsc)
                .cachedIn(coroutineScope).collect { snapshot ->
                    _chapters.value = snapshot
                }
        }

    }


    private fun readSelectedFontState() {
        _state.value = state.value.copy(font = preferencesUseCase.readSelectedFontStateUseCase())
    }

    fun readBrightness(context: Context) {
        val brightness = preferencesUseCase.readBrightnessStateUseCase()
        val activity = context.findAppCompatAcivity()!!
        val window = activity.window
        val layoutParams: WindowManager.LayoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams
        _state.value = state.value.copy(brightness = brightness)
    }

    private fun readFontSize() {
        _state.value = state.value.copy(fontSize = preferencesUseCase.readFontSizeStateUseCase())
    }

    private fun readParagraphDistance() {
        _state.value =
            state.value.copy(distanceBetweenParagraphs = preferencesUseCase.readParagraphDistanceUseCase())
    }

    private fun readParagraphIndent() {
        _state.value =
            state.value.copy(paragraphsIndent = preferencesUseCase.readParagraphIndentUseCase())
    }

    private fun readFontHeight() {
        _state.value = state.value.copy(lineHeight = preferencesUseCase.readFontHeightUseCase())
    }

    private fun readBackgroundColor() {
        val color = readerScreenBackgroundColors[preferencesUseCase.getBackgroundColorUseCase()]
        _state.value =
            state.value.copy(backgroundColor = color.color, textColor = color.onTextColor)
    }


    @SuppressLint("SourceLockedOrientationActivity")
    fun readOrientation(context: Context) {
        val activity = context.findAppCompatAcivity()!!
        when (preferencesUseCase.readOrientationUseCase()) {
            0 -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                _state.value = state.value.copy(orientation = Orientation.Portrait)
            }
            1 -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                _state.value = state.value.copy(orientation = Orientation.Landscape)
            }
        }
    }

    fun changeBackgroundColor(colorIndex: Int) {
        val color = readerScreenBackgroundColors[colorIndex]
        _state.value =
            state.value.copy(backgroundColor = color.color, textColor = color.onTextColor)
        preferencesUseCase.setBackgroundColorUseCase(colorIndex)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun saveOrientation(context: Context) {
        val activity = context.findAppCompatAcivity()!!
        when (state.value.orientation) {
            is Orientation.Landscape -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                _state.value = state.value.copy(orientation = Orientation.Portrait)
                preferencesUseCase.saveOrientationUseCase(Orientation.Portrait.index)
            }
            is Orientation.Portrait -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                _state.value = state.value.copy(orientation = Orientation.Landscape)
                preferencesUseCase.saveOrientationUseCase(Orientation.Landscape.index)
            }
        }
    }

    fun saveFontHeight(isIncreased: Boolean) {
        val currentFontHeight = state.value.lineHeight
        if (isIncreased) {
            preferencesUseCase.saveFontHeightUseCase(currentFontHeight + 1)
            _state.value = state.value.copy(lineHeight = currentFontHeight + 1)

        } else if (currentFontHeight > 20 && !isIncreased) {
            preferencesUseCase.saveFontHeightUseCase(currentFontHeight - 1)
            _state.value = state.value.copy(lineHeight = currentFontHeight - 1)
        }
    }

    fun saveParagraphDistance(isIncreased: Boolean) {
        val currentDistance = state.value.distanceBetweenParagraphs
        if (isIncreased) {
            preferencesUseCase.saveParagraphDistanceUseCase(currentDistance + 1)
            _state.value = state.value.copy(distanceBetweenParagraphs = currentDistance + 1)

        } else if (currentDistance > 1 && !isIncreased) {
            preferencesUseCase.saveParagraphDistanceUseCase(currentDistance - 1)
            _state.value = state.value.copy(distanceBetweenParagraphs = currentDistance - 1)
        }
    }

    fun saveParagraphIndent(isIncreased: Boolean) {
        val paragraphsIndent = state.value.paragraphsIndent
        if (isIncreased) {
            preferencesUseCase.saveParagraphIndentUseCase(paragraphsIndent + 1)
            _state.value = state.value.copy(paragraphsIndent = paragraphsIndent + 1)

        } else if (paragraphsIndent > 1 && !isIncreased) {
            preferencesUseCase.saveParagraphIndentUseCase(paragraphsIndent - 1)
            _state.value = state.value.copy(paragraphsIndent = paragraphsIndent - 1)
        }
    }

    private fun saveFontSize(event: FontSizeEvent) {
        if (event == FontSizeEvent.Increase) {
            _state.value = state.value.copy(fontSize = state.value.fontSize + 1)
            preferencesUseCase.saveFontSizeStateUseCase(state.value.fontSize)
        } else {
            if (state.value.fontSize > 0) {
                _state.value = state.value.copy(fontSize = state.value.fontSize - 1)
                preferencesUseCase.saveFontSizeStateUseCase(state.value.fontSize)
            }
        }
    }

    private fun saveFont(fontType: FontType) {
        _state.value = state.value.copy(font = fontType)
        preferencesUseCase.saveSelectedFontStateUseCase(fonts.indexOf(fontType))
    }

    private fun saveBrightness(brightness: Float, context: Context) {
        val activity = context.findAppCompatAcivity()!!
        val window = activity.window
        _state.value = state.value.copy(brightness = brightness)
        val layoutParams: WindowManager.LayoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams

        preferencesUseCase.saveBrightnessStateUseCase(brightness)
    }


    /**
     * need a index, there is no need to confuse the index because the list reversed
     */
    fun updateChapterSliderIndex(index: Int) {
        _state.value = state.value.copy(currentChapterIndex = index)
    }

    /**
     * get the index pf chapter based on the reversed state
     */
    fun getCurrentIndexOfChapter(chapter: Chapter): Int {
        val chaptersById: List<Int> = state.value.chapters.map { it.chapterId }
        return if (chaptersById.indexOf(chapter.chapterId) != -1) chaptersById.indexOf(chapter.chapterId) else 0
    }

    private fun getCurrentIndex(): Int {
        return if (state.value.currentChapterIndex < 0) {
            0
        } else if (state.value.currentChapterIndex > (state.value.chapters.lastIndex)) {
            state.value.chapters.lastIndex
        } else if (state.value.currentChapterIndex == -1) {
            0
        } else {
            state.value.currentChapterIndex
        }
    }

    fun getCurrentChapterByIndex(): Chapter {
        return try {
            state.value.chapters[getCurrentIndex()]
        }catch (e:Exception) {
            state.value.chapters[0]
        }
    }

    fun reverseSlider() {
        if (!state.value.isChapterReversingInProgress) {
            _state.value =
                state.value.copy(book = state.value.book.copy(areChaptersReversed = !state.value.book.areChaptersReversed),
                    isChapterReversingInProgress = true)

            coroutineScope.launch(Dispatchers.IO) {
                _eventFlow.emit(UiEvent.ShowSnackbar(UiText.DynamicString("Reversing Chapters...")
                    .asString()))
                insertUseCases.insertBook(state.value.book.copy(areChaptersReversed = state.value.book.areChaptersReversed))
                _eventFlow.emit(UiEvent.ShowSnackbar(UiText.DynamicString("Chapters were reversed")
                    .asString()))
            }
            updateChapterSliderIndex(getCurrentIndexOfChapter(state.value.chapter))
            getChapters()
            _state.value = state.value.copy(isChapterReversingInProgress = false)
        }

    }

    fun showSnackBar(message: String) {
        coroutineScope.launchUI {
            _eventFlow.emit(UiEvent.ShowSnackbar(UiText.DynamicString(message).asString()))

        }
    }


    override fun onServiceUnregistered() {
        coroutineScope.cancel()
    }
}