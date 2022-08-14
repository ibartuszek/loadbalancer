package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.ProviderListEmptyException
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
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
        assertEquals(ID_1, actual, "The loadBalancer should return the id of the first provider!")
        assertEquals(2, providerListQueue.size, "The size should be decreased after get method!")
        verify(selectionStrategy).selectIndex(MAXIMUM_NUMBER_OF_PROVIDERS - 1)
    }

    @Test
    fun testGetShouldThrowExceptionWhenThereAreNoProviders() {
        assertThrows<ProviderListEmptyException> {
            // given
            // when
            loadBalancer.get()
            // then
            verify(selectionStrategy, never()).selectIndex(MAXIMUM_NUMBER_OF_PROVIDERS)
        }
    }

}
