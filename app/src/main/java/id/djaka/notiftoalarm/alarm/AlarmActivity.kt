package id.djaka.notiftoalarm.alarm

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import id.djaka.notiftoalarm.core.activityParam
import id.djaka.notiftoalarm.core.putParam
import id.djaka.notiftoalarm.service.NotificationListener
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import id.djaka.notiftoalarm.ui.theme.NotifToAlarmTheme
import id.djaka.notiftoalarm.ui.widget.PackageIcon
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable


@Serializable
class AlarmActivityParam(
    val notificationAppItem: NotificationAppItem,
    val title: String,
    val message: String,
    val playSound: Boolean = false,
)

class AlarmActivity : ComponentActivity() {
    private val param by activityParam<AlarmActivityParam>()
    private val ringtone by lazy {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        RingtoneManager.getRingtone(applicationContext, notification)
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()

        super.onCreate(savedInstanceState)

        setContent {
            NotifToAlarmTheme {
                Surface {
                    AlarmScreen(
                        param.notificationAppItem,
                        param.message,
                        param.title,
                        onSliderFullySwiped = {
                            finish()
                        }
                    )
                }
            }
        }

        if (param.playSound) ringtone.play()
    }

    override fun onDestroy() {
        super.onDestroy()

        ringtone.stop()
        getSystemService(NotificationManager::class.java).cancel(NotificationListener.NOTIFICATION_ID)
    }

    companion object {
        fun create(context: Context, param: AlarmActivityParam): Intent {
            return Intent(context, AlarmActivity::class.java).apply {
                putParam(param)
            }
        }
    }
}

@Composable
fun AlarmScreen(
    notificationAppItem: NotificationAppItem, message: String, title: String,
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
            ColorfulIconSlider(
                value = animateFloatAsState(targetValue = slider).value,
                trackHeight = 64.dp,
                onValueChange = { it ->
                    slider = it
                },
                colors = MaterialSliderDefaults.defaultColors(
                    thumbColor = SliderBrushColor(
                        Color.White
                    ),
                    disabledThumbColor = SliderBrushColor(
                        Color.White
                    ),
                    inactiveTrackColor = SliderBrushColor(
                        MaterialTheme.colorScheme.onSurface
                    ),
                    activeTickColor = SliderBrushColor(
                        MaterialTheme.colorScheme.primary
                    ),
                    activeTrackColor = SliderBrushColor(
                        MaterialTheme.colorScheme.primary
                    ),
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )

            }
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
@Preview(apiLevel = 33)
fun PreviewAlarmScreen() {
    NotifToAlarmTheme {
        Surface {
            NotifToAlarmTheme {
                AlarmScreen(
                    notificationAppItem = NotificationAppItem(
                        id = "id.djaka.notiftoalarm",
                        name = "Example",
                    ), message = "Hello, World!", title = "Hello!"
                )
            }
        }
    }
}