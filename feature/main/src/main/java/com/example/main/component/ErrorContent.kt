package com.example.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.GrensilVideoListTheme

@Composable
fun ErrorContent(message: String, onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error: $message",
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetryClick) {
                Text("Retry")
            }
        }
    }
}

@Preview(showBackground = true, name = "ErrorContent - Network Error")
@Composable
fun PreviewErrorContent() {
    GrensilVideoListTheme {
        ErrorContent(
            message = "Network connection failed",
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true, name = "ErrorContent - Server Error")
@Composable
fun PreviewErrorContentServer() {
    GrensilVideoListTheme {
        ErrorContent(
            message = "Server error 500",
            onRetryClick = {}
        )
    }
}
