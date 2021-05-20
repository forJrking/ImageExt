package com.github.forjrking.image

import com.github.forjrking.image.core.ImageOptions
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        ImageOptions.DrawableOptions.setDefault {
            errorResId = 2
            placeHolderResId = 1
        }

        val imageOptions = ImageOptions()
        imageOptions.setDrawableOptions {
            errorResId =3
        }

        println(ImageOptions.DrawableOptions.DEFAULT)
        println(imageOptions.drawableOptions)
    }
}