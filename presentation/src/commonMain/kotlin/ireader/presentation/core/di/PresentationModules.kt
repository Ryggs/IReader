package ireader.presentation.core.di


import ireader.presentation.core.ScreenContentViewModel
import ireader.presentation.core.theme.AppThemeViewModel
import ireader.presentation.ui.home.explore.viewmodel.BooksState
import ireader.presentation.ui.home.explore.viewmodel.ExploreStateImpl
import ireader.presentation.ui.home.explore.viewmodel.ExploreViewModel
import ireader.presentation.ui.home.history.viewmodel.HistoryStateImpl
import ireader.presentation.ui.home.history.viewmodel.HistoryViewModel
import ireader.presentation.ui.home.library.viewmodel.LibraryStateImpl
import ireader.presentation.ui.home.library.viewmodel.LibraryViewModel
import ireader.presentation.ui.home.sources.extension.CatalogsStateImpl
import ireader.presentation.ui.home.sources.extension.ExtensionViewModel
import ireader.presentation.ui.home.sources.global_search.viewmodel.GlobalSearchStateImpl
import ireader.presentation.ui.home.sources.global_search.viewmodel.GlobalSearchViewModel
import ireader.presentation.ui.home.updates.viewmodel.UpdateStateImpl
import ireader.presentation.ui.home.updates.viewmodel.UpdatesViewModel
import ireader.presentation.ui.settings.MainSettingScreenViewModel
import ireader.presentation.ui.settings.appearance.AppearanceViewModel
import ireader.presentation.ui.settings.category.CategoryScreenViewModel
import ireader.presentation.ui.settings.downloader.DownloadStateImpl
import ireader.presentation.ui.settings.downloader.DownloaderViewModel
import ireader.presentation.ui.settings.font_screens.FontScreenStateImpl
import ireader.presentation.ui.settings.font_screens.FontScreenViewModel
import ireader.presentation.ui.settings.general.GeneralSettingScreenViewModel
import ireader.presentation.ui.settings.reader.ReaderSettingScreenViewModel
import ireader.presentation.ui.settings.repository.SourceRepositoryViewModel
import org.kodein.di.*

val PresentationModules = DI.Module("presentationModule") {

    bindSingleton { BooksState() }
    bindSingleton<HistoryStateImpl> { HistoryStateImpl() }
    bindSingleton<LibraryStateImpl> { LibraryStateImpl() }
    bindSingleton<CatalogsStateImpl> { CatalogsStateImpl() }
    bindSingleton<UpdateStateImpl> { UpdateStateImpl() }

    bindProvider<ExploreStateImpl> { ExploreStateImpl() }
    bindProvider<GlobalSearchStateImpl> { GlobalSearchStateImpl() }

    bindProvider<DownloadStateImpl> { DownloadStateImpl() }
    bindProvider<FontScreenStateImpl> { FontScreenStateImpl() }

    bindProvider { ScreenContentViewModel(instance()) }
    bindSingleton { AppThemeViewModel(instance(), instance(), instance()) }

    bindFactory<ExploreViewModel.Param, ExploreViewModel> { ExploreViewModel(instance(), instance(), instance(), instance(), instance(), it, instance(), instance()) }
    bindProvider { HistoryViewModel(instance(), instance(), instance()) }
    bindProvider { LibraryViewModel(instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance()) }
    bindProvider { ExtensionViewModel(instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance()) }
    bindFactory<GlobalSearchViewModel.Param, GlobalSearchViewModel> { GlobalSearchViewModel(instance(), instance(), instance(), instance(), it) }

    bindProvider { UpdatesViewModel(instance(), instance(), instance(), instance(), instance(), instance()) }


    bindProvider { MainSettingScreenViewModel(instance()) }
    bindProvider { AppearanceViewModel(instance(), instance()) }

    bindProvider { CategoryScreenViewModel(instance(), instance(), instance()) }
    bindProvider { DownloaderViewModel(instance(), instance(), instance(), instance()) }
    bindProvider { FontScreenViewModel(instance(), instance(), instance(), instance()) }
    bindProvider { GeneralSettingScreenViewModel(instance(), instance(), instance()) }
    bindProvider { ReaderSettingScreenViewModel(instance(), instance(), instance()) }
    bindProvider { SourceRepositoryViewModel(instance(), instance()) }

}