package com.divine.traveller.util

import com.divine.traveller.data.entity.ItineraryItemEntity

object ItineraryOrderHelper {
    private const val ORDER_GAP = 1000L
    private const val INITIAL_ORDER = ORDER_GAP

    // Build a map of id -> newOrder from an explicit ordered id list.
    // If existingIds is provided, any ids not present will be ignored.
    fun computeOrderMap(orderedIds: List<Long>, existingIds: Set<Long>? = null): Map<Long, Long> {
        val result = mutableMapOf<Long, Long>()
        var current = INITIAL_ORDER
        val filtered =
            if (existingIds != null) orderedIds.filter { it in existingIds } else orderedIds
        for (id in filtered) {
            result[id] = current
            current += ORDER_GAP
        }
        return result
    }

    // Reindex a sorted list of items with uniform gaps (useful to recover when gaps exhausted)
    fun reindexItems(itemsSorted: List<ItineraryItemEntity>): Map<Long, Long> {
        var current = INITIAL_ORDER
        val result = mutableMapOf<Long, Long>()
        for (item in itemsSorted) {
            result[item.id] = current
            current += ORDER_GAP
        }
        return result
    }
}