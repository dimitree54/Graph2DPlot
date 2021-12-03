package we.rashchenko.gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class ProgramState(
    val nnRunning: MutableState<Boolean>,
    val visualMode: MutableState<Boolean>,
    val neuronsMode: MutableState<NeuronsDrawingMode>
)

@Composable
fun createProgramState() = ProgramState(
    remember { mutableStateOf(false) },
    remember { mutableStateOf(false) },
    remember { mutableStateOf(NeuronsDrawingMode.ACTIVITY) }
)
