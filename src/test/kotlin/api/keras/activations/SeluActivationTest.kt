package api.keras.activations

import org.junit.jupiter.api.Test

internal class SeluActivationTest : ActivationTest() {

    @Test
    fun apply() {
        val input = floatArrayOf(-100f, -10f, -1f, 0f, 1f, 10f, 100f)
        val actual = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val expected = floatArrayOf(
            -1.7580993f, -1.7580194f, -1.1113307f, 0.0f, 1.050701f,
            10.50701f, 105.0701f
        )

        assertActivationFunction(SeluActivation(), input, actual, expected)
    }
}