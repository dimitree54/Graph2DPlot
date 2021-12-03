package we.rashchenko.gui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import we.rashchenko.World

@Composable
fun runButton(world: World, programState: ProgramState, onTick: () -> Unit) =
    Button(
        onClick = {
            programState.nnRunning.value = !programState.nnRunning.value
            if (programState.nnRunning.value){
                Thread{
                    println("NN thread launched")
                    world.run(
                        isCancel = {!programState.nnRunning.value },
                        onTick = onTick
                    )
                    println("NN thread finished")
                }.start()
            }
        }
    ) {
        Text(if (programState.nnRunning.value) "Pause NN" else "Run NN")
    }

@Composable
fun showButton(programState: ProgramState) =
    Button(onClick = { programState.visualMode.value = !programState.visualMode.value }) {
        Text(if (programState.visualMode.value) "HideNN" else "ShowNN")
    }

@Composable
fun infoTPS(runInfo: RunInfo) = Text(
    "TPS: ${runInfo.ticksPerSec.value.toInt()}"
)

@Composable
fun infoScore(runInfo: RunInfo) = Text(
    "Score: ${"%.${2}f".format(runInfo.score.value)}"
)

@Composable
fun chNNWindow(world: World, onCloseRequest: () -> Unit) = Window(onCloseRequest = onCloseRequest) {
    val programState = createProgramState()
    val nnState = createNNState()

    LaunchedEffect(true){
        nnState.update(world, programState)
    }
    val runInfo = createRunInfo()

    fun onTick(){
        runBlocking {
            runInfo.update(world.nnWithInput, world.neuronsManager)
            if (programState.visualMode.value) {
                nnState.update(world, programState)
                delay(1000)  // todo hardcode
            }
            println("tick")
        }
    }

    if (programState.visualMode.value) {
        nnCanvas(nnState)
    }
    Column {
        infoTPS(runInfo)
        infoScore(runInfo)
        runButton(world, programState, ::onTick)
        showButton(programState)
        if (programState.visualMode.value) {
            Button(onClick = {
                programState.neuronsMode.value = programState.neuronsMode.value.next()
                if (!programState.nnRunning.value) {
                    nnState.update(world, programState)
                }
            }) { Text(programState.neuronsMode.value.name) }
        } else {
            Text(runInfo.managerStats.value)
        }
    }
}