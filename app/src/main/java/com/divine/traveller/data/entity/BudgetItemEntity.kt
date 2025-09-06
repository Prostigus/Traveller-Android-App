package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_items")
data class BudgetItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val category: BudgetItemCategory,
    val amount: Double,
    val currency: String = "USD",
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val payedBy: String? = null,
)

enum class BudgetItemCategory {
    TRANSPORT,
    ACCOMMODATION,
    FOOD,
    ACTIVITIES,
    MISCELLANEOUS
}