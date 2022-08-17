package org.ibartuszek.loadbalancer.providerlist

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class RandomSelectionStrategyTest {

    @RepeatedTest(10)
    fun testSelectIndexShouldReturnBetweenInTheSelectionRange() {
        // given
        val maximum = 10
        val underTest = RandomSelectionStrategy()
        // when
        val actual = underTest.selectIndex(10)
        // then
        assertTrue(actual >= 0, "The selected value should be bigger or equal than 0!")
        assertTrue(
            actual <= maximum,
            "The selected value should be less or equal than the maximum=$maximum, actual=$actual!"
        )
    }

    @Test
    fun testShouldRotateShouldReturnFalse() {
        assertFalse(RandomSelectionStrategy().shouldRotate())
    }

}
