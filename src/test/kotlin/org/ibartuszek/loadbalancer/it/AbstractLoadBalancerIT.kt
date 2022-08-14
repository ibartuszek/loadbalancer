package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.LoadBalancer
import org.ibartuszek.loadbalancer.LoadBalancerImpl
import org.ibartuszek.loadbalancer.healthcheck.ProviderHealthCheckManager
import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


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

    private lateinit var providerList: ProviderList
    protected lateinit var providerHealthCheckManager: ProviderHealthCheckManager
    protected lateinit var loadBalancer: LoadBalancer

    protected val providerListQueue = ArrayBlockingQueue<Provider>(MAXIMUM_NUMBER_OF_PROVIDERS)
    protected val inactiveProviderMap: ConcurrentHashMap<Provider, Int> = ConcurrentHashMap()
    protected val providersToReAccept: CopyOnWriteArrayList<Provider> = CopyOnWriteArrayList()

    @BeforeEach
    fun setup() {
        providerList = ProviderList(
            maximumNumberOfProviders = MAXIMUM_NUMBER_OF_PROVIDERS,
            selectionStrategy = getSelectionStrategy(),
            queue = providerListQueue
        )
        providerHealthCheckManager = ProviderHealthCheckManager(
            healthCheckInterval = HEALTH_CHECK_INTERVAL,
            numberOfSuccessfulChecksForReactivate = NUMBER_OF_SUCCESSFUL_HEALTH_CHECKS,
            timer = Timer(),
            providerList = providerList,
            inactiveProviderMap = inactiveProviderMap,
            providersToReAccept = providersToReAccept
        )
        loadBalancer = LoadBalancerImpl(
            providerList = providerList
        )
    }

    @AfterEach
    fun clear() {
        inactiveProviderMap.clear()
        providersToReAccept.clear()
        providerListQueue.clear()
    }

    protected fun loadFullyProviderList() {
        providerListQueue.add(ProviderImpl(ID_1))
        providerListQueue.add(ProviderImpl(ID_2))
        providerListQueue.add(ProviderImpl(ID_3))
    }

}
