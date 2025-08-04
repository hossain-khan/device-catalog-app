package dev.hossain.devicecatalog.model

import androidx.room.ColumnInfo

data class MinMaxRange(
    @ColumnInfo(name = "min") val min: Int?,
    @ColumnInfo(name = "max") val max: Int?,
)
