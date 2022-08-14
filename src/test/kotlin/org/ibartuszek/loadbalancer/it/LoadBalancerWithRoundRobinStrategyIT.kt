package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.ProviderCapacityLimitException
import org.ibartuszek.loadbalancer.ProviderListEmptyException
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.ibartuszek.loadbalancer.providerlist.RoundRobinSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
class LoadBalancerWithRoundRobinStrategyIT : AbstractLoadBalancerIT() {

    private val selectionStrategy = RoundRobinSelectionStrategy()

    override fun getSelectionStrategy(): ProviderSelectionStrategy = selectionStrategy

    @Test
    fun testGetShouldReturnAProviderId() {
        // given
        loadFullyProviderList()
        // when
        val actual = loadBalancer.get()
        // then
        assertEquals(ID_1, actual, "The loadBalancer should return the id of the first provider!")
        assertEquals(2, providerListQueue.size, "The size should be decreased after get method!")
        assertEquals(0L, activeRequests.get())
    }

    @Test
    fun testGetShouldThrowProviderListEmptyExceptionWhenThereAreNoProviders() {
        assertThrows<ProviderListEmptyException> {
            // given
            // when
            loadBalancer.get()
            // then
            assertEquals(0L, activeRequests.get())
        }
    }

    @Test
    fun testGetShouldThrowProviderCapacityLimitExceptionWhenThereAreNoProviders() {
        assertThrows<ProviderCapacityLimitException> {
            // given
            val activeRequestCount = MAXIMUM_NUMBER_OF_PROVIDERS * MAXIMUM_REQUEST_PER_PROVIDER.toLong()
            activeRequests.set(activeRequestCount)
            // when
            loadBalancer.get()
            // then
            assertEquals(activeRequestCount, activeRequests.get())
        }
    }

}
