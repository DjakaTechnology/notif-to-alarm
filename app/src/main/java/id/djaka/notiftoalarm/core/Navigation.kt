package id.djaka.notiftoalarm.core

import android.content.Intent
import androidx.activity.ComponentActivity
import com.chrynan.parcelable.core.Parcelable
import com.chrynan.parcelable.core.getParcelableExtra
import com.chrynan.parcelable.core.putExtra
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer

const val PARAM = "PARAM"

@OptIn(ExperimentalSerializationApi::class)
val ParcelableSerialization = Parcelable

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ComponentActivity.activityParam(): Lazy<T> = lazy {
    intent.getParcelableExtra(PARAM, kClass = T::class, parcelable = ParcelableSerialization)!!
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T: Any> Intent.putParam(param: T) {
    putExtra(PARAM, param, ParcelableSerialization)
}