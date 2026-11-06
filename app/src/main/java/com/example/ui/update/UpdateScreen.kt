package com.example.ui.update

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UnboundedFontFamily
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun UpdateScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isCompleted by remember { mutableStateOf(false) }

    // Sistem geri tuşu ile demodan çıkış
    BackHandler {
        onDismiss()
    }

    // 30 saniyelik görsel demo döngüsü: Her 300 ms'de %1 artış (100 adım x 300 ms = 30 sn)
    LaunchedEffect(Unit) {
        val totalSteps = 100
        val stepDelayMs = 300L
        for (step in 1..totalSteps) {
            delay(stepDelayMs)
            progress = (step / totalSteps.toFloat()).coerceIn(0f, 1f)
        }
        isCompleted = true
        delay(1500L) // %100 tamamlandıktan 1.5 saniye sonra otomatik dönüş
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E14))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Sol Üst: Dairesel Koyu Geri Butonu
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E232E),
            modifier = Modifier
                .padding(start = 20.dp, top = 16.dp)
                .size(44.dp)
                .clip(CircleShape)
                .clickable { onDismiss() }
                .align(Alignment.TopStart)
                .testTag("update_back_button")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Ana İçerik: Sol Dikey "GÜNCELLEME" Metni ve Sağ Kolon
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp, bottom = 24.dp, start = 12.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Taraf: Ekran boyunca dikey uzanan büyük harflerle devasa "GÜNCELLEME"
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(52.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GÜNCELLEME",
                    fontFamily = UnboundedFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 42.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    letterSpacing = 6.sp,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .wrapContentSize(unbounded = true)
                        .rotate(-90f)
                        .testTag("update_vertical_title")
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Sağ Taraf (Dikey Column): 3 Parça
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                // 1. Üst Kısım: Dalgalı Sinüs İlerleme Çubuğu (UndulatingProgressBar)
                UndulatingProgressBarCard(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("undulating_progress_bar_card")
                )

                // 2. Orta Kısım: Sağ tarafa hizalanmış dikey dikdörtgen "FKTB" rozeti
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .height(190.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B),
                                    Color(0xFF1E3A8A)
                                )
                            )
                        )
                        .border(
                            border = BorderStroke(
                                width = 2.dp,
                                color = Color(0xFF60A5FA).copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("fktb_vertical_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FKTB",
                        fontFamily = UnboundedFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        color = Color.White,
                        letterSpacing = 6.sp,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .wrapContentSize(unbounded = true)
                            .rotate(90f)
                    )
                }

                // 3. Alt Kısım: Durum Bilgi Kartı
                StatusInfoCard(
                    progress = progress,
                    isCompleted = isCompleted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("update_status_card")
                )
            }
        }
    }
}

@Composable
private fun UndulatingProgressBarCard(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_motion")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "animated_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A22))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
            ) {
                val width = size.width
                val height = size.height

                // Arka plan izi (Track)
                drawRoundRect(
                    color = Color(0xFF1E232E),
                    size = size,
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                )

                val fillWidth = width * animatedProgress.coerceIn(0f, 1f)

                if (fillWidth > 2f) {
                    val waveAmplitude = 4.dp.toPx()
                    val waveFrequency = 0.05f

                    val wavePath = Path().apply {
                        moveTo(0f, height)
                        lineTo(0f, height * 0.5f)

                        var currentX = 0f
                        val step = 3f
                        while (currentX <= fillWidth) {
                            val waveY = height * 0.5f + (sin(currentX * waveFrequency + wavePhase) * waveAmplitude).toFloat()
                            lineTo(currentX, waveY)
                            currentX += step
                        }

                        lineTo(fillWidth, height)
                        close()
                    }

                    // Dalgalı gövde gradyanı (Neon Mavi & KTB Cyan)
                    drawPath(
                        path = wavePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1D4ED8),
                                Color(0xFF3B82F6),
                                Color(0xFF60A5FA),
                                Color(0xFF9EC5EA)
                            ),
                            startX = 0f,
                            endX = fillWidth.coerceAtLeast(1f)
                        )
                    )

                    // Üst dalga çizgisi (Parlak tepe çizgisi)
                    val crestPath = Path().apply {
                        moveTo(0f, height * 0.5f + (sin(wavePhase) * waveAmplitude).toFloat())
                        var currentX = 0f
                        val step = 3f
                        while (currentX <= fillWidth) {
                            val waveY = height * 0.5f + (sin(currentX * waveFrequency + wavePhase) * waveAmplitude).toFloat()
                            lineTo(currentX, waveY)
                            currentX += step
                        }
                    }

                    drawPath(
                        path = crestPath,
                        color = Color(0xFFBAE6FD),
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    // Öncü parıltı (Leading point glow)
                    val leadingY = height * 0.5f + (sin(fillWidth * waveFrequency + wavePhase) * waveAmplitude).toFloat()
                    drawCircle(
                        color = Color(0xFFE0F2FE),
                        radius = 4.dp.toPx(),
                        center = Offset(fillWidth, leadingY)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusInfoCard(
    progress: Float,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val percentInt = (progress * 100).toInt().coerceIn(0, 100)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A22))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCompleted) "Güncelleme Tamamlandı!" else "Güncelleniyor...",
                    fontFamily = UnboundedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isCompleted) Color(0xFF4ADE80) else Color.White
                )

                Text(
                    text = "%$percentInt",
                    fontFamily = UnboundedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = if (isCompleted) Color(0xFF4ADE80) else Color(0xFF9EC5EA)
                )
            }

            Text(
                text = if (isCompleted) "Sistem başarıyla güncellendi." else "Lütfen bekleyiniz, sistem güncelleniyor",
                color = Color(0xFF8E95A5),
                fontSize = 14.sp
            )
        }
    }
}
