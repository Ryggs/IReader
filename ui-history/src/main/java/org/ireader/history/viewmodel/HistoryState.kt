package org.ireader.history.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.ireader.common_models.entities.HistoryWithRelations
import javax.inject.Inject
import javax.inject.Singleton

interface HistoryState {
    val isLoading: Boolean
    val isEmpty: Boolean

    var searchMode: Boolean
    var searchQuery: String
    var history: Map<String, List<HistoryWithRelations>>
}

@Singleton
open class HistoryStateImpl @Inject constructor() : HistoryState {
    override var isLoading: Boolean by mutableStateOf(false)
    override val isEmpty: Boolean by derivedStateOf { history.isEmpty() }
    override var searchMode by mutableStateOf<Boolean>(false)
    override var searchQuery by mutableStateOf<String>("")
    override var history: Map<String, List<HistoryWithRelations>> by mutableStateOf(emptyMap())
}
