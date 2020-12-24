package com.dimi.advnotes.framework.database.mapper

import com.dimi.advnotes.domain.model.CheckItem
import com.dimi.advnotes.domain.util.EntityMapper
import com.dimi.advnotes.framework.database.model.CheckItemCacheEntity
import com.dimi.advnotes.util.Constants.INVALID_PRIMARY_KEY
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckItemCacheMapper @Inject constructor() : EntityMapper<CheckItemCacheEntity, CheckItem> {

    fun mapFromEntityList(domainModels: List<CheckItemCacheEntity>) =
        domainModels.map { domainModel ->
            mapFromEntity(domainModel)
        }

    fun mapToEntityList(
        domainModels: List<CheckItem>,
        noteId: Long = INVALID_PRIMARY_KEY,
        lastUpdate: Long? = null
    ) = domainModels.mapIndexed { index, checkItem ->
            mapToEntity(checkItem).apply {
                order = index
                if (noteId != INVALID_PRIMARY_KEY) this.noteOwnerId = noteId
                lastUpdate?.let {
                    this.lastUpdated = it
                }
            }
        }

    override fun mapFromEntity(entity: CheckItemCacheEntity): CheckItem {
        return CheckItem(
            id = entity.id,
            text = entity.body,
            checked = entity.checked,
            lastUpdated = entity.lastUpdated
        )
    }

    override fun mapToEntity(domainModel: CheckItem): CheckItemCacheEntity {
        return CheckItemCacheEntity(
            id = domainModel.id,
            body = domainModel.text,
            checked = domainModel.checked,
            lastUpdated = domainModel.lastUpdated
        )
    }
}