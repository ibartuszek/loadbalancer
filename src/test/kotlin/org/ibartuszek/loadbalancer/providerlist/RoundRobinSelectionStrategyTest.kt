package org.ibartuszek.loadbalancer.providerlist

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RoundRobinSelectionStrategyTest {

    @Test
    fun testSelectIndexShouldReturnFirstIndex() {
        // given
        val underTest = RoundRobinSelectionStrategy()
        // when
        val actual = underTest.selectIndex(3)
        // then
        assertEquals(0, actual, "Round Robin should return always the first index!")
    }

}