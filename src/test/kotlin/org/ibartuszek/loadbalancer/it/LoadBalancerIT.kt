package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.LoadBalancer
import org.ibartuszek.loadbalancer.LoadBalancerImpl
import org.ibartuszek.loadbalancer.ProviderListEmptyException
import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
class LoadBalancerIT {

    companion object {
        private const val ID_1 = "id1"
        private const val ID_2 = "id2"
        private const val ID_3 = "id3"
        private const val ID_4 = "id4"
        private const val MAXIMUM_NUMBER_OF_PROVIDERS = 3
    }

    @Mock
    private lateinit var selectionStrategy: ProviderSelectionStrategy
    private lateinit var providerList: ProviderList
    private lateinit var loadBalancer: LoadBalancer

    @BeforeEach
    fun setup() {
        providerList = ProviderList(
            maximumNumberOfProviders = MAXIMUM_NUMBER_OF_PROVIDERS,
            selectionStrategy = selectionStrategy
        )
        loadBalancer = LoadBalancerImpl(providerList)
    }

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
    fun testGetShouldReturnAProviderId() {
        // given
        loadFullyProviderList()
        `when`(selectionStrategy.selectIndex(MAXIMUM_NUMBER_OF_PROVIDERS)).thenReturn(0)
        // when
        val actual = loadBalancer.get()
        // then
        assertEquals(ID_1, actual, "The loadBalancer should return the id of the first provider!")
        assertEquals(2, providerList.size(), "The size should be decreased after get method!")
        verify(selectionStrategy).selectIndex(MAXIMUM_NUMBER_OF_PROVIDERS)
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

    private fun loadFullyProviderList() {
        providerList.add(ProviderImpl(ID_1))
        providerList.add(ProviderImpl(ID_2))
        providerList.add(ProviderImpl(ID_3))
    }

}
