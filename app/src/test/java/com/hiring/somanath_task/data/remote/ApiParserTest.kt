package com.hiring.somanath_task.data.remote

import com.hiring.somanath_task.util.logging.TestLogger
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ApiParserTest {

    private lateinit var parser: ApiParser
    private lateinit var testLogger: TestLogger

    @Before
    fun setup() {
        testLogger = TestLogger()
        parser = ApiParser(testLogger)
    }

    @Test
    fun `parse valid holdings json successfully`() {
        val validJson = """
        {
          "data": {
            "userHolding": [
              {
                "symbol": "MAHABANK",
                "quantity": 990,
                "ltp": 38.05,
                "avgPrice": 35,
                "close": 40
              },
              {
                "symbol": "ICICI",
                "quantity": 100,
                "ltp": 118.25,
                "avgPrice": 110,
                "close": 105
              }
            ]
          }
        }
        """.trimIndent()

        val holdings = parser.parseHoldingsJson(validJson)

        Assert.assertEquals(2, holdings.size)
        Assert.assertEquals("MAHABANK", holdings[0].symbol)
        Assert.assertEquals(990, holdings[0].quantity)
        Assert.assertEquals(38.05, holdings[0].ltp, 0.001)
        Assert.assertEquals(35.0, holdings[0].avgPrice, 0.001)
        Assert.assertEquals(40.0, holdings[0].close, 0.001)
    }


    @Test(expected = IllegalArgumentException::class)
    fun `parse empty json throws exception`() {
        parser.parseHoldingsJson("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `parse invalid json structure throws exception`() {
        val invalidJson = """
        {
          "wrong": {
            "structure": []
          }
        }
        """.trimIndent()

        parser.parseHoldingsJson(invalidJson)
    }

    @Test
    fun `parse json with missing data field throws exception`() {
        val invalidJson = """
        {
          "not_data": {
            "userHolding": []
          }
        }
        """.trimIndent()

        try {
            parser.parseHoldingsJson(invalidJson)
            Assert.fail("Should have thrown exception")
        } catch (e: IllegalArgumentException) {
            Assert.assertTrue(e.message!!.contains("Failed to parse holdings"))
        }
    }

    @Test
    fun `parse holding with negative quantity filters invalid holding`() {
        val jsonWithInvalidHolding = """
        {
          "data": {
            "userHolding": [
              {
                "symbol": "VALID",
                "quantity": 100,
                "ltp": 50.0,
                "avgPrice": 45.0,
                "close": 55.0
              },
              {
                "symbol": "INVALID",
                "quantity": -100,
                "ltp": 50.0,
                "avgPrice": 45.0,
                "close": 55.0
              }
            ]
          }
        }
        """.trimIndent()

        val holdings = parser.parseHoldingsJson(jsonWithInvalidHolding)

        // Should only parse the valid holding
        Assert.assertEquals(1, holdings.size)
        Assert.assertEquals("VALID", holdings[0].symbol)
    }

    @Test
    fun `parse holding with blank symbol filters invalid holding`() {
        val jsonWithInvalidHolding = """
        {
          "data": {
            "userHolding": [
              {
                "symbol": "VALID",
                "quantity": 100,
                "ltp": 50.0,
                "avgPrice": 45.0,
                "close": 55.0
              },
              {
                "symbol": "",
                "quantity": 100,
                "ltp": 50.0,
                "avgPrice": 45.0,
                "close": 55.0
              }
            ]
          }
        }
        """.trimIndent()

        val holdings = parser.parseHoldingsJson(jsonWithInvalidHolding)

        // Should only parse the valid holding
        Assert.assertEquals(1, holdings.size)
        Assert.assertEquals("VALID", holdings[0].symbol)
    }

    @Test
    fun `parse empty holdings array throws exception`() {
        val emptyJson = """
        {
          "data": {
            "userHolding": []
          }
        }
        """.trimIndent()

        try {
            parser.parseHoldingsJson(emptyJson)
            Assert.fail("Should have thrown exception")
        } catch (e: IllegalArgumentException) {
            Assert.assertTrue(e.message!!.contains("No valid holdings found"))
        }
    }

    @Test
    fun `parse real api response sample`() {
        val realJson = """
        {
          "data": {
            "userHolding": [
              {
                "symbol": "MAHABANK",
                "quantity": 990,
                "ltp": 38.05,
                "avgPrice": 35,
                "close": 40
              },
              {
                "symbol": "ICICI",
                "quantity": 100,
                "ltp": 118.25,
                "avgPrice": 110,
                "close": 105
              },
              {
                "symbol": "SBI",
                "quantity": 150,
                "ltp": 550.05,
                "avgPrice": 501,
                "close": 590
              }
            ]
          }
        }
        """.trimIndent()

        val holdings = parser.parseHoldingsJson(realJson)

        Assert.assertEquals(3, holdings.size)
        Assert.assertEquals("MAHABANK", holdings[0].symbol)
        Assert.assertEquals("ICICI", holdings[1].symbol)
        Assert.assertEquals("SBI", holdings[2].symbol)

        // Verify calculations would work
        val totalQuantity = holdings.sumOf { it.quantity }
        Assert.assertEquals(1240, totalQuantity)
    }
}