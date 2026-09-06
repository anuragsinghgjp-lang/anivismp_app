@file:OptIn(ExperimentalMaterial3Api::class)

package com.anivi.smp

import androidx.compose.runtime.Composable
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
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

    if (isLoading) {
        LoadingScreen()
    } else {
        MainAppScreen()
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
                color = Color(0xFF7C4DFF),
                modifier = Modifier.size(35.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "LOADING" + ".".repeat(dots),
                color = Color(0xFF7C4DFF),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MainAppScreen() {

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        "Home",
        "Server",
        "Shop",
        "Rules"
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7C4DFF),
            secondary = Color(0xFF00E676),
            background = Color(0xFF0B0D12),
            surface = Color(0xFF151821)
        )
    ) {

        Scaffold(
            bottomBar = {

                NavigationBar(
                    containerColor = Color(0xFF11131A)
                ) {

                    tabs.forEachIndexed { index, title ->

                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                            },
                            icon = {
                                Text(
                                    when (index) {
                                        0 -> "⌂"
                                        1 -> "⚔"
                                        2 -> "🛒"
                                        else -> "📜"
                                    }
                                )
                            },
                            label = {
                                Text(title)
                            }
                        )
                    }
                }
            }
        ) { padding ->

            when (selectedTab) {

                0 -> HomeScreen(
                    Modifier.padding(padding)
                )

                1 -> ServerScreen(
                    Modifier.padding(padding)
                )

                2 -> ShopScreen(
                    Modifier.padding(padding)
                )

                3 -> RulesScreen(
                    Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var serverStatus by remember {
        mutableStateOf(ServerStatus())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        serverStatus = getServerStatus()
        loading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0D12))
            .padding(20.dp)
    ) {

        Text(
            text = "⚔ ANIVI SMP",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Minecraft Survival • Lifesteal",
            color = Color(0xFF9E9E9E)
        )

        Spacer(modifier = Modifier.height(20.dp))

        ServerStatusCard(
            status = serverStatus,
            loading = loading,
            onRefresh = {
                loading = true
            }
        )

        LaunchedEffect(loading) {
            if (loading) {
                serverStatus = getServerStatus()
                loading = false
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF151821)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "🎮 Server IP",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = SERVER_IP,
                        color = Color(0xFF00E676),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            val clipboard =
                                context.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager

                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "Server IP",
                                    SERVER_IP
                                )
                            )
                        }
                    ) {

                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                openLink(context, DISCORD_LINK)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5865F2)
            )
        ) {

            Text("Join Discord")
        }
    }
}

@Composable
fun ServerStatusCard(
    status: ServerStatus,
    loading: Boolean,
    onRefresh: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151821)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = Color(0xFF7C4DFF)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "LIVE SERVER STATUS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onRefresh
                ) {

                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (loading) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 3.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Checking server...",
                        color = Color(0xFFBDBDBD)
                    )
                }

            } else {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (status.online)
                                    Color(0xFF00E676)
                                else
                                    Color(0xFFFF1744)
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (status.online)
                            "ONLINE"
                        else
                            "OFFLINE",
                        color = if (status.online)
                            Color(0xFF00E676)
                        else
                            Color(0xFFFF1744),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (status.online)
                        "👥 ${status.playersOnline}/${status.maxPlayers} players online"
                    else
                        "Server is currently offline",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }
    }
}

suspend fun getServerStatus(): ServerStatus {

    return withContext(Dispatchers.IO) {

        try {

            val client = OkHttpClient()

            val request = Request.Builder()
                .url(
                    "https://api.mcsrvstat.us/3/$SERVER_IP"
                )
                .header(
                    "User-Agent",
                    "ANIVI-SMP-App"
                )
                .build()

            client.newCall(request).execute().use { response ->

                if (!response.isSuccessful) {
                    return@withContext ServerStatus()
                }

                val body = response.body?.string()
                    ?: return@withContext ServerStatus()

                val json = JSONObject(body)

                val online = json.optBoolean(
                    "online",
                    false
                )

                val players =
                    json.optJSONObject("players")

                val onlinePlayers =
                    players?.optInt("online", 0) ?: 0

                val maxPlayers =
                    players?.optInt("max", 0) ?: 0

                ServerStatus(
                    online = online,
                    playersOnline = onlinePlayers,
                    maxPlayers = maxPlayers
                )
            }

        } catch (e: Exception) {

            ServerStatus()
        }
    }
}

@Composable
fun ShopScreen(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0D12))
            .padding(20.dp)
    ) {

        Text(
            "🛒 ANIVI SMP SHOP",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoCard(
            "👑 Custom Ranks",
            "Coming Soon"
        )

        InfoCard(
            "💎 Special Items",
            "Coming Soon"
        )

        InfoCard(
            "🎁 Crates",
            "Coming Soon"
        )
    }
}

@Composable
fun ServerScreen(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0D12))
            .padding(20.dp)
    ) {

        Text(
            "⚔ SERVER",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoCard(
            "🌐 Server IP",
            SERVER_IP
        )

        InfoCard(
            "🎮 Mode",
            "Survival • Lifesteal"
        )

        InfoCard(
            "🟢 Status",
            "Live status available on Home"
        )
    }
}

@Composable
fun RulesScreen(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0D12))
            .padding(20.dp)
    ) {

        Text(
            "📜 SERVER RULES",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        val rules = listOf(
            "No hacking or unfair advantages.",
            "No exploiting server bugs.",
            "Respect all players and staff.",
            "No spam or excessive advertising.",
            "Do not steal from protected areas.",
            "Do not intentionally crash or lag the server.",
            "Follow staff instructions."
        )

        rules.forEachIndexed { index, rule ->

            InfoCard(
                "${index + 1}. Rule",
                rule
            )
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151821)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                value,
                color = Color(0xFFBDBDBD)
            )
        }
    }
}

fun openLink(
    context: Context,
    url: String
) {

    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(url)
    )

    context.startActivity(intent)
}
