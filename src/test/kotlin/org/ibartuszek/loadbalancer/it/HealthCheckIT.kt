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
        `when`(provider.check()).thenReturn(false).thenReturn(false)
        providerListQueue.add(provider)
        // when
        providerHealthCheckManager.run()
        val actual = providerHealthCheckManager.inactiveProviders()
        // then
        verify(provider, times(1)).check()
        assertEquals(0, providerListQueue.size, "ProviderList should be empty!")
        assertEquals(1, actual.size, "Inactive providers should contain one element!")
        assertTrue(actual.contains(provider), "Inactive providers should contain the new element!")
    }

}
