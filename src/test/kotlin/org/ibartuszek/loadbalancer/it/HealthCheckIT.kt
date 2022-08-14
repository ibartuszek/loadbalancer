package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.ibartuszek.loadbalancer.providerlist.RandomSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


class HealthCheckIT : AbstractLoadBalancerIT() {

    private val selectionStrategy = RandomSelectionStrategy()

    override fun getSelectionStrategy(): ProviderSelectionStrategy = selectionStrategy

    @Test
    fun testHealthCheckShouldRemoveProviderFromProviderListAndAddInactiveProviders() {
        // given
        val provider = mock(Provider::class.java)
        `when`(provider.checkHealth()).thenReturn(false)
        providerList.add(provider)
        // when
        providerHealthCheckManager.run()
        val actual = providerHealthCheckManager.inactiveProviders()
        // then
        verify(provider).checkHealth()
        assertEquals(0, providerList.size())
        assertEquals(1, actual.size)
        assertTrue(actual.contains(provider))
    }

}
