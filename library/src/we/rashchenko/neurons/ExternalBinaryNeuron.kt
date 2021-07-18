package we.rashchenko.neurons

import we.rashchenko.neurons.BinaryNeuron

interface ExternalBinaryNeuron: BinaryNeuron {
	override var active: Boolean  // can be set from outside
}