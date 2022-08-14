package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.LoadBalancer
import org.ibartuszek.loadbalancer.LoadBalancerImpl
import org.ibartuszek.loadbalancer.healthcheck.ProviderHealthCheckManager
import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.junit.jupiter.api.BeforeEach


abstract class AbstractLoadBalancerIT {

    companion object {
        const val MAXIMUM_NUMBER_OF_PROVIDERS = 3
        const val HEALTH_CHECK_INTERVAL = 2L
        const val NUMBER_OF_SUCCESSFUL_HEALTH_CHECKS = 2

        const val ID_1 = "id1"
        const val ID_2 = "id2"
        const val ID_3 = "id3"
        const val ID_4 = "id4"
    }

    abstract fun getSelectionStrategy(): ProviderSelectionStrategy

    protected lateinit var providerList: ProviderList
    protected lateinit var providerHealthCheckManager: ProviderHealthCheckManager
    protected lateinit var loadBalancer: LoadBalancer

    @BeforeEach
    fun setup() {
        providerList = ProviderList(
            maximumNumberOfProviders = MAXIMUM_NUMBER_OF_PROVIDERS,
            selectionStrategy = getSelectionStrategy()
        )
        providerHealthCheckManager = ProviderHealthCheckManager(
            healthCheckInterval = HEALTH_CHECK_INTERVAL,
            providerList = providerList,
            numberOfSuccessfulChecksForReactivate = NUMBER_OF_SUCCESSFUL_HEALTH_CHECKS
        )
        loadBalancer = LoadBalancerImpl(
            providerList = providerList
        )
    }

    protected fun loadFullyProviderList() {
        providerList.add(ProviderImpl(ID_1))
        providerList.add(ProviderImpl(ID_2))
        providerList.add(ProviderImpl(ID_3))
    }

}
