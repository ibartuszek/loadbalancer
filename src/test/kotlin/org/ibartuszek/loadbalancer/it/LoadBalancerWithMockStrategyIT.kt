package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.ProviderRequestCapacityLimitException
import org.ibartuszek.loadbalancer.ProviderListEmptyException
import org.ibartuszek.loadbalancer.provider.ProviderImpl
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
        `when`(selectionStrategy.shouldRotate()).thenReturn(false)
        // when
        val actual = loadBalancer.get()
        // then
        verify(selectionStrategy).selectIndex(MAXIMUM_NUMBER_OF_PROVIDERS - 1)
        verify(selectionStrategy).shouldRotate()
        assertEquals(ID_1, actual, "The loadBalancer should return the id of the first provider!")
        assertEquals(3, providerListQueue.size, "The size should be the same after the request!")
        assertEquals(ID_1, providerListQueue.elementAt(0).get(), "The providers order should not change!")
        assertEquals(ID_2, providerListQueue.elementAt(1).get(), "The providers order should not change!")
        assertEquals(ID_3, providerListQueue.elementAt(2).get(), "The providers order should not change!")
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
    fun testGetShouldThrowProviderRequestCapacityLimitExceptionWhenThereAreNoProviders() {
        assertThrows<ProviderRequestCapacityLimitException> {
            // given
            providerListQueue.add(ProviderImpl(ID_1))
            providerListQueue.add(ProviderImpl(ID_2))
            val activeRequestCount = providerListQueue.size * MAXIMUM_REQUEST_PER_PROVIDER.toLong()
            activeRequests.set(activeRequestCount)
            // when
            loadBalancer.get()
            // then
            assertEquals(activeRequestCount, activeRequests.get())
        }
    }

}
