package we.rashchenko.gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import we.rashchenko.networks.NeuralNetworkWithInput
import we.rashchenko.neurons.NeuronsManager
import we.rashchenko.neurons.inputs.InputNeuron
import we.rashchenko.utils.ExponentialMovingAverage

// todo such info would be useful for console version. Can we share it?
class RunInfo(
    val score: MutableState<Double>,
    val ticksPerSec: MutableState<Double>,
    val managerStats: MutableState<String>
) {
    private var lastTimeStep = 0.0
    private var lastTimeMS = System.currentTimeMillis().toDouble()
    private var scoreAggregator = ExponentialMovingAverage(0.0)
    private var tpsAggregator = ExponentialMovingAverage(0.0)

    fun update(nn: NeuralNetworkWithInput, manager: NeuronsManager) {
        scoreAggregator.update(
            nn.inputNeuronIDs.sumOf { (nn.getNeuron(it)!! as InputNeuron).getInternalFeedback().value })
        score.value = scoreAggregator.value
        val currentTime = System.currentTimeMillis().toDouble()
        tpsAggregator.update(1000 * (nn.timeStep - lastTimeStep) / (currentTime - lastTimeMS + 1))
        ticksPerSec.value = tpsAggregator.value
        lastTimeStep = nn.timeStep.toDouble()
        lastTimeMS = currentTime
        managerStats.value = manager.getSummary()
    }
}

@Composable
fun createRunInfo() = RunInfo(
    remember { mutableStateOf(0.0) },
    remember { mutableStateOf(0.0) },
    remember { mutableStateOf("") }
)
