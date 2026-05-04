package com.kotlinegitim.libraryapp.data.repository

import com.kotlinegitim.libraryapp.data.model.BorrowRecord
import com.kotlinegitim.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.coroutineScope

class BorrowRepository {

    private val bookRepository = BookRepository()

    data class BorrowWithBookTitle(
        val borrow: BorrowRecord,
        val bookTitle: String
    )

    suspend fun getStudentBorrows(studentId: String): Result<List<BorrowWithBookTitle>> = runCatching {
        val records = supabase.postgrest["borrow_records"]
            .select {
                filter { eq("student_id", studentId) }
            }
            .decodeList<BorrowRecord>()


        coroutineScope {
            records.map { borrow ->
                // kitabı id ile alıyoruz
                val book = bookRepository.getBookById(borrow.bookId).getOrNull()
                val title = book?.title ?: "Bilinmeyen Kitap"
                BorrowWithBookTitle(borrow, title)
            }
        }
    }
}