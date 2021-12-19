package ir.kazemcodes.infinity.local_feature.data.repository

import ir.kazemcodes.infinity.local_feature.data.BookDao
import ir.kazemcodes.infinity.local_feature.domain.model.BookEntity
import ir.kazemcodes.infinity.local_feature.domain.repository.LocalBookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalBookRepositoryImpl @Inject constructor(
    private val dao : BookDao
) : LocalBookRepository {
    override fun getBooks(): Flow<List<BookEntity>> {
        return dao.getInitializedBooks()
    }
    override fun getBookById(bookId: Int): Flow<BookEntity> {
        return dao.getBookById(bookId)
    }

    override fun getBookByName(bookName: String): Flow<BookEntity> {
        return dao.getBookByName(bookName)
    }

    override suspend fun insertBook(bookEntity: BookEntity) {
        return dao.insertBook(bookEntity)
    }

    override suspend fun insertBooks(bookEntities: List<BookEntity>) {
        return dao.insertBooks(bookEntities)
    }
    override suspend fun deleteBook(bookName: String) {
        return dao.deleteBook(bookName = bookName)
    }
    override suspend fun deleteAllBook() {
        return dao.deleteAllBook()
    }
}