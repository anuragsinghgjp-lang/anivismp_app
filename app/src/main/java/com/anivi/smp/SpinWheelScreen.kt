package com.anivi.smp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val WheelBackground = Color(0xFF121212)
private val Purple = Color(0xFF9C27B0)
private val CardBackground = Color(0xFF1E1E1E)

private data class WheelReward(
    val name: String,
    val chance: Int
)

private val rewards = listOf(
    WheelReward("💰 $5,000", 30),
    WheelReward("🍞 Common Items", 25),
    WheelReward("🔑 1 Crate Key", 20),
    WheelReward("💎 5 Diamonds", 12),
    WheelReward("💎 10 Diamonds", 7),
    WheelReward("✨ Rare Reward", 4),
    WheelReward("👑 Legendary Reward", 2)
)

private fun chooseReward(): WheelReward {
    val totalChance = rewards.sumOf { it.chance }
    var randomValue = Random.nextInt(1, totalChance + 1)

    for (reward in rewards) {
        randomValue -= reward.chance

        if (randomValue <= 0) {
            return reward
        }
    }

    return rewards.first()
}

@Composable
fun SpinWheelScreen(
    onBack: () -> Unit
) {
    var freeSpins by remember {
        mutableStateOf(3)
    }

    var paidSpins by remember {
        mutableStateOf(5)
    }

    var isSpinning by remember {
        mutableStateOf(false)
    }

    var rotation by remember {
        mutableFloatStateOf(0f)
    }

    var selectedReward by remember {
        mutableStateOf<WheelReward?>(null)
    }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(
            durationMillis = 3500
        ),
        label = "wheel_rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WheelBackground)
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🎡 ANIVI WHEEL",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Spin & try your luck!",
                color = Color.Gray,
                fontSize = 15.sp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                SpinCounterCard(
                    title = "FREE SPINS",
                    count = freeSpins,
                    modifier = Modifier.weight(1f)
                )

                SpinCounterCard(
                    title = "PAID SPINS",
                    count = paidSpins,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = Modifier.size(260.dp)
                ) {

                    val segmentAngle =
                        360f / rewards.size

                    rotate(animatedRotation) {

                        rewards.forEachIndexed { index, _ ->

                            drawArc(
                                color = if (index % 2 == 0) {
                                    Purple
                                } else {
                                    Color(0xFF292929)
                                },
                                startAngle = index * segmentAngle,
                                sweepAngle = segmentAngle,
                                useCenter = true
                            )
                        }
                    }
                }

                Text(
                    text = "▼",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )

                Text(
                    text = "ANIVI",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Choose a spin",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = {

                        if (
                            freeSpins > 0 &&
                            !isSpinning
                        ) {
                            freeSpins--
                            startSpin(
                                onStart = {
                                    isSpinning = true
                                },
                                onRotation = { amount ->
                                    rotation += amount
                                },
                                onResult = { reward ->
                                    selectedReward = reward
                                    isSpinning = false
                                }
                            )
                        }
                    },
                    enabled = freeSpins > 0 && !isSpinning,
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    )
                ) {

                    Text(
                        text = "🎁 FREE\n$freeSpins",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {

                        if (
                            paidSpins > 0 &&
                            !isSpinning
                        ) {
                            paidSpins--
                            startSpin(
                                onStart = {
                                    isSpinning = true
                                },
                                onRotation = { amount ->
                                    rotation += amount
                                },
                                onResult = { reward ->
                                    selectedReward = reward
                                    isSpinning = false
                                }
                            )
                        }
                    },
                    enabled = paidSpins > 0 && !isSpinning,
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A1B9A)
                    )
                ) {

                    Text(
                        text = "💎 PAID\n$paidSpins",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onBack,
                enabled = !isSpinning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("← Back")
            }
        }
    }

    selectedReward?.let { reward ->

        AlertDialog(
            onDismissRequest = {
                selectedReward = null
            },
            title = {
                Text(
                    text = "🎉 Congratulations!"
                )
            },
            text = {
                Column {

                    Text(
                        text = "You won:",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = reward.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Reward chance: ${reward.chance}%"
                    )
                }
            },
            confirmButton = {

                Button(
                    onClick = {
                        selectedReward = null
                    }
                ) {
                    Text("Awesome!")
                }
            }
        )
    }
}

@Composable
private fun SpinCounterCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun startSpin(
    onStart: () -> Unit,
    onRotation: (Float) -> Unit,
    onResult: (WheelReward) -> Unit
) {

    onStart()

    val extraRotation =
        360f * 6 +
        Random.nextInt(0, 360)

    onRotation(extraRotation)

    android.os.Handler(
        android.os.Looper.getMainLooper()
    ).postDelayed({

        onResult(
            chooseReward()
        )

    }, 3500)
}
