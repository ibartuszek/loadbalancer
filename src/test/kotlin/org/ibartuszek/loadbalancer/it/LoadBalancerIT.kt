package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.LoadBalancerImpl
import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LoadBalancerIT {

    companion object {
        private const val ID_1 = "id1"
        private const val ID_2 = "id2"
        private const val ID_3 = "id3"
        private const val ID_4 = "id4"
        private const val MAXIMUM_NUMBER_OF_PROVIDERS = 3
    }

    @Test
    fun testAcceptShouldPutTheProviderIntoProverList() {
        // given
        val provider = ProviderImpl(ID_1)
        val providerList = ProviderList(MAXIMUM_NUMBER_OF_PROVIDERS)
        val loadBalancer = LoadBalancerImpl(providerList)
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
        val providerList = createFullyLoadedProviderList()
        val loadBalancer = LoadBalancerImpl(providerList)
        // when
        val actual = loadBalancer.accept(provider)
        // then
        assertFalse(actual, "The loadBalancer should accept provider!")
        assertEquals(MAXIMUM_NUMBER_OF_PROVIDERS, providerList.size(), "The list should have maximum size!")
    }

    @Test
    fun testGetShouldReturnAProviderId() {
        // given
        val providerList = createFullyLoadedProviderList()
        val loadBalancer = LoadBalancerImpl(providerList)
        // when
        val actual = loadBalancer.get()
        // then
        assertEquals(ID_1, actual, "The loadBalancer should return the id of the first provider!")
        assertEquals(2, providerList.size(), "The size should be decreased after get method!")
    }

    private fun createFullyLoadedProviderList(): ProviderList {
        val providerList = ProviderList(MAXIMUM_NUMBER_OF_PROVIDERS)
        providerList.add(ProviderImpl(ID_1))
        providerList.add(ProviderImpl(ID_2))
        providerList.add(ProviderImpl(ID_3))
        return providerList
    }

}
