/*
 *  疑似乱数生成機  移植
 *
 *  Mersenne Twister with improved initialization (2002)
 *  http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/mt.html
 *  http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/MT2002/mt19937ar.html
 */
// = 移植元ラインセンス =======================================================
// ======================================================================
/*
A C-program for MT19937, with initialization improved 2002/2/10.
Coded by Takuji Nishimura and Makoto Matsumoto.
This is a faster version by taking Shawn Cokus's optimization,
Matthe Bellew's simplification, Isaku Wada's real version.
Before using, initialize the state by using init_genrand(seed)
or init_by_array(init_key, key_length).
Copyright (C) 1997 - 2002, Makoto Matsumoto and Takuji Nishimura,
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The names of its contributors may not be used to endorse or promote
products derived from this software without specific prior written
permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
Any feedback is very welcome.
http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html
email: m-mat @ math.sci.hiroshima-u.ac.jp (remove space)
*/
// ======================================================================
package mt19937ar

class MersenneTwister {
    /*
     * the array for the
     * state vector
     */
    private val state = LongArray(N)
    private var left = 1
    private var initf = 0
    private var next = 0
    var seed: Long = 0
        private set

    @JvmOverloads
    constructor(seed: Long = 0) {
        init_genrand(seed)
    }

    @JvmOverloads
    constructor(init_key: LongArray, key_length: Int = init_key.size) {
        init_by_array(init_key, key_length)
    }

    /* initializes state[N] with a seed */
    fun init_genrand(seed: Long) {
        this.seed = seed and 0xffffffffL
        state[0] = seed and 0xffffffffL
        for (j in 1 until N) {
            state[j] = 1812433253L * (state[j - 1] xor (state[j - 1] shr 30)) + j
            /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
            /* In the previous versions, MSBs of the seed affect */
            /* only MSBs of the array state[]. */
            /* 2002/01/09 modified by Makoto Matsumoto */ state[j] =
                    state[j] and 0xffffffffL /* for >32 bit machines */
        }
        left = 1
        initf = 1
    }

    /* initialize by an array with array-length */
    /* init_key is the array for initializing keys */
    /* key_length is its length */
    /* slight change for C++, 2004/2/26 */
    @JvmOverloads
    fun init_by_array(init_key: LongArray, key_length: Int = init_key.size) {
        var i: Int
        var j: Int
        var k: Int
        init_genrand(19650218L)
        i = 1
        j = 0
        k = if (N > key_length) N else key_length
        while (k > 0) {
            state[i] =
                    ((state[i] xor (state[i - 1] xor (state[i - 1] shr 30)) * 1664525L) +
                            init_key[j] +
                            j) /* non linear */
            state[i] = state[i] and 0xffffffffL /* for WORDSIZE > 32 machines */
            i++
            j++
            if (i >= N) {
                state[0] = state[N - 1]
                i = 1
            }
            if (j >= key_length) j = 0
            k--
        }
        k = N - 1
        while (k > 0) {
            state[i] =
                    ((state[i] xor (state[i - 1] xor (state[i - 1] shr 30)) * 1566083941L) -
                            i) /* non linear */
            state[i] = state[i] and 0xffffffffL /* for WORDSIZE > 32 machines */
            i++
            if (i >= N) {
                state[0] = state[N - 1]
                i = 1
            }
            k--
        }
        state[0] = 0x80000000L /* MSB is 1; assuring non-zero initial array */
        left = 1
        initf = 1
    }

    fun next_state() {
        var p = 0
        var j: Int

        /* if init_genrand() has not been called, */
        /* a default initial seed is used */ if (initf == 0) init_genrand(5489L)
        left = N
        next = 0
        j = N - M + 1
        while (--j > 0) {
            state[p] = (state[p + M] xor twist(state[p], state[p + 1]))
            p++
        }
        j = M
        while (--j > 0) {
            state[p] = (state[p + M - N] xor twist(state[p], state[p + 1]))
            p++
        }
        state[p] = (state[p + M - N] xor twist(state[p], state[0]))
    }

    /* generates a random number on [0,0xffffffff]-interval */
    fun genrand_int32(): Long {
        var y: Long
        if (--left == 0) next_state()
        y = state[next++]

        /* Tempering */ y = y xor (y shr 11)
        y = y xor (y shl 7 and 0x9d2c5680L)
        y = y xor (y shl 15 and 0xefc60000L)
        y = y xor (y shr 18)
        return y
    }

    /* generates a random number on [0,0x7fffffff]-interval */
    fun genrand_int31(): Long {
        var y: Long
        if (--left == 0) next_state()
        y = state[next++]

        /* Tempering */ y = y xor (y shr 11)
        y = y xor (y shl 7 and 0x9d2c5680L)
        y = y xor (y shl 15 and 0xefc60000L)
        y = y xor (y shr 18)
        return (y shr 1)
    }

    /* generates a random number on [0,1]-real-interval */
    fun genrand_real1(): Double {
        var y: Long
        if (--left == 0) next_state()
        y = state[next++]

        /* Tempering */ y = y xor (y shr 11)
        y = y xor (y shl 7 and 0x9d2c5680L)
        y = y xor (y shl 15 and 0xefc60000L)
        y = y xor (y shr 18)
        return y.toDouble() * (1.0 / 4294967295.0)
        /* divided by 2^32-1 */
    }

    /* generates a random number on [0,1)-real-interval */
    fun genrand_real2(): Double {
        var y: Long
        if (--left == 0) next_state()
        y = state[next++]

        /* Tempering */ y = y xor (y shr 11)
        y = y xor (y shl 7 and 0x9d2c5680L)
        y = y xor (y shl 15 and 0xefc60000L)
        y = y xor (y shr 18)
        return y.toDouble() * (1.0 / 4294967296.0)
        /* divided by 2^32 */
    }

    /* generates a random number on (0,1)-real-interval */
    fun genrand_real3(): Double {
        var y: Long
        if (--left == 0) next_state()
        y = state[next++]

        /* Tempering */ y = y xor (y shr 11)
        y = y xor (y shl 7 and 0x9d2c5680L)
        y = y xor (y shl 15 and 0xefc60000L)
        y = y xor (y shr 18)
        return (y.toDouble() + 0.5) * (1.0 / 4294967296.0)
        /* divided by 2^32 */
    }

    /* generates a random number on [0,1) with 53-bit resolution */
    fun genrand_res53(): Double {
        val a = genrand_int32() shr 5
        val b = genrand_int32() shr 6
        return (a * 67108864.0 + b) * (1.0 / 9007199254740992.0)
    } /* These real versions are due to Isaku Wada, 2002/01/09 added */

    companion object {
        /* Period parameters */
        private const val N = 624
        private const val M = 397
        private const val MATRIX_A = 0x9908b0dfL /* constant vector a */
        private const val UMASK = 0x80000000L /* most significant w-r bits */
        private const val LMASK = 0x7fffffffL /* least significant r bits */
        private fun mixBits(u: Long, v: Long): Long {
            return u and UMASK or (v and LMASK)
        }

        private fun twist(u: Long, v: Long): Long {
            return (mixBits(u, v) shr 1 xor if (v and 1 == 1L) MATRIX_A else 0)
        }
    }
}
