package we.rashchenko

import we.rashchenko.base.Ticking
import we.rashchenko.environments.SimpleEnvironment
import we.rashchenko.networks.ControlledNeuralNetwork
import we.rashchenko.networks.StochasticNeuralNetwork
import we.rashchenko.networks.builders.Evolution
import we.rashchenko.networks.builders.NeuralNetworkIn2DBuilder
import we.rashchenko.networks.controllers.ActivityController
import we.rashchenko.networks.controllers.ComplexController
import we.rashchenko.networks.controllers.TimeController
import we.rashchenko.neurons.getContestManager

const val ENV_TICK_PERIOD = 100

const val CONTROLLER_AUDIT_PROBABILITY = 0.1
const val CONTROLLER_UPDATE_FEEDBACK_PERIOD = 1000L
const val EXTERNAL_FEEDBACK_WEIGHT = 0.2

const val NUM_NEURONS = 1000

const val EVOLUTION_NEURONS_FOR_SELECTION = 100
const val EVOLUTION_WARNINGS_BEFORE_KILL = 10
const val EVOLUTION_PROBABILITY = 0.1


class World: Ticking {
    @Suppress("MemberVisibilityCanBePrivate")
    val environment = SimpleEnvironment(ENV_TICK_PERIOD)
    val nnWithInput = StochasticNeuralNetwork()
    val controlledNN = ControlledNeuralNetwork(
        nnWithInput,
        ComplexController(
            listOf(TimeController(), ActivityController())
        ),
        CONTROLLER_AUDIT_PROBABILITY, CONTROLLER_UPDATE_FEEDBACK_PERIOD, EXTERNAL_FEEDBACK_WEIGHT
    )
    val neuronsManager = getContestManager()
    val builder = NeuralNetworkIn2DBuilder(
        controlledNN,
        neuronsManager
    ).apply {
        addEnvironment(environment)
        repeat(NUM_NEURONS) { addNeuron() }
    }
    @Suppress("MemberVisibilityCanBePrivate")
    val evolution = Evolution(builder,
        EVOLUTION_NEURONS_FOR_SELECTION, EVOLUTION_WARNINGS_BEFORE_KILL, EVOLUTION_PROBABILITY
    )

    override val timeStep: Long
        get() = environment.timeStep

    override fun tick(){
        controlledNN.tick()
        environment.tick()
        evolution.tick()
    }

    fun run(
        isCancel: () -> Boolean,
        onTick: () -> Unit
    ){
        while(!isCancel()){
            tick()
            onTick()
        }
    }
}

