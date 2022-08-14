package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.LoadBalancer
import org.ibartuszek.loadbalancer.LoadBalancerImpl
import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.junit.jupiter.api.BeforeEach


abstract class AbstractLoadBalancerIT {

    companion object {
        const val ID_1 = "id1"
        const val ID_2 = "id2"
        const val ID_3 = "id3"
        const val ID_4 = "id4"
        const val MAXIMUM_NUMBER_OF_PROVIDERS = 3
    }

    abstract fun getSelectionStrategy(): ProviderSelectionStrategy

    protected lateinit var providerList: ProviderList
    protected lateinit var loadBalancer: LoadBalancer

    @BeforeEach
    fun setup() {
        providerList = ProviderList(
            maximumNumberOfProviders = MAXIMUM_NUMBER_OF_PROVIDERS,
            selectionStrategy = getSelectionStrategy()
        )
        loadBalancer = LoadBalancerImpl(providerList)
    }

    protected fun loadFullyProviderList() {
        providerList.add(ProviderImpl(ID_1))
        providerList.add(ProviderImpl(ID_2))
        providerList.add(ProviderImpl(ID_3))
    }

}
