package catrin.dev.pairgame.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

object ResourceUtils {
    fun getColor(context: Context, color: Int): Int {
        return ResourcesCompat.getColor(context.resources, color, null)
    }

    fun getDrawable(context: Context, drawable: Int): Drawable? {
        return ResourcesCompat.getDrawable(context.resources, drawable, null)
    }
}