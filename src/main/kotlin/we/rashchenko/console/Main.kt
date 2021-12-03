package we.rashchenko.console

import we.rashchenko.World

fun main() {
    val world = World()
    world.run(
        isCancel = {world.timeStep > 100000},
        onTick = {}
    )
    println(getSummary(world.neuronsManager.getSamplerStats()))
}