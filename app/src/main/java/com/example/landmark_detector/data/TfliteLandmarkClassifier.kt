package com.example.landmark_detector.data

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.example.landmark_detector.domain.Classification
import com.example.landmark_detector.domain.LandmarkClassifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


class TfLiteLandmarkClassifier(
    private val context: Context,
    private val threshold: Float = 0.5f,
    private val maxResults: Int = 1
): LandmarkClassifier {

    private var africaClassifier: ImageClassifier? = null
    private var asianClassifier: ImageClassifier? = null
    private var europeClassifier: ImageClassifier? = null
    private var antarticaClasssifer: ImageClassifier? = null
    private var northAmericaClassifier: ImageClassifier? = null
    private var southAmericaClassifier: ImageClassifier? = null

    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(threshold)
            .build()

        try {
            africaClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark_africa.tflite",
                options
            )

            asianClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark_asia.tflite",
                options
            )

            europeClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark_europe.tflite",
                options
            )
            antarticaClasssifer = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark_antartica.tflite",
                options
            )
            northAmericaClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark_northamerica.tflite",
                options
            )
            southAmericaClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark_southamerica.tflite",
                options
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {
        if(africaClassifier == null || asianClassifier == null || europeClassifier == null || antarticaClasssifer == null || northAmericaClassifier == null || southAmericaClassifier == null) {
            setupClassifier()
        }

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()



        val africaResults = africaClassifier?.classify(tensorImage, imageProcessingOptions)
        val asiaResults = asianClassifier?.classify(tensorImage, imageProcessingOptions)
        val europeResults = europeClassifier?.classify(tensorImage, imageProcessingOptions)
        val antarticaResults = antarticaClasssifer?.classify(tensorImage, imageProcessingOptions)
        val northAmericaResults = northAmericaClassifier?.classify(tensorImage, imageProcessingOptions)
        val southAmericaResults = southAmericaClassifier?.classify(tensorImage, imageProcessingOptions)

        val allResults = listOfNotNull(africaResults, asiaResults, europeResults, antarticaResults, northAmericaResults, southAmericaResults).flatten()

        return allResults.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    name = category.displayName,
                    score = category.score
                )
            }
        }.maxByOrNull { it.score }?.let { listOf(it) } ?: emptyList()
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when(rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }
}












