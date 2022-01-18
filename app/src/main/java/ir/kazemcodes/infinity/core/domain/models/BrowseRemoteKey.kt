package ir.kazemcodes.infinity.core.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.kazemcodes.infinity.feature_detail.presentation.book_detail.Constants.ImageKeyTable

@Entity(tableName = ImageKeyTable)
data class BrowseRemoteKey(
   @PrimaryKey(autoGenerate = false)
   var id : String,
   val previousPage : Int?,
   var nextPage : Int?,
   var lastUpdated: Long?
)