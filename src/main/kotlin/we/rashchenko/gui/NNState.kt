package we.rashchenko.gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import we.rashchenko.World
import we.rashchenko.utils.Vector2

class NNState(
    val inputPositions: MutableState<List<Vector2>>,
    val positions: MutableState<List<Vector2>>,
    val coloredConnections: MutableState<List<Triple<Vector2, Vector2, Color>>>,
    val neuronColors: MutableState<List<Color>>
) {
    fun update(
        world: World, programState: ProgramState
    ) {
        inputPositions.value = world.builder.getInputPositions()
        positions.value = world.builder.getAllPositions()
        neuronColors.value = when (programState.neuronsMode.value) {
            NeuronsDrawingMode.FEEDBACK -> world.controlledNN.getNeuronFeedbackColors()
            NeuronsDrawingMode.EXTERNAL_FEEDBACK -> world.controlledNN.getControllerFeedbackColors()
            NeuronsDrawingMode.INTERNAL_FEEDBACK -> world.controlledNN.getCollaborativeFeedbackColors()
            else -> world.controlledNN.getActivePassiveColors()
        }
        coloredConnections.value = world.builder.getConnectionsWithColor()
    }
}

@Composable
fun createNNState() = NNState(
    remember { mutableStateOf(emptyList()) },
    remember { mutableStateOf(emptyList()) },
    remember { mutableStateOf(emptyList()) },
    remember { mutableStateOf(emptyList()) }
)
