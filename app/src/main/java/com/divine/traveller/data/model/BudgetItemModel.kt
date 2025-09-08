package com.divine.traveller.data.model

import com.divine.traveller.data.entity.BudgetItemCategory

data class BudgetItemModel(
    val id: Long,
    val tripId: Long,
    val category: BudgetItemCategory,
    val amount: Double,
    val currency: String,
    val notes: String?,
    val createdAt: Long,
    val payedBy: String?
)