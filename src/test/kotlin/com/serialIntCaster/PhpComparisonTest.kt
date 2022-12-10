package com.serialIntCaster

import org.junit.Assume
import org.junit.Assume.assumeThat
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random
import kotlin.test.*

class PhpComparisonTest {
    companion object {
        private const val ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        private const val LENGTH       = 10
        private const val MAX_INTEGER  = 99999999999
    }

    /**
     * Test php generated serials
     */
    @Test fun testPhpGeneratedFile()
    {
        val kotlinFileName = "serialTestFileKotlin.csv"
        val file = File(kotlinFileName)

        Assume.assumeTrue(
            "Missing kotlin file `{$kotlinFileName}`, this test will be skipped",
            file.exists()
        )

        if (!file.canRead()) {
            throw RuntimeException("Could not read `{}` file");
        }

        // * Read CSV
        val cvsContent = file.readLines().map { it.split(";") }
        val header   = arrayOf("seed", "length", "dict")
        val suheader = arrayOf("integer", "encoded")

        // * Check Header
        assertTrue(
            header.toList().minus(cvsContent[0].toSet()).isEmpty(),
            "Array structure is invalid expected $header"
        );

        // * Check Header values
        assertTrue(
      cvsContent[1].size == 3 &&
            cvsContent[1][0].all { char -> char.isDigit() } &&
            cvsContent[1][1].toIntOrNull() == LENGTH && cvsContent[1][2] == ALPHANUMERIC,
    "Array Header values should be like " + arrayOf(
                "integer value", LENGTH, ALPHANUMERIC
            )
        )

        // * Check subheader
        assertTrue(
            suheader.toList().minus(cvsContent[2].toSet()).isEmpty(),
            "Array structure is invalid expected $suheader"
        )

        // * Check rows
        val size = cvsContent.size
        assertTrue(size - 3 > 0, "There should be line to check in the file")

        for (i in  3 until size -1) {
           assertTrue(0 in cvsContent[i].indices, "The row should have an integer")
            assertTrue(cvsContent[i][0].all { char -> char.isDigit() }, "The row should have an integer")
            assertTrue(1 in cvsContent[i].indices, "The row should have a serial")
            assertTrue(cvsContent[i][1].isNotEmpty(), "The row should have a serial")
        }
        // * Run encode and decode tests
        val seed = cvsContent[1][0].toLong()
        for (i in 3 until size-1) {
            val integer = cvsContent[i][0].toLong()
            val serial  = cvsContent[i][1]

            val testEncode = SerialCaster.encode(integer, seed, LENGTH, ALPHANUMERIC.toCharArray())
            val testDecode = SerialCaster.decode(serial, seed, ALPHANUMERIC.toCharArray())
            assertEquals(serial, testEncode, "Encode `{$integer}` should give `{$serial}`, got `{$testEncode}`")
            assertEquals(integer, testDecode, "Decode `{$serial}` should give `{$integer}`, got `{$testDecode}`");
        }
    }

    /**
     * Generate test list for Php usage
     */
    @Test fun generatePhpTestList()
    {
        val lines_to_genenerate = 9999

        if (lines_to_genenerate < 1) {
            throw RuntimeException(
                "%s::%s requires an argument \\'lines\\' with a positive number above 0.".format(
                    PhpComparisonTest::class.java.simpleName,
                    object{}::class.java.enclosingMethod.name
                )
            );
        }

        val seed          = System.nanoTime()
        val csv_rows      =  arrayOf<Array<String>>(
            arrayOf("seed", "length", "dict"),
            arrayOf(seed.toString(), LENGTH.toString(), ALPHANUMERIC),
            arrayOf("integer", "encoded"),
        ).toMutableList()
        val header_offset = csv_rows.size

        // * Since Serial Caster also use mt_rand we will first inject our integer list.
        // * Generate list of integers (usage unaltered of mt_rand).
        val rand = Random(seed)
        for(i in lines_to_genenerate downTo  1) {
            val integer = rand.nextInt(1, MAX_INTEGER.toInt())
            csv_rows.add(arrayOf(integer.toString(), ""))
        }

        // * Insert encoded column (usage unaltered of mt_rand).

        var i = header_offset
        while (i < (lines_to_genenerate + header_offset)) {
            /** @var integer $seed Our previous seed integer. */
            val integer          = csv_rows[i][0].toString().toLong()
            csv_rows[i][1] = SerialCaster.encode(integer, seed, LENGTH, ALPHANUMERIC.toCharArray())
            i++
        }

        // * Print into file.
        val filename = "serialTestFileKotlin.csv"
        val file = File(filename)
        if(file.exists()) {
            file.delete()
        }
        file.createNewFile()
        if (!file.canWrite()) {
            throw RuntimeException("Failed to open file `$filename` for writing");
        }

        // * Empty file
        val stream = FileOutputStream(file, false)
        stream.write("".toByteArray())
        stream.close()

        for(csv_row in csv_rows) {
            file.appendText(csv_row.joinToString(";") + "\n")
        }
        file.appendText("\n")
    }
}