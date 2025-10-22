package io.github.kwaadpepper.serialintcaster

import kotlin.random.Random
import kotlin.test.*

class FisherYatesShufflerTest {

    /** Test can shuffle string */
    @Test
    fun testShuffle() {
        val shuffler = FisherYatesShuffler(1492)
        val string = "I love donuts"
        for (i in 99 downTo 0) {
            shuffler.shuffle(string)
        }
        assertTrue(true)
    }

    /** Test can unShuffle string */
    @Test
    fun testUnShuffle() {
        val shuffler = FisherYatesShuffler(1492)
        val string = "uv otsId lnoe"
        for (i in 99 downTo 0) {
            shuffler.unShuffle(string)
        }
        assertTrue(true)
    }

    /** Test can retrieve shuffled string */
    @Test
    fun testRetrieveUnShuffle() {
        for (i in 999 downTo 0) {
            val oldString = this.generateRandomString()
            var string = oldString
            val shuffler = FisherYatesShuffler(Random.nextLong(0, 9999999))
            string = shuffler.shuffle(string)
            string = shuffler.unShuffle(string)
            assertEquals(oldString, string)
        }
    }

    /**
     * Generates a random string
     * @url https://stackoverflow.com/questions/4356289/php-random-string-generator
     */
    private fun generateRandomString(length: Int = 10): String {
        val characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val charactersLength = characters.length
        val randomString = StringBuilder()
        var i = 0
        while (i < length) {
            randomString.append(characters[Random.nextInt(0, charactersLength - 1)])
            i++
        }
        return randomString.toString()
    }
}
