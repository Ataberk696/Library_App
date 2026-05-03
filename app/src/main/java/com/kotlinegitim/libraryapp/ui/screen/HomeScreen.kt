package com.kotlinegitim.libraryapp.ui.screen

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinegitim.libraryapp.data.model.Book
import com.kotlinegitim.libraryapp.ui.viewmodel.AuthViewModel
import com.kotlinegitim.libraryapp.ui.viewmodel.BookViewModel
import com.kotlinegitim.libraryapp.ui.viewmodel.BorrowResult
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.plus
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel
) {
    val profile by authViewModel.profile.collectAsState()
    val books by bookViewModel.books.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val borrowResult by bookViewModel.borrowResult.collectAsState()



    var showBorrowDialog by remember { mutableStateOf(false) }
    var selectedBookId by remember { mutableStateOf("") }
    var selectedBookTitle by remember { mutableStateOf("") }
    var selectedDays by remember { mutableIntStateOf(1) }

    LaunchedEffect(borrowResult) {
        if (borrowResult is BorrowResult.Success){
            showBorrowDialog = false
            bookViewModel.resetBorrowResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📚 Kütüphane Sistemi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (profile != null) {
                        Text(
                            text = "Merhaba, ${profile!!.fullName}",
                            modifier = Modifier.padding(end = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                books.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Kitaplar Yüklenmedi", fontSize = 16.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(books, key = { it.id }) { book ->
                            BookCard(book = book,
                                onBorrowClick = {
                                    selectedBookId = book.id
                                    selectedBookTitle = book.title
                                    selectedDays = 1
                                    showBorrowDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    if (showBorrowDialog) {
        AlertDialog(
            onDismissRequest = {
                showBorrowDialog = false
                bookViewModel.resetBorrowResult()
            },
            title = { Text("Ödünç Al: $selectedBookTitle") },
            text = {
                Column {
                    Text("Kaç gün sonra iade edeceksiniz? (1-5)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly) {
                        for (day in 1..5) {
                            Button(
                                onClick = { selectedDays = day },
                                modifier = Modifier.size(48.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (day == selectedDays)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text("$day",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    if (borrowResult is BorrowResult.Error) {
                        Text(
                            text = (borrowResult as BorrowResult.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val studentId = authViewModel.profile.value?.userId ?: return@Button
                        val today = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                        val due = today.plus(selectedDays.toLong(), DateTimeUnit.DAY)
                        bookViewModel.borrowBook(
                            bookId = selectedBookId,
                            studentId = studentId,
                            dueDate = due.toString()
                        )
                    },
                    enabled = borrowResult !is BorrowResult.Loading
                ) {
                    if (borrowResult is BorrowResult.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Onayla")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBorrowDialog = false
                    bookViewModel.resetBorrowResult()
                }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun BookCard(
    book: Book,
    onBorrowClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Yazar: ${book.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (book.category.isNotBlank()) {
                Text(
                    text = "Kategori: ${book.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = "Stok: ${book.avaiableCopies}",
                style = MaterialTheme.typography.bodySmall,
                color = if (book.avaiableCopies > 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (book.avaiableCopies > 0){
                Button(
                    onClick = onBorrowClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ödünç Al")
                }
            }
            else{
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                    )
                ) {
                    Text("STOKTA YOK", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}