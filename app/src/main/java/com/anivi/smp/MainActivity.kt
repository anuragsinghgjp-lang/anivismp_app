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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

// Dark Premium Purple Palette
private val PrimaryPurple = Color(0xFF9C27B0)
private val DarkBackground = Color(0xFF121212)
private val CardBackground = Color(0xFF1E1E1E)
private val SurfaceDark = Color(0xFF252525)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGray = Color(0xFFA0A0A0)
private val StatusGreen = Color(0xFF4CAF50)
private val StatusRed = Color(0xFFF44336)

class MainActivity : ComponentActivity() {
    private val authManager by lazy { AuthManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ANIVISMPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    var isLoggedIn by remember { mutableStateOf(authManager.isLoggedIn()) }

                    if (isLoggedIn) {
                        MainAppScreen(
                            authManager = authManager,
                            onLogout = {
                                authManager.logout()
                                isLoggedIn = false
                            }
                        )
                    } else {
                        AuthScreen(
                            authManager = authManager,
                            onAuthSuccess = { isLoggedIn = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    authManager: AuthManager,
    onSpinWheel: () -> Unit
) {
    
fun ANIVISMPTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = PrimaryPurple,
        background = DarkBackground,
        surface = CardBackground,
        onPrimary = TextWhite,
        onBackground = TextWhite,
        onSurface = TextWhite
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}

// --------------------------------------------------
// AUTHENTICATION SCREEN
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    authManager: AuthManager,
    onAuthSuccess: () -> Unit
) {
    var mode by remember { mutableStateOf(0) } // 0: Login, 1: Register, 2: Reset
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var ign by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.anivi_logo),
            contentDescription = "ANIVI SMP Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ANIVI SMP",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = when (mode) {
                0 -> "Welcome Back, Gamer!"
                1 -> "Create New Account"
                else -> "Reset Password"
            },
            fontSize = 14.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = TextGray
            )
        )

        if (mode == 1) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ign,
                onValueChange = { ign = it },
                label = { Text("Minecraft IGN") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = TextGray
                )
            )
        }

        if (mode != 2) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = TextGray
                )
            )
        }

        if (mode == 1) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = TextGray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = ""
                when (mode) {
                    0 -> {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "All fields are required"
                            return@Button
                        }
                        isLoading = true
                        authManager.login(email.trim(), password) { success, msg ->
                            isLoading = false
                            if (success) onAuthSuccess() else errorMessage = msg ?: "Login failed"
                        }
                    }
                    1 -> {
                        if (email.isBlank() || password.isBlank() || ign.isBlank()) {
                            errorMessage = "All fields are required"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }
                        isLoading = true
                        authManager.register(ign, email.trim(), password) { success, msg ->
                            isLoading = false
                            if (success) onAuthSuccess() else errorMessage = msg ?: "Registration failed"
                        }
                    }
                    2 -> {
                        if (email.isBlank()) {
                            errorMessage = "Enter your email"
                            return@Button
                        }
                        isLoading = true
                        authManager.resetPassword(email) { success, msg ->
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                                mode = 0
                            } else {
                                errorMessage = msg ?: "Reset failed"
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = when (mode) {
                        0 -> "LOGIN"
                        1 -> "REGISTER"
                        else -> "SEND RESET EMAIL"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (mode == 0) {
                TextButton(onClick = { mode = 2; errorMessage = "" }) {
                    Text("Forgot Password?", color = TextGray)
                }
                TextButton(onClick = { mode = 1; errorMessage = "" }) {
                    Text("Register", color = PrimaryPurple)
                }
            } else {
                TextButton(onClick = { mode = 0; errorMessage = "" }) {
                    Text("Back to Login", color = PrimaryPurple)
                }
            }
        }
    }
}

// --------------------------------------------------
// MAIN APP SCREEN (WITH BOTTOM NAV & TOP BAR)
// --------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    authManager: AuthManager,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.anivi_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ANIVI SMP", fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = SurfaceDark) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPurple,
                        selectedTextColor = PrimaryPurple,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Dns, contentDescription = "Server") },
                    label = { Text("Server") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPurple,
                        selectedTextColor = PrimaryPurple,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Shop") },
                    label = { Text("Shop") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPurple,
                        selectedTextColor = PrimaryPurple,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Gavel, contentDescription = "Rules") },
                    label = { Text("Rules") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryPurple,
                        selectedTextColor = PrimaryPurple,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    )
                )
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
    authManager = authManager,
    onSpinWheel = {
        selectedTab = 4
    }
)
                1 -> ServerScreen()
                2 -> ShopScreen()
                3 -> RulesScreen()
                4 -> SpinWheelScreen(
    onBack = {
        selectedTab = 0
    }
)
            }
        }
    }
}

// --------------------------------------------------
// HOME SCREEN
// --------------------------------------------------
@Composable
fun HomeScreen(
    authManager: AuthManager,
    onSpinWheel: () -> Unit
) {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(true) }
    var ign by remember { mutableStateOf("Player") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

    authManager.getIGN { playerName ->
        ign = playerName
    }

    coroutineScope.launch {
        isChecking = true

        isOnline = getServerStatus(
            "anivismp.aternos.me",
            25565
        )

        isChecking = false
    }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Welcome Back,", fontSize = 14.sp, color = TextGray)
                    Text(
                        text = ign,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple
                    )
                }
            }
        }

      item {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🎡 ANIVI Spin Wheel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Get your free spins and try your luck!",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onSpinWheel,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🎰 OPEN SPIN WHEEL",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
      }
                )
            }
        }
    }
      }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Live Server Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = if (isChecking) TextGray else if (isOnline) StatusGreen else StatusRed,
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isChecking) "Checking Status..." else if (isOnline) "ONLINE" else "OFFLINE",
                            fontWeight = FontWeight.SemiBold,
                            color = if (isChecking) TextGray else if (isOnline) StatusGreen else StatusRed
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Actions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Server IP", "anivismp.aternos.me")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Server IP Copied!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy IP")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Server IP")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/ehF7HTTqkq"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark)
                    ) {
                        Text("Join Discord Community", color = TextWhite)
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// SERVER SCREEN
// --------------------------------------------------
@Composable
fun ServerScreen() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            InfoCard(
                title = "Server Connection Info",
                description = "IP: anivismp.aternos.me\nPort: 25565 (Java / Bedrock)",
                icon = Icons.Default.Dns
            )
        }
        item {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Server IP", "anivismp.aternos.me")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "IP Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy IP Address")
            }
        }
        item {
            InfoCard(
                title = "About ANIVI SMP",
                description = "ANIVI SMP is a premium Minecraft Survival Multiplayer server featuring custom quests, balanced economy, clans, and exciting weekly events. Join our vibrant community today!",
                icon = Icons.Default.Info
            )
        }
    }
}

// --------------------------------------------------
// SHOP SCREEN
// --------------------------------------------------
@Composable
fun ShopScreen() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Store & Ranks",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
        item {
            InfoCard(
                title = "VIP Rank",
                description = "• Colored Name Tag\n• Access to /fly in hub\n• 2x Claim Blocks",
                icon = Icons.Default.Star
            )
        }
        item {
            InfoCard(
                title = "MVP Rank",
                description = "• All VIP Perks\n• Custom Title\n• Priority Queue Access",
                icon = Icons.Default.WorkspacePremium
            )
        }
        item {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/ehF7HTTqkq"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Purchase via Discord")
            }
        }
    }
}

// --------------------------------------------------
// RULES SCREEN
// --------------------------------------------------
@Composable
fun RulesScreen() {
    val rulesList = listOf(
        "1. Respect all players and staff members.",
        "2. No hacking, cheating, or using unapproved mods/x-ray.",
        "3. Griefing and stealing are strictly prohibited.",
        "4. Keep chat clean: No spam, hate speech, or toxicity.",
        "5. Do not exploit bugs/glitches. Report them to staff immediately.",
        "6. Follow staff instructions at all times."
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Server Rules",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(rulesList) { rule ->
            RuleItem(ruleText = rule)
        }
    }
}

// --------------------------------------------------
// REUSABLE HELPER COMPOSABLES
// --------------------------------------------------
@Composable
fun InfoCard(
    title: String,
    description: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
fun RuleItem(ruleText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = ruleText,
            fontSize = 14.sp,
            color = TextWhite,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// --------------------------------------------------
// NETWORK SERVER STATUS CHECK (NON-UI THREAD)
// --------------------------------------------------
suspend fun getServerStatus(address: String, port: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(address, port), 2000)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
