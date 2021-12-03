package we.rashchenko.gui

import androidx.compose.ui.window.application
import we.rashchenko.World


fun main() = application {
    chNNWindow(World(), onCloseRequest = ::exitApplication)
}
