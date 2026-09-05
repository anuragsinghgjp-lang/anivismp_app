package com.anivi.smp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp


const val SERVER_IP = "anivismp.aternos.me"

const val DISCORD_LINK =
    "https://discord.gg/ehF7HTTqk"


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

    var selectedTab by remember {

        mutableIntStateOf(0)
    }


    val tabs = listOf(

        "Home",

        "Server",

        "Shop",

        "Rules"
    )


    val icons = listOf(

        Icons.Default.Home,

        Icons.Default.SportsEsports,

        Icons.Default.ShoppingCart,

        Icons.Default.MenuBook
    )


    MaterialTheme(

        colorScheme = darkColorScheme(

            primary = Color(0xFF7C4DFF),

            secondary = Color(0xFF00E676),

            background = Color(0xFF0B0D12)
        )

    ) {


        Scaffold(

            topBar = {

                CenterAlignedTopAppBar(

                    title = {

                        Text(

                            text = "⚔ ANIVI SMP",

                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            },


            bottomBar = {

                NavigationBar {

                    tabs.forEachIndexed { index, name ->

                        NavigationBarItem(

                            selected =
                                selectedTab == index,

                            onClick = {

                                selectedTab = index
                            },

                            icon = {

                                Icon(

                                    imageVector = icons[index],

                                    contentDescription = name
                                )
                            },

                            label = {

                                Text(name)
                            }
                        )
                    }
                }
            }

        ) { paddingValues ->


            Box(

                modifier = Modifier

                    .fillMaxSize()

                    .padding(paddingValues)
            ) {


                when (selectedTab) {

                    0 -> HomeScreen()

                    1 -> ServerScreen()

                    2 -> ShopScreen()

                    3 -> RulesScreen()
                }
            }
        }
    }
}


@Composable
fun HomeScreen() {

    val context = LocalContext.current


    LazyColumn(

        modifier = Modifier

            .fillMaxSize()

            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {


        item {

            Card(

                modifier =
                    Modifier.fillMaxWidth()
            ) {


                Column(

                    modifier = Modifier

                        .padding(24.dp)

                        .fillMaxWidth(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {


                    Text(

                        text = "⚔ ANIVI SMP",

                        style =
                            MaterialTheme.typography.headlineLarge,

                        fontWeight =
                            FontWeight.ExtraBold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    Text(

                        text =
                            "Your Minecraft adventure starts here!"
                    )


                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )


                    Button(

                        onClick = {

                            copyIP(context)
                        }
                    ) {

                        Text(
                            "📋 COPY SERVER IP"
                        )
                    }
                }
            }
        }


        item {

            InfoCard(

                title = "🎮 SERVER",

                value = SERVER_IP,

                subtitle =
                    "Minecraft Server"
            )
        }


        item {

            Button(

                modifier =
                    Modifier.fillMaxWidth(),

                onClick = {

                    openLink(
                        context,
                        DISCORD_LINK
                    )
                }
            ) {

                Text(
                    "💬 JOIN DISCORD"
                )
            }
        }


        item {

            OutlinedButton(

                modifier =
                    Modifier.fillMaxWidth(),

                onClick = {

                    Toast.makeText(

                        context,

                        "Shop coming soon!",

                        Toast.LENGTH_SHORT

                    ).show()
                }
            ) {

                Text(
                    "🛒 OPEN SHOP"
                )
            }
        }
    }
}


@Composable
fun ServerScreen() {

    LazyColumn(

        modifier = Modifier

            .fillMaxSize()

            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {


        item {

            Text(

                text = "🎮 Server",

                style =
                    MaterialTheme.typography.headlineMedium,

                fontWeight =
                    FontWeight.Bold
            )
        }


        item {

            InfoCard(

                "Server IP",

                SERVER_IP,

                "Use this address in Minecraft"
            )
        }


        item {

            InfoCard(

                "Mode",

                "ANIVI SMP",

                "Survival • Lifesteal"
            )
        }


        item {

            InfoCard(

                "Status",

                "Coming Soon",

                "Live status will be added later"
            )
        }
    }
}


@Composable
fun ShopScreen() {

    LazyColumn(

        modifier = Modifier

            .fillMaxSize()

            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {


        item {

            Text(

                text = "🛒 ANIVI SHOP",

                style =
                    MaterialTheme.typography.headlineMedium,

                fontWeight =
                    FontWeight.Bold
            )
        }


        item {

            InfoCard(

                "⭐ Custom Ranks",

                "Coming Soon",

                "Buy ranks using in-game money"
            )
        }


        item {

            InfoCard(

                "💎 Items",

                "Coming Soon",

                "Special items and rewards"
            )
        }


        item {

            InfoCard(

                "🎁 Crates",

                "Coming Soon",

                "Special rewards"
            )
        }
    }
}


@Composable
fun RulesScreen() {

    val rules = listOf(

        "Respect all players and staff.",

        "No cheating or unfair advantages.",

        "Do not spam the chat.",

        "Do not grief other players.",

        "Do not abuse bugs or exploits.",

        "Follow staff instructions.",

        "Have fun and enjoy ANIVI SMP!"
    )


    LazyColumn(

        modifier = Modifier

            .fillMaxSize()

            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {


        item {

            Text(

                text = "📜 SERVER RULES",

                style =
                    MaterialTheme.typography.headlineMedium,

                fontWeight =
                    FontWeight.Bold
            )
        }


        items(rules.size) { index ->


            Card(

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(

                    text =
                        "• ${rules[index]}",

                    modifier =
                        Modifier.padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun InfoCard(

    title: String,

    value: String,

    subtitle: String

) {

    Card(

        modifier =
            Modifier.fillMaxWidth()
    ) {


        Column(

            modifier =
                Modifier.padding(18.dp)
        ) {


            Text(
                text = title,

                style =
                    MaterialTheme.typography.labelLarge
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(

                text = value,

                style =
                    MaterialTheme.typography.titleLarge,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(
                text = subtitle
            )
        }
    }
}


fun copyIP(context: Context) {

    val clipboard =

        context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager


    clipboard.setPrimaryClip(

        ClipData.newPlainText(

            "ANIVI SMP IP",

            SERVER_IP
        )
    )


    Toast.makeText(

        context,

        "Server IP copied!",

        Toast.LENGTH_SHORT

    ).show()
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
