package com.kotlinegitim.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinegitim.libraryapp.data.repository.BorrowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class BorrowUiState(
    val isLoading: Boolean = false,
    val borrows: List<BorrowRepository.BorrowWithBookTitle> = emptyList(),
    val error: String? = null
)

class BorrowViewModel: ViewModel() {
    private val repository = BorrowRepository()
    private val _state = MutableStateFlow(BorrowUiState())
    val state: StateFlow<BorrowUiState> = _state

    fun loadBorrows(studentId: String){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getStudentBorrows(studentId)
                .onSuccess { list ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        borrows = list
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
        }
    }
}