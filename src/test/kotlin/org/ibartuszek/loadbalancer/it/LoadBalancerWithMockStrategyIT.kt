package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.ProviderCapacityLimitException
import org.ibartuszek.loadbalancer.ProviderListEmptyException
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
class LoadBalancerWithMockStrategyIT : AbstractLoadBalancerIT() {

    @Mock
    private lateinit var selectionStrategy: ProviderSelectionStrategy

    override fun getSelectionStrategy(): ProviderSelectionStrategy = selectionStrategy

    @Test
    fun testGetShouldReturnAProviderId() {
        // given
        loadFullyProviderList()
        `when`(selectionStrategy.selectIndex(MAXIMUM_NUMBER_OF_PROVIDERS - 1)).thenReturn(0)
        // when
        val actual = loadBalancer.get()
        // then
        verify(selectionStrategy).selectIndex(MAXIMUM_NUMBER_OF_PROVIDERS - 1)
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
