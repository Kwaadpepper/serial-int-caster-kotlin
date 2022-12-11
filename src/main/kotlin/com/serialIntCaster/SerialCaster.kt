package com.serialIntCaster

import java.math.BigInteger
import kotlin.math.floor
import kotlin.math.ln

class SerialCaster {

    // Static
    companion object {

        private const val BASE10 = "0123456789"

        /**
         * Available chars for Serial generation
         */
        private var intChars: List<Char> = listOf()

        /**
         * Encode an integer using chars generating a serial of a minimum length
         *
         * Note: if seed is equal to 0 it won't be used
         */
        fun encode(number: Long, seed: Long = 0, length: Int = 6, chars: CharArray = charArrayOf()): String
        {
            val outString = StringBuilder()
            outString.append(number)
            init(number, length, chars)
            val charsCount = intChars.size.toString().padStart(2, '0')
            outString.append(charsCount)
            return this.shuffle(seed, this.convBase(
                outString.toString(), BASE10, String(intChars.toCharArray())
            ).padStart(length, intChars[0]))
        }

        /**
         * Decode an integer using chars
         *
         * Note: if seed is equal to 0 it won't be used
         */
        fun decode(serial: String, seed: Long = 0, chars: CharArray = charArrayOf()): Long
        {
            this.setChars(chars)
            val unShuffledSerial = this.unShuffle(seed, serial)
            val serialLength = unShuffledSerial.length
            var outNumber    = this.convBase(unShuffledSerial, String(intChars.toCharArray()), BASE10)
            var i = 0
            while(i < serialLength) {
                val serialChar = serial[i]
                if (!intChars.contains(serialChar)) {
                    throw SerialCasterException(
                        "SerialCaster::decode a non valid char `$serialChar` is present"
                    )
                }
                i++
            }
            if (outNumber.length < 3) {
                throw SerialCasterException("SerialCaster::decode a un valid serial code has been given")
            }
            val charsCount = outNumber.substring(outNumber.length -2, outNumber.length).toInt()
            outNumber      = outNumber.substring(0,outNumber.length -2)
            if (charsCount != intChars.size) {
                throw SerialCasterException(
                    "SerialCaster::decode the char list used for encoding and decoding is seems to be different",
                )
            }
            return outNumber.toLong()
        }

        /**
         * Initialize SerialCaster
         */
        private fun init(number: Long, length: Int, chars: CharArray) {
            this.setChars(chars)

            if (intChars.size < 2) {
                throw SerialCasterException("SerialCaster need a minimum length of 2 unique chars")
            }
            if (intChars.size > 99) {
                throw SerialCasterException(
                        "SerialCasters can have a minimum length of 99 unique chars"
                )
            }
            val minimumLength = this.calculateNewBaseLengthFromBase10(number, intChars.size) + 2
            if (length < minimumLength) {
                throw SerialCasterException("SerialCaster need a minimum length of $minimumLength")
            }
        }

        /**
         * Setup char dict
         */
        private fun setChars(chars: CharArray) {
            if (chars.isNotEmpty()) {
                // Keep a string of unique chars
                intChars = chars.distinct().sorted()
            } else {
                val init =
                        arrayOf(
                                intArrayOf('a'.code, 'z'.code),
                                intArrayOf('A'.code, 'Z'.code),
                                intArrayOf('0'.code, '9'.code)
                        )
                val list = charArrayOf().toMutableList()
                init.forEach {
                    var i = it[0]
                    while (i <= it[1]) {
                        list.add(i.toChar())
                        i++
                    }
                }
                intChars = list.toList().distinct().sorted()
            }
        }

        /**
         * Converts any number from a base to another using chars
         *
         * @param numberInput
         * @param fromBaseInput
         * @param toBaseInput
         * @return string
         * @url https://www.php.net/manual/fr/function.base-convert.php#106546
         */
        private fun convBase(numberInput: String, fromBaseInput: String, toBaseInput: String): String {
            if (fromBaseInput == toBaseInput) {
                return numberInput
            }
            val fromLen = BigInteger(fromBaseInput.length.toString())
            val toLen = BigInteger(toBaseInput.length.toString())
            val numberLen = BigInteger(numberInput.length.toString())
            if (toBaseInput == BASE10) {
                var outValue: BigInteger = BigInteger.ZERO
                for (i in 1..numberInput.length) {
                    outValue = outValue.add(
                        BigInteger(fromBaseInput.indexOf(numberInput[i - 1]).toString()).multiply(
                            fromLen.pow(
                                numberLen.subtract(BigInteger(i.toString())).intValueExact()
                            ) //pow(fromLen, numberLen.subtract(new BigInteger(""+i)))
                        )
                    )
                }
                return outValue.toString()
            }
            val base10 = if (fromBaseInput == BASE10) numberInput else convBase(numberInput, fromBaseInput, BASE10)
            if (BigInteger(base10) < toLen) {
                return toBaseInput[Integer.parseInt(base10)].toString()
            }
            var outValue = ""
            var base10bigInt = BigInteger(base10)
            while (base10bigInt != BigInteger.ZERO) {
                outValue = toBaseInput[base10bigInt.mod(toLen).intValueExact()].toString() + outValue
                base10bigInt = base10bigInt.divide(toLen)
            }
            return outValue
        }

        /**
         * If seed is different from 0, then shuffles the serial bytes using the seed
         */
        private fun shuffle(seed: Long, serial: String): String {
            if (seed == 0.toLong()) {
                return serial
            }
            val shuffledSerial = FisherYatesShuffler(seed).shuffle(serial)
            return this.rotateLeft(
                    shuffledSerial,
                    this.sumString(shuffledSerial).mod(shuffledSerial.length)
            )
        }

        /**
         * If seed is different from 0, then un shuffle the serial bytes using the seed
         */
        private fun unShuffle(seed: Long, serial: String): String {
            if (seed == 0.toLong()) {
                return serial
            }
            return FisherYatesShuffler(seed)
                    .unShuffle(this.rotateRight(serial, this.sumString(serial).mod(serial.length)))
        }

        /**
         * Calculate the length of a decimal number in a new base length
         * @url https://www.geeksforgeeks.org/given-number-n-decimal-base-find-number-digits-base-base-b/
         */
        private fun calculateNewBaseLengthFromBase10(number: Long, base: Int): Int {
            return (floor(ln(number.toDouble()) / ln(base.toDouble())) + 1).toInt()
        }

        /** Move chars in a string from left to right */
        private fun rotateLeft(string: String, distance: Int): String {
            val charList: MutableList<Char> = string.toList().toMutableList()
            var i = 0
            while (i < distance) {
                // * Take the char on index
                val char = charList.removeAt(0)
                // * Push at the end
                charList.add(char)
                i++
            }
            return String(charList.toCharArray())
        }

        /** Move chars in a string from right to left */
        private fun rotateRight(string: String, distance: Int): String {
            val charList: MutableList<Char> = string.toList().toMutableList()
            var i = 0
            while (i < distance) {
                // * Pop le dernier char
                val lastChar = charList.removeLast()

                // * Push on top
                charList.add(0, lastChar)
                i++
            }
            return String(charList.toCharArray())
        }

        /**
         * Sum all string chars values
         */
        private fun sumString(string: String): Int {
            var o = 0
            var i = string.length - 1
            while (i >= 0) {
                o += string[i].code
                i--
            }
            return o
        }
    }
}
