package com.example.landmark_detector.presentation

import android.graphics.Bitmap

fun Bitmap.centerCrop(desiredWidh: Int, desiredHeight: Int): Bitmap{
    val xStart = (width - desiredWidh) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidh > width || desiredHeight > height){
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidh, desiredHeight)
}