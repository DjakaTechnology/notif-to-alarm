package id.djaka.notiftoalarm.core

import android.content.Intent
import android.os.Parcelable
import androidx.activity.ComponentActivity

fun <T : Parcelable> ComponentActivity.activityParam(): Lazy<T> = lazy { intent.getParcelableExtra<T>("PARAM")!! }

fun Intent.putParam(param: Parcelable) {
    putExtra("PARAM", param)
}