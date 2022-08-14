package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.ibartuszek.loadbalancer.providerlist.RandomSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


class HealthCheckIT : AbstractLoadBalancerIT() {

    private val selectionStrategy = RandomSelectionStrategy()

    override fun getSelectionStrategy(): ProviderSelectionStrategy = selectionStrategy

    @Test
    fun testHealthCheckShouldRemoveProviderFromProviderListAndAddInactiveProviders() {
        // given
        val provider = mock(Provider::class.java)
        `when`(provider.check()).thenReturn(false)
        providerListQueue.add(provider)
        // when
        providerHealthCheckManager.run()
        // then
        verify(provider).check()
        assertEquals(0, providerListQueue.size, "ProviderList should be empty!")
        assertEquals(1, inactiveProviderMap.size, "Inactive providers should contain one element!")
        assertTrue(inactiveProviderMap.containsKey(provider), "Inactive providers should contain the new element!")
        assertTrue(inactiveProviderMap[provider] == 0, "Inactive provider should have 0 counter!")
    }

    @Test
    fun testHealthCheckShouldIncreaseTheCounterAfterSuccessfulHealthCheck() {
        // given
        val provider = mock(Provider::class.java)
        `when`(provider.check()).thenReturn(true)
        inactiveProviderMap[provider] = 0

        // when
        providerHealthCheckManager.run()
        // then
        verify(provider).check()
        assertEquals(0, providerListQueue.size, "ProviderList still should be empty!")
        assertEquals(1, inactiveProviderMap.size, "One inactive provider should be there!")
        assertTrue(inactiveProviderMap.containsKey(provider), "Inactive providers should contain the new element!")
        assertTrue(inactiveProviderMap[provider]!! == 1, "After the first alive signal the counter should be 1!")
        assertEquals(0, providersToReAccept.size, "The actual providers to re-accept should be empty!")
    }

    @Test
    fun testHealthCheckShouldReAcceptProviderAfterSecondSuccessfulHealthCheck() {
        // given
        val provider = mock(Provider::class.java)
        `when`(provider.check()).thenReturn(true).thenReturn(true)
        inactiveProviderMap[provider] = 1

        // when
        providerHealthCheckManager.run()
        // then
        verify(provider, times(2)).check() // Second is coming from the inactive provider check in the providerList
        assertEquals(1, providerListQueue.size, "ProviderList should have one provider!")
        assertTrue(providerListQueue.contains(provider), "ProviderList should contain the provider!")
        assertEquals(0, inactiveProviderMap.size, "Inactive providers map should be empty!")
        assertEquals(0, providersToReAccept.size, "The actual providers to re-accept should be empty (Again)!")
    }

}
