package id.djaka.notiftoalarm.shared.ui.alarm

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.djaka.notiftoalarm.shared.closeApp
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import id.djaka.notiftoalarm.shared.ui.theme.NotifToAlarmTheme
import id.djaka.notiftoalarm.shared.ui.widget.PackageIcon
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AlarmNavigator(
    notificationAppItem: NotificationAppItem,
    title: String,
    message: String,
    playSound: Boolean
) {
    Navigator(
        AlarmScreen(
            notificationAppItem = notificationAppItem,
            title = title,
            message = message,
            playSound = playSound
        )
    )
}

interface SoundPlayer {
    fun play()

    fun stop()
}

class AlarmScreen(
    private val notificationAppItem: NotificationAppItem,
    private val title: String,
    private val message: String,
    private val playSound: Boolean,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val soundPlayer = soundPlayer()
        LifecycleEffect(
            onStarted = {
                if (playSound) {
                    soundPlayer.play()
                }
            },
            onDisposed = {
                soundPlayer.stop()
            }
        )

        NotifToAlarmTheme {
            Screen(
                notificationAppItem = notificationAppItem,
                message = message,
                title = title,
                onSliderFullySwiped = {
                    if (navigator.canPop) {
                        navigator.pop()
                    } else {
                        closeApp()
                    }
                }
            )
        }
    }
}

@Composable
expect fun soundPlayer(): SoundPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    notificationAppItem: NotificationAppItem,
    message: String, title: String,
    onSliderFullySwiped: () -> Unit = {}
) {
    var slider by remember { mutableStateOf(0f) }
    LaunchedEffect(slider) {
        if (slider == 1f) {
            onSliderFullySwiped()
        } else {
            delay(1000)
            slider = 0f
        }
    }
    Scaffold {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Box {
                Pulsating(pulseFraction = 1.2f) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .scale(3f)
                            .clip(CircleShape)
                            .alpha(0.8f)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))
                    )
                }
                Pulsating(pulseFraction = 1.4f) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .scale(4f)
                            .clip(CircleShape)
                            .alpha(0.3f)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))
                    )
                }
                Pulsating(pulseFraction = 2f) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .scale(4f)
                            .clip(CircleShape)
                            .alpha(0.3f)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))
                    )
                }
                PackageIcon(
                    packageName = notificationAppItem.id,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = notificationAppItem.name, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(48.dp))
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = message, fontSize = 18.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.weight(0.5f))
            Slider(
                value = animateFloatAsState(targetValue = slider).value,
                onValueChange = {
                    slider = it
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            "swipe",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.weight(0.3f))
        }
    }
}

@Composable
fun Pulsating(pulseFraction: Float = 1.2f, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.scale(scale)) {
        content()
    }
}

@Composable
@Preview
fun PreviewAlarmScreen() {
    NotifToAlarmTheme {
        Surface {
            NotifToAlarmTheme {
                Screen(
                    notificationAppItem = NotificationAppItem(
                        id = "id.djaka.notiftoalarm",
                        name = "Example",
                    ), message = "Hello, World!", title = "Hello!",
                    onSliderFullySwiped = {}
                )
            }
        }
    }
}