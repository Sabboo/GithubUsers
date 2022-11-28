package com.saber.githubusers.glide

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest


/**
 * Inverts the colours of the image.
 */
class InverseTransformation : BitmapTransformation() {
    override fun transform(
        pool: BitmapPool,
        source: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        val matrix = ColorMatrix(
            floatArrayOf(
                -1.0f, 0.0f, 0.0f, 0.0f, 255f,
                0.0f, -1.0f, 0.0f, 0.0f, 255f,
                0.0f, 0.0f, -1.0f, 0.0f, 255f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(source, 0f, 0f, paint)
        return bitmap
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    companion object {
        private const val ID = "android.transformations.glide.Inverse"
        private val ID_BYTES = ID.toByteArray()
    }
}