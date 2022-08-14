package org.ibartuszek.loadbalancer

import mu.KotlinLogging
import org.ibartuszek.loadbalancer.provider.Provider
import org.ibartuszek.loadbalancer.providerlist.ProviderList

class LoadBalancerImpl(
    private val providerList: ProviderList
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

    override fun get(): String = providerList.poll()?.get().also { id ->
        logger.info { "Return id=$id" }
    } ?: throw ProviderListEmptyException()

}
