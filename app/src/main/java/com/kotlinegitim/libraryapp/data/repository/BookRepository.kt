package com.kotlinegitim.libraryapp.data.repository

import com.kotlinegitim.libraryapp.data.model.Book
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
}