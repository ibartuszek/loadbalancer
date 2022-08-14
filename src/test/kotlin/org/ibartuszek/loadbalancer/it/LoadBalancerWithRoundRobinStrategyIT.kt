package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.ProviderListEmptyException
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.ibartuszek.loadbalancer.providerlist.RoundRobinSelectionStrategy
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
        assertEquals(2, providerList.size(), "The size should be decreased after get method!")
    }

    @Test
    fun testGetShouldThrowExceptionWhenThereAreNoProviders() {
        assertThrows<ProviderListEmptyException> {
            // given
            // when
            loadBalancer.get()
            // then
        }
    }

}
