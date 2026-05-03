package com.kotlinegitim.libraryapp.data.repository

import com.kotlinegitim.libraryapp.data.model.Book
import com.kotlinegitim.libraryapp.data.model.BorrowRecord
import com.kotlinegitim.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest


class BookRepository {
    suspend fun getAllBooks(): Result<List<Book>> = runCatching {
        supabase.postgrest["books"]
            .select()
            .decodeList<Book>()
    }

    suspend fun getBookById(id:String): Result<Book> = runCatching {
        supabase.postgrest["books"]
            .select { filter { eq("id",id) } }
            .decodeSingle<Book>()
    }

    suspend fun addBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].insert(book)
    }

    // Ödev2: BookRepository güncelleme, silme , arama fonksiyonlarını tanımla

    suspend fun updateBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"]
            .update(book) {
                filter { eq("id",book.id) }
            }
    }

    suspend fun deleteBook(bookId: String): Result<Unit> = runCatching {
        supabase.postgrest["books"]
            .delete {
                filter { eq("id",bookId) }
            }
    }

    suspend fun searchBook(query: String): Result<List<Book>> = runCatching {
        if (query.isBlank()) {
            getAllBooks().getOrThrow()
        } else {
            supabase.postgrest["books"]
                .select {
                    filter {
                        or {
                            ilike("title", "%$query%")
                            ilike("author", "%$query%")
                        }
                    }
                }
                .decodeList<Book>()
        }
    }

    suspend fun borrowBook(bookId: String,studentId: String, dueDate: String): Result<Unit> = runCatching {
        val book = getBookById(bookId).getOrThrow()
        if (book.avaiableCopies <= 0){
            error("Kitap stokta bulunmamaktadır.")
        }

        val borrowRecord = BorrowRecord(
            id = null,
            studentId = studentId,
            bookId = bookId,
            borrowedAt = "",
            dueDate = dueDate,
        )
        supabase.postgrest["borrow_records"].insert(borrowRecord)

        val updatedBook = book.copy(avaiableCopies = book.avaiableCopies - 1)
        supabase.postgrest["books"].update(updatedBook) {
            filter { eq("id",bookId) }
        }
    }




}