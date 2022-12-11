package com.serialIntCaster

import mt19937ar.MersenneTwister

/**
 *
 * Fisher Yates Seeded shuffle using improved algorithm and seed
 *
 * @author Jérémy Munsch <github@jeremydev.ovh>
 * @url https://stackoverflow.com/questions/24262147/can-a-seeded-shuffle-be-reversed
 * @url https://fr.wikipedia.org/wiki/M%C3%A9lange_de_Fisher-Yates
 */
class FisherYatesShuffler(seed: Long) {
    private val seedInt: Long = seed
    private var twister: MersenneTwister = MersenneTwister(seed)

    /**
     * Get the actual seed.
     */
    fun seed(): Long
    {
        return seedInt
    }

    /** Shuffle a string using the seed number */
    fun shuffle(string: String): String {
        twister = MersenneTwister(seedInt)
        val stringMutable = string.toMutableList()
        var i = string.length - 1
        while (i >= 1) {
            val j = this.random(0, i.toLong()).toInt()
            val t = stringMutable[j]
            stringMutable[j] = stringMutable[i]
            stringMutable[i] = t
            i--
        }
        return stringMutable.joinToString("")
    }

    /** Un shuffle a string using the seed number */
    fun unShuffle(string: String): String {
        twister = MersenneTwister(seedInt)
        val stringMutable = string.toMutableList()
        val length = string.length
        val indices: MutableList<Int> = MutableList(length) { 0 }

        var i = length -1
        while (i >= 1) {
            indices[i] = this.random(0, i.toLong()).toInt()
            i--
        }
        indices.removeAt(0)
        indices.withIndex().forEach{
            val t = stringMutable[it.value]
            stringMutable[it.value] = stringMutable[it.index +1]
            stringMutable[it.index +1] = t
        }
        return stringMutable.joinToString("")
    }

    /** Generate a random number between bounds */
    private fun random(min: Long, max: Long): Long
    {
        return min + (this.rand() * ((max - min) + 1)).toLong()
    }

    /** Equivalent to Math.random using MersenneTwister int32 */
    private fun rand(): Double
    {
        return twister.genrand_int32().toDouble() / 0xffffffff
    }
}
