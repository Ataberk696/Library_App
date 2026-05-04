package com.kotlinegitim.libraryapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinegitim.libraryapp.data.repository.BorrowRepository
import com.kotlinegitim.libraryapp.ui.viewmodel.AuthViewModel
import com.kotlinegitim.libraryapp.ui.viewmodel.BorrowViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBorrowsScreen(
    authViewModel: AuthViewModel,
    borrowViewModel: BorrowViewModel,
    onNavigateBack : () -> Unit
){
    val profile by authViewModel.profile.collectAsState()
    val uiState by borrowViewModel.state.collectAsState()

    LaunchedEffect(profile) {
        profile?.let {
            borrowViewModel.loadBorrows(it.userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title =  { Text("Kiraladığım Kitaplar") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("<- Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when{
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Hata: ${uiState.error!!}", color = MaterialTheme.colorScheme.error)
                    }
                }
                uiState.borrows.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Hiç kiralama kaydınız yok.")
                    }
                } else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.borrows) { item ->
                        BorrowCard(item)
                    }
                }
                }

            }

        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun BorrowCard(item: BorrowRepository.BorrowWithBookTitle){
    val borrow = item.borrow
    val isReturned = borrow.returnedAt != null
    val isOverdue = !isReturned && runCatching {
        val dueDate = LocalDate.parse(borrow.dueDate.substring(0, 10))
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        dueDate < today
    }.getOrDefault(false)
    val statusText = when {
        isReturned -> "İade Edildi"
        isOverdue -> "Gecikmiş!"
        else -> "Aktif"
    }
    val statusColor = when {
        isReturned -> Color.Gray
        isOverdue -> Color.Red
        else -> Color.Green
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.bookTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Alış Tarihi: ${borrow.borrowedAt.take(10)} • İade Tarihi: ${borrow.dueDate.take(10)}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Durum: $statusText",
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
            if (isOverdue) {
                Text(
                    "Gecikme süresi: ${borrow.dueDate.take(10)}",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }

}