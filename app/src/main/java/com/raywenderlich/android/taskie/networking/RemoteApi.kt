package com.raywenderlich.android.taskie.networking

import com.raywenderlich.android.taskie.model.Result
import com.raywenderlich.android.taskie.model.Task
import com.raywenderlich.android.taskie.model.UserProfile
import com.raywenderlich.android.taskie.model.request.AddTaskRequest
import com.raywenderlich.android.taskie.model.request.UserDataRequest

interface RemoteApi {

  fun loginUser(userDataRequest: UserDataRequest, onUserLoggedIn: (Result<String>) -> Unit)

  fun registerUser(userDataRequest: UserDataRequest, onUserCreated: (Result<String>) -> Unit)

  suspend fun getTasks(): Result<List<Task>>

  suspend fun deleteTask(taskId: String): Result<String>

  suspend fun completeTask(taskId: String): Result<String>

  suspend fun addTask(addTaskRequest: AddTaskRequest): Result<Task>

  suspend fun getUserProfile(): Result<UserProfile>
}
