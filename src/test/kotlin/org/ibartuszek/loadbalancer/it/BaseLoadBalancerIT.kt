package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.ibartuszek.loadbalancer.providerlist.RandomSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


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
        assertEquals(provider, providerList.poll(), "The provider should be present in the list!")
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
        assertEquals(MAXIMUM_NUMBER_OF_PROVIDERS, providerList.size(), "The list should have maximum size!")
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
        assertEquals(2, providerList.size(), "The size should be decreased after get method!")
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
        assertEquals(MAXIMUM_NUMBER_OF_PROVIDERS, providerList.size(), "The list should have maximum size!")
    }

    @Test
    fun testHealthCheckShouldRemoveProviderFromProviderListAndAddInactiveProviders() {
        // given
        setupTime()
        val provider = mock(Provider::class.java)
        `when`(provider.checkHealth()).thenReturn(false)
        // `when`(provider.get()).thenReturn(ID_1)
        providerList.add(provider)
        // when
        healthChecker.run()
        val actual = healthChecker.inactiveProviders()
        // then
        verify(provider).checkHealth()
        assertEquals(0, providerList.size())
        assertEquals(1, actual.size)
        assertTrue(actual.contains(provider))
    }

}
