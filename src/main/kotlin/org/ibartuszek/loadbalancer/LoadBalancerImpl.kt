package org.ibartuszek.loadbalancer

import mu.KotlinLogging
import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.providerlist.ProviderList
import java.util.concurrent.atomic.AtomicLong

class LoadBalancerImpl(
    private val maximumRequestPerProviders: Int,
    private val providerList: ProviderList,
    private val activeRequests: AtomicLong
) : LoadBalancer {

    private val logger = KotlinLogging.logger { }

    override fun accept(provider: Provider): Boolean =
        providerList.add(provider).also { accept ->
            if (accept) {
                logger.info { "New provider=$provider accepted!" }
            } else {
                logger.warn { "List of providers is full, provider=$provider is rejected!" }
            }
        }

    override fun exclude(provider: Provider): Boolean =
        providerList.exclude(provider).also { excluded ->
            if (excluded) {
                logger.info { "The provider=$provider was excluded from the list!" }
            } else {
                logger.warn { "List of providers does not contain the provider=$provider!" }
            }
        }

    override fun get(): String =
        if (providerList.aliveProviders() == 0) {
            logger.warn { "Request cannot be forwarded, there are no registered Provider!" }
            throw ProviderListEmptyException()
        } else if (providerList.aliveProviders() * maximumRequestPerProviders <= activeRequests.get()) {
            logger.warn { "Request cannot be forwarded, because the active requests number reached the request limit!" }
            throw ProviderRequestCapacityLimitException()
        } else {
            activeRequests.incrementAndGet()
            val id = providerList.poll().get()
            logger.info { "Return id=$id" }
            activeRequests.decrementAndGet()
            id
        }

}
