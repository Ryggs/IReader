package ir.kazemcodes.infinity.data.network.models

import android.content.Context
import ir.kazemcodes.infinity.api_feature.network.GET
import ir.kazemcodes.infinity.domain.models.remote.Book
import ir.kazemcodes.infinity.domain.models.remote.Chapter
import ir.kazemcodes.infinity.util.asJsoup
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * a class that create source for the application
 * note that every item can be null, but be careful, you need to fill needed items related to supported feature
 * @param _baseUrl the base url of site like https://freewebnovel.com/
 * @param _lang the language of source
 * @param _name the name of site
 * @param _supportsLatest change the value to true if your source support this feature
 * @param _supportsMostPopular change the value  to true if your source support this feature
 * @param _supportsSearch change the value to true if your source support this feature
 * @param _latestUpdateEndpoint the endpoint of url where latestUpdate books  is located like and replace the page number with {page}: /latest-novel/{page}/
 * @param _popularEndpoint the endpoint of url where most popular books  is located like and replace the page number with {page} if the source support it: /most-popular-novel/
 * @param _chaptersEndpoint the endpoint of url where chapterEndpoint this value is better to be null: /a-record-of-a-mortals-journey-to-immortality-novel/chapter-1.html
 * @param _contentEndpoint the endpoint of url where _contentEndpoint this value is better to be null: /a-record-of-a-mortals-journey-to-immortality-novel.html
 * @param _searchEndpoint the endpoint of url where _searchEndpoint is located like and replace the query with {query}: /search?searchkey={query}"
 * @param _latestBookSelector this is the element of one item where every details such as name, url and book cover is located Note: This selector need to contain only one book
 * @param _nameLatestSelector selector where name of book is located, Note: this selector can be left empty if there is no selector
 * @param _nameLatestAtt Attribute where name of book is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _linkLatestSelector selector where link of book is located, Note: this selector can be left empty if there is no selector
 * @param _linkLatestAtt Attribute where link of link is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _coverLatestSelector selector where cover of book is located, Note: this selector can be left empty if there is no selector
 * @param _coverLatestAtt Attribute where cover of link is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _latestNextPageSelector the selector where next page button is located
 * @param _latestNextPageValue the value of the button
 * @param _popularBookSelector this is the element of one item where every details such as name, url and book cover is located Note: This selector need to contain only one book
 * @param _namePopularSelector selector where name of book is located, Note: this selector can be left empty if there is no selector
 * @param _namePopularAtt Attribute where name of book is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _linkPopularSelector selector where link of book is located, Note: this selector can be left empty if there is no selector
 * @param _linkPopularAtt Attribute where link of link is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _coverPopularSelector selector where cover of book is located, Note: this selector can be left empty if there is no selector
 * @param _coverPopularAtt Attribute where cover of link is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _popularNextBookSelector  the selector where next page button is located
 * @param _popularNextBookValue  the value of the button
 * @param _searchBookSelector this is the element of one item where every details such as name, url and book cover is located Note: This selector need to contain only one book
 * @param _nameSearchedSelector selector where name of book is located, Note: this selector can be left empty if there is no selector
 * @param _nameSearchedAtt  Attribute where name of book is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _linkSearchedSelector selector where link of book is located, Note: this selector can be left empty if there is no selector
 * @param _linkSearchedAtt Attribute where link of link is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _coverSearchedSelector selector where cover of book is located, Note: this selector can be left empty if there is no selector
 * @param _coverSearchedAtt Attribute where cover of link is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _hasNextSearchedBooksNextPageSelector the selector where next page button is located
 * @param _hasNextSearchedBookNextPageValue the value of the button
 * @param _nameDetailSelector selector where name of book is located, Note: this selector can be left empty if there is no selector
 * @param _nameDetailAtt Attribute where name of book is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _authorDetailBookSelector selector where author of book is located, Note: this selector can be left empty if there is no selector
 * @param _authorDetailBookAtt Attribute where name of author is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _descriptionDetailSelector selector where description of book is located, Note: this selector can be left empty if there is no selector
 * @param _descriptionDetailBookAtt Attribute where name of description is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _categoryDetailSelector selector where category of book is located, Note: this selector can be left empty if there is no selector
 * @param _categoryDetailAtt  Attribute where name of category is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _chapterListSelector  this is the element of one item where every details such as name, url is located Note: This selector need to contain only one chapter
 * @param _linkChapterSelector selector where link of book is located, Note: this selector can be left empty if there is no selector
 * @param _linkChapterAtt Attribute where link of link is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _nameChapterSelector selector where name of book is located, Note: this selector can be left empty if there is no selector
 * @param _nameChapterAtt Attribute where name of book is located, Note: this Attribute can be left empty if there is no Attribute
 * @param _hasNextChapterListSelector the selector where next page button is located
 * @param _hasNextChapterListValue the value of the button
 * @param _isChapterStatsFromFirst change it true if chapter list start from first chapter
 * @param _chapterPageContentSelector  selector where content of chapter is located, Note: this selector can be left empty if there is no selector
 * @param _chapterPageContentAtt  Attribute where content of chapter is located, Note: this Attribute can be left empty if there is no Attribute
 * @param context context of application
 *
 */
class SourceCreator(
    context: Context,
    private val _baseUrl: String,
    private val _lang: String,
    private val _name: String,
    private val _supportsLatest: Boolean = false,
    private val _supportsMostPopular: Boolean = false,
    private val _supportsSearch: Boolean = false,
    private val _latestUpdateEndpoint: String? = null,
    private val _popularEndpoint: String? = null,
    private val _searchEndpoint: String? = null,
    private val _chaptersEndpoint: String? = null,
    private val _contentEndpoint: String? = null,
    private val _popularBookSelector: String? = null,
    private val _popularNextBookSelector: String? = null,
    private val _popularNextBookValue: String? = null,
    private val _linkPopularSelector: String? = null,
    private val _linkPopularAtt: String? = null,
    private val _namePopularSelector: String? = null,
    private val _namePopularAtt: String? = null,
    private val _coverPopularSelector: String? = null,
    private val _coverPopularAtt: String? = null,
    private val _latestBookSelector: String? = null,
    private val _latestNextPageSelector: String? = null,
    private val _latestNextPageValue: String? = null,
    private val _linkLatestSelector: String? = null,
    private val _linkLatestAtt: String? = null,
    private val _nameLatestSelector: String? = null,
    private val _nameLatestAtt: String? = null,
    private val _coverLatestSelector: String? = null,
    private val _coverLatestAtt: String? = null,
    private val _nameDetailSelector: String? = null,
    private val _nameDetailAtt: String? = null,
    private val _descriptionDetailSelector: String? = null,
    private val _descriptionDetailBookAtt: String? = null,
    private val _authorDetailBookSelector: String? = null,
    private val _authorDetailBookAtt: String? = null,
    private val _categoryDetailSelector: String? = null,
    private val _categoryDetailAtt: String? = null,
    private val _isChapterStatsFromFirst :Boolean = true,
    private val _hasNextChapterListSelector: String? = null,
    private val _hasNextChapterListValue: String? = null,
    private val _chapterListSelector: String? = null,
    private val _linkChapterSelector: String? = null,
    private val _linkChapterAtt: String? = null,
    private val _nameChapterSelector: String? = null,
    private val _nameChapterAtt: String? = null,
    private val _chapterPageContentSelector: String? = null,
    private val _chapterPageContentAtt: String? = null,
    private val _searchBookSelector: String? = null,
    private val _linkSearchedSelector: String? = null,
    private val _linkSearchedAtt: String? = null,
    private val _nameSearchedSelector: String? = null,
    private val _nameSearchedAtt: String? = null,
    private val _coverSearchedSelector: String? = null,
    private val _coverSearchedAtt: String? = null,
    private val _hasNextSearchedBooksNextPageSelector: String? = null,
    private val _hasNextSearchedBookNextPageValue: String? = null,


    ) : ParsedHttpSource(context) {
    override val lang: String
        get() = _lang
    override val name: String
        get() = _name
    override val supportsLatest: Boolean
        get() = _supportsLatest
    override val supportsMostPopular: Boolean
        get() = _supportsMostPopular
    override val baseUrl: String = _baseUrl

    override val supportSearch: Boolean  = _supportsSearch


    override fun fetchLatestUpdatesEndpoint(): String? = _latestUpdateEndpoint
    override fun fetchPopularEndpoint(): String? = _popularEndpoint
    override fun fetchSearchBookEndpoint(): String? = _searchEndpoint
    override fun fetchChaptersEndpoint(): String? = _chaptersEndpoint
    override fun fetchContentEndpoint(): String? = _contentEndpoint

    override fun popularBookSelector(): String? = _popularBookSelector

    override fun popularBookNextPageSelector(): String? = _popularNextBookSelector

    override fun popularBookRequest(page: Int): Request =
        GET("$baseUrl${
            getUrlWithoutDomain(fetchPopularEndpoint()?.replace(pageFormat,
                page.toString()) ?: "")
        }")


    override fun popularBookFromElement(element: Element): Book {
        val book: Book = Book.create()

        val selectorLink = _linkPopularSelector
        val attLink = _linkPopularAtt
        val selectorName = _namePopularSelector
        val attName = _namePopularAtt
        val selectorCover = _coverPopularSelector
        val attCover = _coverPopularAtt

        book.link = selectorReturnerStringType(element, selectorLink, attLink)
        book.bookName = selectorReturnerStringType(element, selectorName, attName)
        book.coverLink = selectorReturnerStringType(element, selectorCover, attCover)


        return book
    }


    override fun latestUpdatesSelector(): String? = _latestBookSelector


    override fun latestUpdatesFromElement(element: Element): Book {
        val book: Book = Book.create()

        val selectorLink = _linkLatestSelector
        val attLink = _linkLatestAtt
        val selectorName = _nameLatestSelector
        val attName = _nameLatestAtt
        val selectorCover = _coverLatestSelector
        val attCover = _coverLatestAtt

        book.link = baseUrl + getUrlWithoutDomain(selectorReturnerStringType(element, selectorLink, attLink))
        book.bookName = selectorReturnerStringType(element, selectorName, attName)
        book.coverLink = selectorReturnerStringType(element, selectorCover, attCover)

        return book
    }

    override fun latestUpdatesNextPageSelector(): String? = _latestNextPageSelector

    override fun latestUpdatesRequest(page: Int): Request {
        val url = fetchLatestUpdatesEndpoint()?.replace(pageFormat,
            page.toString()) ?: ""
        return GET("$baseUrl${
            getUrlWithoutDomain(url)
        }")
    }


    override fun bookDetailsParse(document: Document): Book {
        val book = Book.create()

        val selectorBookName = _nameDetailSelector
        val attBookName = _nameDetailAtt
        val selectorDescription = _descriptionDetailSelector
        val attDescription = _descriptionDetailBookAtt
        val selectorAuthor = _authorDetailBookSelector
        val attAuthor = _authorDetailBookAtt
        val selectorCategory = _categoryDetailSelector
        val attCategory = _categoryDetailAtt


        book.bookName = selectorReturnerStringType(document, selectorBookName, attAuthor)
        book.author = selectorReturnerStringType(document, selectorAuthor, attBookName)
        book.description = selectorReturnerListType(document, selectorDescription, attDescription)
        book.category = selectorReturnerListType(document, selectorCategory, attCategory)

        book.source = name
        return book
    }


    override fun hasNextChapterSelector() = _hasNextChapterListSelector

    override fun hasNextChaptersParse(document: Document): Boolean {
        if (!hasNextChapterSelector().isNullOrEmpty()) {
            return document.select(hasNextChapterSelector()!!).text()
                .contains("$_hasNextChapterListValue")
        } else {
            return false
        }
    }


    override fun chapterListRequest(book: Book, page: Int): Request {

        if (!fetchChaptersEndpoint().isNullOrEmpty()) {
            return  GET(baseUrl + getUrlWithoutDomain(fetchChaptersEndpoint() ?: ""))
        } else {
            return  GET(baseUrl + getUrlWithoutDomain(book.link))
        }
    }

    override fun chapterListSelector(): String? = _chapterListSelector

    override fun chapterFromElement(element: Element): Chapter {
        val chapter = Chapter.create()

        val selectorLink = _linkChapterSelector
        val attLink = _linkChapterAtt
        val selectorName = _nameChapterSelector
        val attName = _nameChapterAtt

        chapter.link = selectorReturnerStringType(element, selectorLink, attLink)
        chapter.title = selectorReturnerStringType(element, selectorName, attName)
        chapter.haveBeenRead = false


        return chapter
    }
    override fun chapterListParse(response: Response): ChaptersPage {
        val document = response.asJsoup()
        var chapters = document.select(chapterListSelector()).map { chapterFromElement(it) }
        val hasNext = hasNextChaptersParse(document)
        if (_isChapterStatsFromFirst) {
            chapters = chapters.reversed()
        }

        return ChaptersPage(chapters, hasNext)
    }




    override fun pageContentParse(document: Document): ChapterPage {

        val contentSelector = _chapterPageContentSelector
        val contentAtt = _chapterPageContentAtt
        val content: MutableList<String> = mutableListOf()
        val page = selectorReturnerListType(document, contentSelector, contentAtt)

        content.addAll(page)

        return ChapterPage(content)
    }


    override fun searchBookSelector(): String? = _searchBookSelector

    override fun searchBookFromElement(element: Element): Book {
        val book: Book = Book.create()
        val selectorLink = _linkSearchedSelector
        val attLink = _linkSearchedAtt
        val selectorName = _nameSearchedSelector
        val attName = _nameSearchedAtt
        val selectorCover = _coverSearchedSelector
        val attCover = _coverSearchedAtt

        book.link = selectorReturnerStringType(element, selectorLink, attLink)
        book.bookName = selectorReturnerStringType(element, selectorName, attName)
        book.coverLink = selectorReturnerStringType(element, selectorCover, attCover)

        return book
    }

    override fun searchBookRequest(page: Int, query: String): Request =
        GET("$baseUrl${
            getUrlWithoutDomain(fetchSearchBookEndpoint()?.replace(searchQueryFormat,
                query) ?: "")
        }")

    override fun searchBookNextPageSelector(): String? = _hasNextSearchedBooksNextPageSelector

    override fun searchBookNextValuePageSelector(): String? =
        _hasNextSearchedBookNextPageValue

    private fun selectorReturnerStringType(
        document: Document,
        selector: String? = null,
        att: String? = null,
    ): String {
        if (selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return document.attr(att)
        } else if (!selector.isNullOrEmpty() && att.isNullOrEmpty()) {
            return document.select(selector).text()
        } else if (!selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return document.select(selector).attr(att)
        } else {
            return ""
        }
    }

    private fun selectorReturnerStringType(
        element: Element,
        selector: String? = null,
        att: String? = null,
    ): String {
        if (selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return element.attr(att)
        } else if (!selector.isNullOrEmpty() && att.isNullOrEmpty()) {
            return element.select(selector).text()
        } else if (!selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return element.select(selector).attr(att)
        } else {
            return ""
        }
    }

    private fun selectorReturnerListType(
        element: Element,
        selector: String? = null,
        att: String? = null,
    ): List<String> {
        if (selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return listOf(element.attr(att))
        } else if (!selector.isNullOrEmpty() && att.isNullOrEmpty()) {
            return element.select(selector).eachText()
        } else if (!selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return listOf(element.select(selector).attr(att))
        } else {
            return emptyList()
        }
    }

    private fun selectorReturnerListType(
        document: Document,
        selector: String? = null,
        att: String? = null,
    ): List<String> {
        if (selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return listOf(document.attr(att))
        } else if (!selector.isNullOrEmpty() && att.isNullOrEmpty()) {
            return document.select(selector).eachText()
        } else if (!selector.isNullOrEmpty() && !att.isNullOrEmpty()) {
            return listOf(document.select(selector).attr(att))
        } else {
            return emptyList()
        }
    }
}