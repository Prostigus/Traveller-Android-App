package com.divine.traveller.data.viewmodel

import com.divine.traveller.data.repository.HotelRepository
import javax.inject.Inject

class HotelViewModel @Inject constructor(
    private val repository: HotelRepository
) {

}