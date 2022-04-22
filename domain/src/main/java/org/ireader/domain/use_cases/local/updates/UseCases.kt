package org.ireader.domain.use_cases.local.updates

import kotlinx.coroutines.flow.Flow
import org.ireader.domain.models.entities.Update
import org.ireader.domain.models.entities.UpdateWithInfo
import org.ireader.domain.repository.UpdatesRepository
import org.ireader.domain.utils.withIOContext
import javax.inject.Inject

class SubscribeUpdates @Inject constructor(private val updatesRepository: UpdatesRepository) {
    operator fun invoke(): Flow<Map<String, List<UpdateWithInfo>>> {
        return updatesRepository.subscribeAllUpdates()
    }
}

class InsertUpdatesUseCase @Inject constructor(private val updatesRepository: UpdatesRepository) {
    suspend operator fun invoke(updates: List<Update>) {
        return withIOContext {
            return@withIOContext updatesRepository.insertUpdates(updates)
        }
    }
}

class InsertUpdateUseCase @Inject constructor(private val updatesRepository: UpdatesRepository) {
    suspend operator fun invoke(updates: Update) {
        return withIOContext {
            return@withIOContext updatesRepository.insertUpdate(updates)
        }
    }
}