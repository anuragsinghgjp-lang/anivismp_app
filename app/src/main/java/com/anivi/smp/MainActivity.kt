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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

private val PrimaryPurple = Color(0xFF9C27B0)
private val DarkBackground = Color(0xFF121212)
private val CardBackground = Color(0xFF1E1E1E)
private val SurfaceDark = Color(0xFF252525)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGray = Color(0xFFA0A0A0)
private val StatusGreen = Color(0xFF4CAF50)
private val StatusRed = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    background = DarkBackground,
    surface = CardBackground,
    onPrimary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun ANIVISMPTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

class MainActivity : ComponentActivity() {
    private val authManager = AuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ANIVISMPTheme {
                var isLoggedIn by remember { mutableStateOf(authManager.isLoggedIn()) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    authManager: AuthManager,
    onLoginSuccess: () -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }

    var ign by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.anivi_logo),
                contentDescription = "ANIVI SMP Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ANIVI SMP",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Text(
                text = when (authMode) {
                    AuthMode.LOGIN -> "Welcome back, adventurer!"
                    AuthMode.REGISTER -> "Join the Minecraft SMP"
                    AuthMode.FORGOT_PASSWORD -> "Reset your password"
                },
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (authMode == AuthMode.REGISTER) {
                        OutlinedTextField(
                            value = ign,
                            onValueChange = { ign = it },
                            label = { Text("Minecraft IGN", color = TextGray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = SurfaceDark,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = TextGray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = SurfaceDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (authMode != AuthMode.FORGOT_PASSWORD) {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("App Password", color = TextGray) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = SurfaceDark,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (authMode == AuthMode.REGISTER) {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm App Password", color = TextGray) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = SurfaceDark,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = err,
                            color = StatusRed,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = PrimaryPurple)
                    } else {
                        Button(
                            onClick = {
                                errorMessage = null
                                when (authMode) {
                                    AuthMode.LOGIN -> {
                                        if (email.isBlank() || password.isBlank()) {
                                            errorMessage = "Please fill in all fields"
                                            return@Button
                                        }
                                        isLoading = true
                                        authManager.login(email, password) { success, msg ->
                                            isLoading = false
                                            if (success) {
                                                onLoginSuccess()
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    }
                                    AuthMode.REGISTER -> {
                                        if (password != confirmPassword) {
                                            errorMessage = "Passwords do not match"
                                            return@Button
                                        }
                                        isLoading = true
                                        authManager.register(ign, email, password) { success, msg ->
                                            isLoading = false
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            if (success) {
                                                onLoginSuccess()
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    }
                                    AuthMode.FORGOT_PASSWORD -> {
                                        if (email.isBlank()) {
                                            errorMessage = "Please enter your email"
                                            return@Button
                                        }
                                        isLoading = true
                                        authManager.resetPassword(email) { success, msg ->
                                            isLoading = false
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                            if (success) {
                                                authMode = AuthMode.LOGIN
                                            } else {
                                                errorMessage = msg
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = when (authMode) {
                                    AuthMode.LOGIN -> "LOGIN"
                                    AuthMode.REGISTER -> "REGISTER"
                                    AuthMode.FORGOT_PASSWORD -> "SEND RESET EMAIL"
                                },
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (authMode) {
                AuthMode.LOGIN -> {
                    TextButton(onClick = {
                        errorMessage = null
                        authMode = AuthMode.FORGOT_PASSWORD
                    }) {
                        Text("Forgot Password?", color = TextGray)
                    }

                    TextButton(onClick = {
                        errorMessage = null
                        authMode = AuthMode.REGISTER
                    }) {
                        Text("Don't have an account? Register", color = PrimaryPurple)
                    }
                }
                AuthMode.REGISTER -> {
                    TextButton(onClick = {
                        errorMessage = null
                        authMode = AuthMode.LOGIN
                    }) {
                        Text("Already have an account? Login", color = PrimaryPurple)
                    }
                }
                AuthMode.FORGOT_PASSWORD -> {
                    TextButton(onClick = {
                        errorMessage = null
                        authMode = AuthMode.LOGIN
                    }) {
                        Text("Back to Login", color = PrimaryPurple)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    authManager: AuthManager,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anivi_logo),
                            contentDescription = "ANIVI Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "ANIVI SMP",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(visible = selectedTab != 4) {
                NavigationBar(
                    containerColor = CardBackground
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryPurple,
                            selectedTextColor = PrimaryPurple,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray,
                            indicatorColor = SurfaceDark
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
                            unselectedTextColor = TextGray,
                            indicatorColor = SurfaceDark
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
                            unselectedTextColor = TextGray,
                            indicatorColor = SurfaceDark
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Rules") },
                        label = { Text("Rules") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryPurple,
                            selectedTextColor = PrimaryPurple,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray,
                            indicatorColor = SurfaceDark
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    authManager = authManager,
                    onSpinWheel = { selectedTab = 4 }
                )
                1 -> ServerScreen()
                2 -> ShopScreen()
                3 -> RulesScreen()
                4 -> SpinWheelScreen(
                    onBack = { selectedTab = 0 }
                )
            }
        }
    }
}

suspend fun getServerStatus(host: String, port: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 3000)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}

@Composable
fun HomeScreen(
    authManager: AuthManager,
    onSpinWheel: () -> Unit
) {
    var ign by remember { mutableStateOf("Player") }
    var isOnline by remember { mutableStateOf<Boolean?>(null) }
    var isChecking by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val host = "anivismp.aternos.me"
    val port = 25565

    LaunchedEffect(Unit) {
        authManager.getIGN { playerName ->
            ign = playerName
        }
        isChecking = true
        isOnline = getServerStatus(host, port)
        isChecking = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome Back,",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = ign,
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = PrimaryPurple,
                    modifier = Modifier
                        .size(48.dp)
                        .background(SurfaceDark, CircleShape)
                        .padding(8.dp)
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "🎡 ANIVI Spin Wheel",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Get your free spins and try your luck!",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSpinWheel,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎰 OPEN SPIN WHEEL",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Server Status",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isChecking = true
                                isOnline = getServerStatus(host, port)
                                isChecking = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = PrimaryPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isChecking) TextGray
                                else if (isOnline == true) StatusGreen
                                else StatusRed
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isChecking) "CHECKING..."
                        else if (isOnline == true) "ONLINE"
                        else "OFFLINE",
                        color = if (isChecking) TextGray
                        else if (isOnline == true) StatusGreen
                        else StatusRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "IP: $host",
                    color = TextWhite,
                    fontSize = 14.sp
                )
                Text(
                    text = "Port: $port",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Server IP", host)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Server IP copied!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy IP",
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy IP", color = TextWhite, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://discord.gg/V2YK4kvm53")
                    )
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Discord",
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Discord", color = TextWhite, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ServerScreen() {
    val context = LocalContext.current
    val host = "anivismp.aternos.me"
    val port = 25565
    var isOnline by remember { mutableStateOf<Boolean?>(null) }
    var isChecking by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isChecking = true
        isOnline = getServerStatus(host, port)
        isChecking = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "ANIVI SMP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Minecraft Survival Multiplayer Server",
                    fontSize = 14.sp,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (isChecking) TextGray
                                else if (isOnline == true) StatusGreen
                                else StatusRed
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isChecking) "Checking Status..."
                        else if (isOnline == true) "Server Online"
                        else "Server Offline",
                        color = TextWhite,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoCard(label = "Server IP", value = host)
                Spacer(modifier = Modifier.height(8.dp))
                InfoCard(label = "Port", value = port.toString())

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Server IP", host)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Server IP copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy IP",
                        tint = TextWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("COPY SERVER IP", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "About the Server",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ANIVI SMP is a friendly and competitive Minecraft Survival Multiplayer server. Join us to build, trade, explore, and participate in fun community events!",
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextGray, fontSize = 14.sp)
        Text(text = value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ShopScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Server Shop",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )

        Text(
            text = "Enhance your gameplay with exclusive rank perks!",
            fontSize = 14.sp,
            color = TextGray
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PrimaryPurple, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIP Rank",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple
                    )
                    Text(
                        text = "PERKS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier
                            .background(PrimaryPurple, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("• Custom VIP Tag in chat", color = TextGray, fontSize = 14.sp)
                Text("• Access to /fly in claims", color = TextGray, fontSize = 14.sp)
                Text("• 3 Extra Sethomes", color = TextGray, fontSize = 14.sp)
                Text("• Reserved slot connection", color = TextGray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://discord.gg/V2YK4kvm53")
                        )
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Purchase via Discord", fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, StatusGreen, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MVP Rank",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen
                    )
                    Text(
                        text = "POPULAR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier
                            .background(StatusGreen, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("• All VIP Perks included", color = TextGray, fontSize = 14.sp)
                Text("• Custom MVP Tag in chat", color = TextGray, fontSize = 14.sp)
                Text("• 5 Extra Sethomes", color = TextGray, fontSize = 14.sp)
                Text("• Special Discord Role", color = TextGray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://discord.gg/V2YK4kvm53")
                        )
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Purchase via Discord", fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
fun RulesScreen() {
    val rules = listOf(
        "No hacking, cheating, or using unfair client modifications.",
        "No exploiting bugs - report any issues to the staff immediately.",
        "No griefing or stealing in claimed territories.",
        "No toxic behavior, hate speech, or harassment in chat.",
        "Respect all players and server staff members.",
        "No spamming, advertising other servers, or self-promotion.",
        "Follow staff instructions at all times."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Server Rules",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )

        Text(
            text = "To keep ANIVI SMP fun and fair for everyone, please strictly follow these rules:",
            fontSize = 14.sp,
            color = TextGray
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rules.forEachIndexed { index, rule ->
                    RuleItem(number = index + 1, ruleText = rule)
                }
            }
        }
    }
}

@Composable
fun RuleItem(number: Int, ruleText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = ruleText,
            color = TextWhite,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}