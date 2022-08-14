package org.ibartuszek.loadbalancer.it

import org.ibartuszek.loadbalancer.LoadBalancer
import org.ibartuszek.loadbalancer.LoadBalancerImpl
import org.ibartuszek.loadbalancer.healthcheck.HealthChecker
import org.ibartuszek.loadbalancer.provider.ProviderImpl
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import org.ibartuszek.loadbalancer.providerlist.ProviderSelectionStrategy
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


abstract class AbstractLoadBalancerIT {

    companion object {
        const val MAXIMUM_NUMBER_OF_PROVIDERS = 3
        const val HEALTH_CHECK_INTERVAL = 2L

        const val ID_1 = "id1"
        const val ID_2 = "id2"
        const val ID_3 = "id3"
        const val ID_4 = "id4"
        val defaultTime: Instant = Instant.parse("2022-08-14T12:00:00Z")
        val defaultZoneId: ZoneId = ZoneId.of("UTC")
    }

    private val clock: Clock = mock(Clock::class.java)

    abstract fun getSelectionStrategy(): ProviderSelectionStrategy

    protected lateinit var providerList: ProviderList
    protected lateinit var healthChecker: HealthChecker
    protected lateinit var loadBalancer: LoadBalancer

    @BeforeEach
    fun setup() {
        providerList = ProviderList(
            maximumNumberOfProviders = MAXIMUM_NUMBER_OF_PROVIDERS,
            selectionStrategy = getSelectionStrategy()
        )
        healthChecker = HealthChecker(
            healthCheckInterval = HEALTH_CHECK_INTERVAL,
            providerList = providerList,
            clock = clock
        )
        loadBalancer = LoadBalancerImpl(
            providerList = providerList
        )
    }

    fun setupTime() {
        `when`(clock.zone).thenReturn(defaultZoneId)
        `when`(clock.instant()).thenReturn(defaultTime)
    }

    protected fun loadFullyProviderList() {
        providerList.add(ProviderImpl(ID_1))
        providerList.add(ProviderImpl(ID_2))
        providerList.add(ProviderImpl(ID_3))
    }

}
