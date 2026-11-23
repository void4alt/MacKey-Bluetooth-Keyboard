package com.mackey.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.bluetooth.ConnectionState
import com.mackey.app.ui.components.ConnectionBadge
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.ui.theme.MacGreen
import com.mackey.app.viewmodel.KeyboardViewModel

@Composable
fun TextInputScreen(
    keyboardViewModel: KeyboardViewModel,
    onNavigateToConnection: () -> Unit
) {
    val connectionState by keyboardViewModel.connectionState.collectAsState()
    val isSending by keyboardViewModel.isSendingText.collectAsState()
    val progress by keyboardViewModel.sendProgress.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ConnectionBadge(
            connectionState = connectionState,
            onClick = onNavigateToConnection
        )

        // Title and description
        Column {
            Text(
                text = "Text Input Mode",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Type or paste longer text using your phone's native keyboard, swipe, or voice typing, then send it directly to your Mac.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Multi-line Text Area
        OutlinedTextField(
            value = textInput,
            onValueChange = {
                textInput = it
                statusMessage = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(
                    text = "Type or paste text here… (e.g. paragraphs, URLs, code snippets, notes)",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(10.dp),
            enabled = !isSending
        )

        // Character counter and Clear button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${textInput.length} characters • ${textInput.split(Regex("\\s+")).filter { it.isNotBlank() }.size} words",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (textInput.isNotEmpty() && !isSending) {
                OutlinedButton(
                    onClick = {
                        textInput = ""
                        statusMessage = null
                    },
                    modifier = Modifier.height(30.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Clear", fontSize = 11.sp)
                }
            }
        }

        // Progress Indicator while sending
        if (isSending) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MacBlueAccent
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Streaming to Mac: ${(progress * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = MacBlueAccent
                    )
                    OutlinedButton(
                        onClick = { keyboardViewModel.cancelTextStream() },
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Cancel", fontSize = 11.sp)
                    }
                }
            }
        }

        if (statusMessage != null) {
            Text(
                text = statusMessage ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MacGreen
            )
        }

        // Send Button
        Button(
            onClick = {
                if (connectionState !is ConnectionState.Connected) {
                    statusMessage = "Cannot send: Mac is not connected."
                    return@Button
                }

                keyboardViewModel.sendBatchText(textInput) { success ->
                    if (success) {
                        statusMessage = "Sent successfully to your Mac!"
                    } else {
                        statusMessage = "Transmission interrupted."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MacBlueAccent),
            enabled = textInput.isNotEmpty() && !isSending
        ) {
            Text(
                text = if (isSending) "Sending to Mac…" else "Send to Mac ⏎",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Zero-storage privacy guarantee notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Privacy Protected: Typed text is never saved to disk or logged. Keystrokes are converted in memory and transmitted directly over Bluetooth.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

