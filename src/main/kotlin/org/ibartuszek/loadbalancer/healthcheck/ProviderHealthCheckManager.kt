package org.ibartuszek.loadbalancer.healthcheck

import mu.KotlinLogging
import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ProviderHealthCheckManager(
    private val healthCheckInterval: Long,
    private val numberOfSuccessfulChecksForReactivate: Int,
    private val timer: Timer,
    private val providerList: ProviderList,
    private val inactiveProviderMap: ConcurrentHashMap<Provider, Int>,
    private val providersToReAccept: CopyOnWriteArrayList<Provider>
) : TimerTask() {

    private val logger = KotlinLogging.logger { }

    fun start() {
        timer.schedule(this, healthCheckInterval)
    }

    override fun run() {
        checkInactiveProviders()
        handleRevivedProviders()
        collectInactiveProviders()
        logger.info { "Inactive providers: $inactiveProviderMap" }
    }

    private fun checkInactiveProviders() {
        inactiveProviderMap.keys.associateWith { provider -> provider.check() }
            .filter { entry -> entry.value }.keys
            .forEach { provider -> inactiveProviderMap.merge(provider, 0) { oldVal, _ -> oldVal + 1 } }
    }

    private fun handleRevivedProviders() {
        inactiveProviderMap.filter { it.value == numberOfSuccessfulChecksForReactivate }.keys
            .forEach {
                inactiveProviderMap.remove(it)
                providersToReAccept.add(it)
            }

        val acceptedProviders = mutableListOf<Provider>()
        providersToReAccept.forEach {
            if (providerList.add(it)) {
                acceptedProviders.add(it)
            }
        }

        acceptedProviders.forEach { providersToReAccept.remove(it) }
    }

    private fun collectInactiveProviders() {
        val providerMap = providerList.check()
        val newInactiveProviderMap = providerMap.filter { !it.value }.mapValues { 0 }
        providerList.exclude(newInactiveProviderMap.keys)
        newInactiveProviderMap.forEach { (k, v) -> inactiveProviderMap.merge(k, v) { oldVal, _ -> oldVal } }
    }

    fun inactiveProviders() = inactiveProviderMap.toMap()

    fun providersToReAccept() = providersToReAccept.toList()

}
