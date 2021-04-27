/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.taskie.networking

import com.raywenderlich.android.taskie.model.*
import com.raywenderlich.android.taskie.model.request.AddTaskRequest
import com.raywenderlich.android.taskie.model.request.UserDataRequest
import com.raywenderlich.android.taskie.model.response.LoginResponse
import com.raywenderlich.android.taskie.model.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Holds decoupled logic for all the API calls.
 */

class RemoteApiImpl @Inject constructor(private val apiService: RemoteApiService) : RemoteApi {

    override fun loginUser(userDataRequest: UserDataRequest, onUserLoggedIn: (Result<String>) -> Unit) {
        apiService.loginUser(userDataRequest).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, error: Throwable) {
                onUserLoggedIn(Failure(error))
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()

                if (loginResponse == null || loginResponse.token.isNullOrEmpty()) {
                    onUserLoggedIn(Failure(NullPointerException("No response body!")))
                } else {
                    onUserLoggedIn(Success(loginResponse.token))
                }
            }
        })
    }

    override fun registerUser(userDataRequest: UserDataRequest, onUserCreated: (Result<String>) -> Unit) {
        apiService.registerUser(userDataRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onFailure(call: Call<RegisterResponse>, error: Throwable) {
                onUserCreated(Failure(error))
            }

            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                val message = response.body()?.message
                if (message == null) {
                    onUserCreated(Failure(NullPointerException("No response body!")))
                    return
                }

                onUserCreated(Success(message))
            }
        })
    }

    override suspend fun getTasks(): Result<List<Task>> = try {
        val data = apiService.getNotes().notes

        Success(data.filter { !it.isCompleted })
    } catch (error: Throwable) {
        Failure(error)
    }

    override suspend fun deleteTask(taskId: String) = try {
        val data = apiService.deleteNote(taskId)

        Success(data.message)
    } catch (error: Throwable) {
        Failure(error)
    }

    override suspend fun completeTask(taskId: String): Result<String> = try {
        val response = apiService.completeTask(taskId)

        Success(response.message!!)
    } catch (error: Throwable) {
        Failure(error)
    }

    override suspend fun addTask(addTaskRequest: AddTaskRequest): Result<Task> = try {
        val task = apiService.addTask(addTaskRequest)

        Success(task)
    } catch (error: Throwable) {
        Failure(error)
    }


    override suspend fun getUserProfile(): Result<UserProfile> = try {
        val notesResult = getTasks()

        if (notesResult is Failure) {
            Failure(notesResult.error)
        } else {
            val notes = notesResult as Success
            val data = apiService.getMyProfile()

            if (data.name == null || data.email == null) {
                Failure(NullPointerException("No data available!"))
            } else {
                Success(UserProfile(data.email, data.name, notes.data.size))
            }
        }
    } catch (error: Throwable) {
        Failure(error)
    }
}