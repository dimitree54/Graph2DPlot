package we.rashchenko.neurons

interface ExternalBinaryNeuron: BinaryNeuron {
	override var active: Boolean  // can be set from outside
}