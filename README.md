# ChNN-Client (Chaotic neural network client)

That repository contains a code for ChNN contest inference and visualisation. 
To check more info about ChNN project you can visit [project page](https://dimitree54.github.io/ChNN/).

## ChNN-Contest

To participate, please visit [contest page](https://dimitree54.github.io/ChNN/contest/). 

## ChNN-Contest evaluation

For ChNN contest evaluation (console version), client do following: 
1. takes neurons from [ChNN-Neurons](https://github.com/dimitree54/ChNN-Neurons) zoo;
2. randomly initializes neural network with them (alongside with the environment in which that neural network placed);
3. runs that network for some time;
4. remembers what score each neuron sampler achieved during the run;
5. repeats 2-4 steps several times and averages sampler scores;
6. using it scores creates rate table and prints it to stdout in Markdown format.

## ChNN visualisation

Desktop compose application to observe ChNN evolution. 
It slows down ChNN ticks per second to visually acceptable rate, 
 and you can see how neurons born and die, create new connections. 
To resume maximum ticks per second you can hide NN visualisation.
Several view options provided:
- Neurons activation to see what neurons active/passive right now and how activation spreads.
- Neurons feedback mode to see what neurons most likely to die
- Neurons feedback - only external mode (from NN controllers)
- Neurons feedback - only internal mode (from other neurons)

## Installation

Installing that application is quite straight forward, gradle will all the work for you. 
Console version launcher can be found at `we/rashchenko/console/Main.kt`,
Desktop version at `we/rashchenko/gui/Main.kt`.
The only tricky thing is to make ChNN-Library available for gradle.
To do so you have to add your GITHUB auth info to your environment variables.
More info can be found at installation section of the [ChNN-Library readme](https://github.com/dimitree54/ChNN-Library).

## Contribute
We would be happy to see you moving ChNN project forward. 
More info about how to help can be found on [contribution page](https://dimitree54.github.io/ChNN/contribution/)
and in the Contribution section of [ChNN-Library readme](https://github.com/dimitree54/ChNN-Library).

## Author
If you have questions or suggestion not covered by the README, you can reach me (Dmitrii Rashchenko) via email (dimitree54@gmail.com)
