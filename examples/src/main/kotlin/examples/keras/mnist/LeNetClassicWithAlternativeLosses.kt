package examples.keras.mnist

import api.keras.Sequential
import api.keras.activations.Activations
import api.keras.history.EpochTrainingEvent
import api.keras.initializers.Constant
import api.keras.initializers.GlorotNormal
import api.keras.initializers.Zeros
import api.keras.layers.Dense
import api.keras.layers.Flatten
import api.keras.layers.Input
import api.keras.layers.twodim.AvgPool2D
import api.keras.layers.twodim.Conv2D
import api.keras.layers.twodim.ConvPadding
import api.keras.loss.LossFunctions
import api.keras.metric.Metrics
import api.keras.optimizers.Adam
import api.keras.optimizers.ClipGradientByValue
import datasets.Dataset
import datasets.handlers.*

private const val EPOCHS = 3
private const val TRAINING_BATCH_SIZE = 2000
private const val NUM_CHANNELS = 1L
private const val IMAGE_SIZE = 28L
private const val SEED = 12L
private const val TEST_BATCH_SIZE = 1000

private val lenet5Classic = Sequential.of(
    Input(
        IMAGE_SIZE,
        IMAGE_SIZE,
        NUM_CHANNELS
    ),
    Conv2D(
        filters = 6,
        kernelSize = longArrayOf(5, 5),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Tanh,
        kernelInitializer = GlorotNormal(SEED),
        biasInitializer = Zeros(),
        padding = ConvPadding.SAME
    ),
    AvgPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1),
        padding = ConvPadding.VALID
    ),
    Conv2D(
        filters = 16,
        kernelSize = longArrayOf(5, 5),
        strides = longArrayOf(1, 1, 1, 1),
        activation = Activations.Tanh,
        kernelInitializer = GlorotNormal(SEED),
        biasInitializer = Zeros(),
        padding = ConvPadding.SAME
    ),
    AvgPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
        strides = intArrayOf(1, 2, 2, 1),
        padding = ConvPadding.VALID
    ),
    Flatten(), // 3136
    Dense(
        outputSize = 120,
        activation = Activations.Tanh,
        kernelInitializer = GlorotNormal(SEED),
        biasInitializer = Constant(0.1f)
    ),
    Dense(
        outputSize = 84,
        activation = Activations.Tanh,
        kernelInitializer = GlorotNormal(SEED),
        biasInitializer = Constant(0.1f)
    ),
    Dense(
        outputSize = AMOUNT_OF_CLASSES,
        activation = Activations.Sigmoid,
        kernelInitializer = GlorotNormal(SEED),
        biasInitializer = Constant(0.1f)
    )
)

fun main() {
    val (train, test) = Dataset.createTrainAndTestDatasets(
        TRAIN_IMAGES_ARCHIVE,
        TRAIN_LABELS_ARCHIVE,
        TEST_IMAGES_ARCHIVE,
        TEST_LABELS_ARCHIVE,
        AMOUNT_OF_CLASSES,
        ::extractImages,
        ::extractLabels
    )

    val (newTrain, validation) = train.split(0.95)

    lenet5Classic.use {
        it.compile(
            optimizer = Adam(clipGradient = ClipGradientByValue(0.5f)),
            loss = LossFunctions.RMSE,
            metric = Metrics.ACCURACY
        )

        it.summary()

        val history = it.fit(
            trainingDataset = newTrain,
            validationDataset = validation,
            validationBatchSize = TRAINING_BATCH_SIZE,
            epochs = EPOCHS,
            trainBatchSize = TRAINING_BATCH_SIZE,
            verbose = true
        )

        val accuracy = it.evaluate(dataset = test, batchSize = TEST_BATCH_SIZE).metrics[Metrics.ACCURACY]

        println("Accuracy: $accuracy")

        val accuracyByEpoch = history[EpochTrainingEvent::metricValue]
        println(accuracyByEpoch.contentToString())
    }
}