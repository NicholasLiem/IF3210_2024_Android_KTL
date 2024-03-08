package com.ktl.bondoman.network

import com.ktl.bondoman.network.requests.LoginRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiClientTest {

    @Test
    fun `test login`() {
        val apiClient = ApiClient

        val response = apiClient.apiService.login(LoginRequest("13521135@std.stei.itb.ac.id", "password_13521135")).execute()
        assertEquals(200, response.code())
        val responseBody = response.body()
        assert(responseBody != null)
    }

    @Test
    fun `test token expiration`() {
        val apiClient = ApiClient

        val response = apiClient.apiService.checkTokenExpiration("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTEzNSIsImlhdCI6MTcwOTkxNzYwOSwiZXhwIjoxNzA5OTE3OTA5fQ.VrURIcGhI6PyEMwa0Q3RuDv39epgVo5kP01Gc4-VfUM").execute()
        assertEquals(401, response.code())
    }
}
