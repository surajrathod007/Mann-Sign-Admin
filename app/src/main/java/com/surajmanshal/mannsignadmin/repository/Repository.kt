package com.surajmanshal.mannsignadmin.repository

import com.surajmanshal.mannsignadmin.network.NetworkService

class Repository(private val networkService: NetworkService) {
    fun fetchMaterials() = networkService.networkInstance.fetchMaterials()
}