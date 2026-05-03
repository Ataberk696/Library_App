package com.kotlinegitim.libraryapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BorrowRecord(
    val id: String? = null,
    @SerialName("student_id") val studentId: String,
    @SerialName("book_id") val bookId: String,
    @SerialName("borrowed_at") val borrowedAt: String = "", // Date?
    @SerialName("due_date") val dueDate: String = "",
    @SerialName("returned_at") val returnedAt: String? = null
) {
}