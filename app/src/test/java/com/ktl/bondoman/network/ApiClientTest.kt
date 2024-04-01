package com.ktl.bondoman.network

import com.ktl.bondoman.network.requests.BillUploadRequest
import com.ktl.bondoman.network.requests.LoginRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiClientTest {

    @Test
    fun `test login`() = runBlocking {
        val apiClient = ApiClient

        val response = apiClient.apiService.login(LoginRequest("13521135@std.stei.itb.ac.id", "password_13521135"))
        assertEquals(200, response.code())
        val responseBody = response.body()
        assert(responseBody != null)
    }

    @Test
    fun `test token expiration`() = runBlocking {
        val apiClient = ApiClient

        val response = apiClient.apiService.checkTokenExpiration("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTEzNSIsImlhdCI6MTcwOTkxNzYwOSwiZXhwIjoxNzA5OTE3OTA5fQ.VrURIcGhI6PyEMwa0Q3RuDv39epgVo5kP01Gc4-VfUM")
        assertEquals(401, response.code())
    }

    @Test
    fun `test bill upload`() = runBlocking {
        val apiClient = ApiClient
        val cwd = System.getProperty("user.dir")

        val filePath = "$cwd/src/test/java/com/ktl/bondoman/network/fixtures/Cat03.png"
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTEzNSIsImlhdCI6MTcxMTk2NTc2MywiZXhwIjoxNzExOTY2MDYzfQ.nk3VsPKRXtV1JpbC1LSVsmNryhex4qyE_kVMV0mwO3Y"
        val response = apiClient.apiService.uploadBill("Bearer $token", BillUploadRequest(filePath).toMultipartBodyPart())
        assertEquals(401, response.code())
    }
}
