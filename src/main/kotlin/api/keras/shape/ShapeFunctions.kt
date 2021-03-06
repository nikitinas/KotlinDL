package api.keras.shape

import org.tensorflow.Operand
import org.tensorflow.Shape
import org.tensorflow.Tensor
import org.tensorflow.op.Ops
import kotlin.math.abs

fun constArray(tf: Ops, vararg i: Int): Operand<Int> {
    return tf.constant(i)
}

fun shapeOperand(tf: Ops, shape: Shape): Operand<Int> {
    val shapeArray = IntArray(shape.numDimensions())
    for (i in shapeArray.indices) {
        shapeArray[i] = shape.size(i).toInt()
    }
    return tf.constant(shapeArray)
}

fun shapeToIntArray(shape: Shape): IntArray {
    val shapeArray = IntArray(shape.numDimensions())
    for (i in shapeArray.indices) {
        shapeArray[i] = shape.size(i).toInt()
    }
    return shapeArray
}

fun shapeToLongArray(shape: Shape): LongArray {
    val shapeArray = LongArray(shape.numDimensions())
    for (i in shapeArray.indices) {
        shapeArray[i] = shape.size(i)
    }
    return shapeArray
}

fun shapeArrayToString(shape: Shape): String {
    val shapeArray = IntArray(shape.numDimensions())
    for (i in shapeArray.indices) {
        shapeArray[i] = shape.size(i).toInt()
    }
    return shapeArray.contentToString()
}

fun head(vararg dims: Long): Long {
    return dims[0]
}

fun tail(vararg dims: Long): LongArray {
    return dims.copyOfRange(1, dims.size)
}

fun tail(shape: Shape): LongArray {
    val shapeArray = LongArray(shape.numDimensions())
    for (i in shapeArray.indices) {
        shapeArray[i] = shape.size(i)
    }
    return tail(*shapeArray)
}

fun shapeFromDims(vararg dims: Long): Shape {
    return Shape.make(head(*dims), *tail(*dims))
}


fun numElementsInShape(shape: LongArray): Long {
    var prod = 1L
    for (i in shape.indices) {
        prod *= abs(shape[i])
    }
    return prod
}

fun reshape2DTo1D(dst: Array<FloatArray>, size: Int): FloatArray {
    val result = FloatArray(size) { 0.0f }

    var pos = 0

    for (i in dst.indices) {
        for (j in dst[i].indices) {
            result[pos] = dst[i][j]
            pos++
        }
    }

    return result
}

fun reshape3DTo1D(dst: Array<Array<FloatArray>>, size: Int): FloatArray {
    val result = FloatArray(size) { 0.0f }

    var pos = 0
    for (i in dst.indices) {
        for (j in dst[i].indices) {
            for (k in dst[i][j].indices) {

                result[pos] = dst[i][j][k]
                pos++
            }

        }
    }
    return result
}

fun reshape4DTo1D(dst: Array<Array<Array<FloatArray>>>, size: Int): FloatArray {
    val result = FloatArray(size) { 0.0f }

    var pos = 0
    for (i in dst.indices) {
        for (j in dst[i].indices) {
            for (k in dst[i][j].indices) {
                for (m in dst[i][j][k].indices) {
                    result[pos] = dst[i][j][k][m]
                    pos++
                }
            }
        }
    }
    return result
}

// TODO: to extension function
fun convertTensorToFlattenFloatArray(
    tensorForCopying: Tensor<*>
): FloatArray {
    val shape = tensorForCopying.shape()
    val reshaped: FloatArray
    return when (shape.size) {
        1 -> {
            reshaped = FloatArray(shape[0].toInt()) { 0.0f }
            tensorForCopying.copyTo(reshaped)
            reshaped
        }
        2 -> {
            val dst =
                Array(shape[0].toInt()) { FloatArray(shape[1].toInt()) }
            tensorForCopying.copyTo(dst)
            reshaped = reshape2DTo1D(dst, numElementsInShape(shape).toInt())
            reshaped
        }
        3 -> {
            val dst = Array(shape[0].toInt()) {
                Array(shape[1].toInt()) {
                    FloatArray(shape[2].toInt())
                }
            }
            tensorForCopying.copyTo(dst)
            reshaped = reshape3DTo1D(dst, numElementsInShape(shape).toInt())
            reshaped
        }
        4 -> {
            val dst = Array(shape[0].toInt()) {
                Array(shape[1].toInt()) {
                    Array(shape[2].toInt()) {
                        FloatArray(shape[3].toInt())
                    }
                }
            }
            tensorForCopying.copyTo(dst)
            reshaped = reshape4DTo1D(dst, numElementsInShape(shape).toInt())
            reshaped
        }
        else -> {
            throw UnsupportedOperationException("Parsing for ${shape.size} dimensions is not supported yet!")
        }
    }
    return reshaped
}

// TODO: to extension function
fun convertTensorToMultiDimArray(
    tensorForCopying: Tensor<*>
): Array<*> {
    val shape = tensorForCopying.shape()
    val dst: Array<*>

    return when (shape.size) {
        1 -> {
            val oneDimDst = FloatArray(shape[0].toInt()) { 0.0f }
            tensorForCopying.copyTo(oneDimDst)
            dst = Array(oneDimDst.size) { 0.0f }
            for (i in oneDimDst.indices) dst[i] = oneDimDst[i]
            dst
        }
        2 -> {
            dst = Array(shape[0].toInt()) { FloatArray(shape[1].toInt()) }

            tensorForCopying.copyTo(dst)
            dst
        }
        3 -> {
            dst = Array(shape[0].toInt()) {
                Array(shape[1].toInt()) {
                    FloatArray(shape[2].toInt())
                }
            }
            tensorForCopying.copyTo(dst)
            dst
        }
        4 -> {
            dst = Array(shape[0].toInt()) {
                Array(shape[1].toInt()) {
                    Array(shape[2].toInt()) {
                        FloatArray(shape[3].toInt())
                    }
                }
            }
            tensorForCopying.copyTo(dst)
            dst
        }
        else -> {
            throw UnsupportedOperationException("Parsing for ${shape.size} dimensions is not supported yet!")
        }
    }
}

