package we.rashchenko.console

import we.rashchenko.World
import we.rashchenko.neurons.SamplerStats
import we.rashchenko.neurons.getSummary

const val CONTEST_RUNS = 16

fun main() {
    val lock = Object()
    val runStats = mutableMapOf<Pair<String, String>, MutableList<SamplerStats>>()

    (0 until CONTEST_RUNS).toList().parallelStream().forEach {
        val world = World()
        world.run(
            isCancel = {world.timeStep > 1000000},
            onTick = {}
        )
        val stats = world.neuronsManager.getSamplerStats()
        synchronized(lock){
            println(getSummary(stats))
            stats.forEach{ (sampler, stats) ->
                runStats.getOrPut(Pair(sampler.name, sampler.author)) { mutableListOf() }.add(stats)
            }
        }
    }
    val averageRunStats = runStats.mapValues { it.value.average() }
    println()
    println(getResultsMarkdownTable(averageRunStats))
}

fun List<SamplerStats>.average() = SamplerStats(
        sumOf { it.score } / size,
        sumOf { it.chooseProbability }/ size,
        sumOf {it.population} / size
    )

fun getResultsMarkdownTable(stats: Map<Pair<String, String>, SamplerStats>): String{
    val totalPopulation = Integer.max(1, stats.values.sumOf { it.population }).toDouble()
    val summary = StringBuilder(
        "# Results" +
                "\n| Rating position | Sampler name | Author | Score | Population rate |" +
                "\n| --------------- | ------------ | -------| ----- | --------------- |"
    )
    stats.toList().sortedByDescending { it.second.score }.forEachIndexed{ place, (nameAndAuthor, stats) ->
        summary.append("\n| ${place + 1} | ${nameAndAuthor.first} | ${nameAndAuthor.second} |  " +
                "${"%.${2}f".format(stats.score)} | ${"%.${2}f".format(stats.population / totalPopulation)} |"
        )
    }
    return summary.toString()
}
