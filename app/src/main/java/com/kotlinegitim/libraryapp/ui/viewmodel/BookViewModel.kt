package com.kotlinegitim.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinegitim.libraryapp.data.model.Book
import com.kotlinegitim.libraryapp.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class BorrowResult {
    object Idle : BorrowResult()
    object Loading : BorrowResult()
    object Success : BorrowResult()
    data class Error(val message: String) : BorrowResult()
}

class BookViewModel : ViewModel() {
    private val repository = BookRepository()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _borrowResult = MutableStateFlow<BorrowResult>(BorrowResult.Idle)
    val borrowResult: StateFlow<BorrowResult> = _borrowResult

    init {
        loadBooks()
    }


    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            repository
                .getAllBooks()
                .onSuccess { _books.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun addBook(book: Book){
        viewModelScope.launch {
            _isLoading.value = true
            repository.addBook(book)
                .onSuccess { loadBooks() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateBook(book: Book){
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBook(book)
                .onSuccess {
                    loadBooks()
                }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteBook(bookId)
                .onSuccess { loadBooks() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun searchBooks(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _isLoading.value = true
            repository.searchBook(query)
                .onSuccess { _books.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun borrowBook(bookId: String, studentId: String, dueDate: String) {
        viewModelScope.launch {
            _borrowResult.value = BorrowResult.Loading
            repository.borrowBook(bookId,studentId,dueDate)
                .onSuccess {
                    _borrowResult.value = BorrowResult.Success
                    loadBooks()
                }
                .onFailure { e ->
                    _borrowResult.value = BorrowResult.Error(e.message ?: "Hata Oluştu")
                }
        }
    }

    fun resetBorrowResult() {
        _borrowResult.value = BorrowResult.Idle
    }
}