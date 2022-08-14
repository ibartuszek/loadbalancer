package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.ibartuszek.loadbalancer.providerlist.RandomSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class BaseLoadBalancerIT : AbstractLoadBalancerIT() {

    private val selectionStrategy = RandomSelectionStrategy()

    override fun getSelectionStrategy(): ProviderSelectionStrategy = selectionStrategy

    @Test
    fun testAcceptShouldPutTheProviderIntoProverList() {
        // given
        val provider = ProviderImpl(ID_1)
        // when
        val actual = loadBalancer.accept(provider)
        // then
        assertTrue(actual, "The loadBalancer should accept provider!")
        assertEquals(provider, providerListQueue.poll(), "The provider should be present in the list!")
    }

    @Test
    fun testAcceptShouldRejectTheProviderWhenProverListIsFull() {
        // given
        val provider = ProviderImpl(ID_4)
        loadFullyProviderList()
        // when
        val actual = loadBalancer.accept(provider)
        // then
        assertFalse(actual, "The loadBalancer should accept provider!")
        assertEquals(MAXIMUM_NUMBER_OF_PROVIDERS, providerListQueue.size, "The list should have maximum size!")
    }

    @Test
    fun testExcludeShouldRemoveTheProviderFromProverList() {
        // given
        loadFullyProviderList()
        val provider = ProviderImpl(ID_2)
        // when
        val actual = loadBalancer.exclude(provider)
        // then
        assertTrue(actual, "The loadBalancer should remove the provider!")
        assertEquals(2, providerListQueue.size, "The size should be decreased after get method!")
    }

    @Test
    fun testExcludeShouldReturnFalseWhenTheProverListDoesNotContainTheProvider() {
        // given
        val provider = ProviderImpl(ID_4)
        loadFullyProviderList()
        // when
        val actual = loadBalancer.exclude(provider)
        // then
        assertFalse(actual, "The providerList does not contain provider!")
        assertEquals(MAXIMUM_NUMBER_OF_PROVIDERS, providerListQueue.size, "The list should have maximum size!")
    }

}
