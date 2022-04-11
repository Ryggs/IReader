package org.ireader.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import org.ireader.core.utils.convertLongToTime
import org.ireader.data.local.dao.UpdatesDao
import org.ireader.domain.models.entities.Update
import org.ireader.domain.models.entities.UpdateWithInfo
import org.ireader.domain.repository.UpdatesRepository
import javax.inject.Inject

class UpdatesRepositoryImpl @Inject constructor(private val updatesDao: UpdatesDao) :
    UpdatesRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun subscribeAllUpdates(): Flow<Map<String, List<UpdateWithInfo>>> {
        return updatesDao.subscribeUpdates().mapLatest { updates ->
            updates.groupBy { update -> convertLongToTime(update.date, "yyyy/MM/dd") }
        }.distinctUntilChanged()
    }

    override suspend fun insertUpdates(update: List<Update>) {
         updatesDao.insert(update)
    }

    override suspend fun insertUpdate(update: Update) {
        updatesDao.insert(update)
    }

    override suspend fun deleteUpdate(update: Update) {
        updatesDao.delete(update)
    }

    override suspend fun deleteUpdates(update: List<Update>) {
        updatesDao.delete(update)

    }

    override suspend fun deleteAllUpdates() {
        updatesDao.deleteAllUpdates()

    }
}