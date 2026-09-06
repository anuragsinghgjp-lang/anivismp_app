@file:OptIn(ExperimentalMaterial3Api::class)

package com.anivi.smp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PasswordVisualTransformation
import androidx.compose.material3.VisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

const val SERVER_IP = "anivismp.aternos.me"
const val DISCORD_LINK = "https://discord.gg/ehF7HTTqkq"

data class ServerStatus(
    val online: Boolean = false,
    val playersOnline: Int = 0,
    val maxPlayers: Int = 0
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ANIVISMPApp()
        }
    }
}

@Composable
fun ANIVISMPApp() {

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500)
        isLoading = false
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7C4DFF),
            secondary = Color(0xFF00E676),
            background = Color(0xFF0B0D12),
            surface = Color(0xFF151821)
        )
    ) {
        if (isLoading) {
            LoadingScreen()
        } else {
            AuthGate()
        }
    }
}

@Composable
fun LoadingScreen() {

    var dots by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dots = if (dots >= 3) 1 else dots + 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0D12)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.anivi_logo
                ),
                contentDescription = "ANIVI SMP Logo",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "⚔ ANIVI SMP",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "YOUR MINECRAFT ADVENTURE",
                color = Color(0xFF9E9E9E),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            CircularProgressIndicator(
                color = Color(0
