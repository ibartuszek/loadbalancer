package org.ibartuszek.loadbalancer.healthcheck

import mu.KotlinLogging
import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import java.time.Clock
import java.time.Instant
import java.util.*

class HealthChecker(
    private val healthCheckInterval: Long,
    private val providerList: ProviderList,
    private val clock: Clock
): TimerTask() {

    private val logger = KotlinLogging.logger { }
    private val timer = Timer()
    private val inactiveProviderMap = mutableMapOf <Provider, Instant>()

    fun start() {
        timer.schedule(this, healthCheckInterval)
    }

    override fun run() {
        val newInactiveProviderMap = providerList.checkHealth().filter { !it.value }.mapValues {  Instant.now(clock) }
        providerList.exclude(newInactiveProviderMap.keys)
        newInactiveProviderMap.forEach { (k, v) -> inactiveProviderMap.merge(k, v) { oldVal, _ -> oldVal} }
        logger.info { "Inactive providers: $inactiveProviderMap" }
    }

    fun inactiveProviders() = inactiveProviderMap.toMap()

}
