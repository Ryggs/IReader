package ir.kazemcodes.infinity.core.domain.use_cases.local.book_usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import ir.kazemcodes.infinity.core.domain.models.Book
import ir.kazemcodes.infinity.core.domain.repository.LocalBookRepository
import ir.kazemcodes.infinity.core.utils.Constants
import kotlinx.coroutines.flow.Flow

/**
 * get a paging data that is used in library screen
 */
class GetBooksByQueryByPagination(private val localBookRepository: LocalBookRepository) {
    operator fun invoke(query: String): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE,
                maxSize = Constants.MAX_PAGE_SIZE, enablePlaceholders = true),
            pagingSourceFactory = {
                localBookRepository.getBooksByQueryPagingSource(query)
            }
        ).flow
    }
}