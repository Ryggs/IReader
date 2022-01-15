package ir.kazemcodes.infinity.presentation.library.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.zhuinden.simplestack.Backstack
import ir.kazemcodes.infinity.data.network.models.Source
import ir.kazemcodes.infinity.domain.models.remote.Book
import ir.kazemcodes.infinity.presentation.layouts.GridLayoutComposable
import ir.kazemcodes.infinity.presentation.layouts.LinearListDisplay
import ir.kazemcodes.infinity.feature_activity.presentation.BookDetailKey
import ir.kazemcodes.infinity.presentation.layouts.CompactGridLayoutComposable
import ir.kazemcodes.infinity.presentation.layouts.LayoutType


@Composable
fun LayoutComposable(
    backStack : Backstack,
    books: List<Book>,
    layout: LayoutType,
    scrollState: LazyListState = rememberLazyListState(),
    source: Source? = null,
) {

    when (layout) {
        is LayoutType.GridLayout -> {
            GridLayoutComposable(books = books,
                onClick = { index ->
                    backStack.goTo(
                        BookDetailKey(
                            book = books[index],
                            sourceName = if (source?.name != null) source.name else books[index].source
                                ?: ""
                        )
                    )
                }, scrollState = scrollState)
        }
        is LayoutType.ListLayout -> {
            LinearListDisplay(books = books, onClick = { index ->
                backStack.goTo(
                    BookDetailKey(
                        books[index],
                        sourceName = if (source?.name != null) source.name else books[index].source
                            ?: ""
                    )
                )
            }, scrollState = scrollState)
        }
        is LayoutType.CompactGrid -> {
            CompactGridLayoutComposable(
                books = books,
                onClick = { index ->
                    backStack.goTo(
                        BookDetailKey(
                            book = books[index],
                            sourceName = if (source?.name != null) source.name else books[index].source
                                ?: ""
                        )
                    )
                }, scrollState = scrollState)
        }
    }
}