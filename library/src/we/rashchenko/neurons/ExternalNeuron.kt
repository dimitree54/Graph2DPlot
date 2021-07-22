package we.rashchenko.neurons

interface ExternalNeuron: Neuron {
	override var active: Boolean  // can be set from outside
}