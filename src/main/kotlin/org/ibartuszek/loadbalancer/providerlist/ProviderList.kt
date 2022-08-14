package org.ibartuszek.loadbalancer.providerlist

import mu.KotlinLogging
import org.ibartuszek.loadbalancer.provider.Provider

class ProviderList(
    private val maximumNumberOfProviders: Int,
    private val selectionStrategy: ProviderSelectionStrategy
) {

    private val logger = KotlinLogging.logger { }
    private val queue = ArrayDeque<Provider>(maximumNumberOfProviders)

    fun add(provider: Provider): Boolean {
        if (queue.size >= maximumNumberOfProviders) {
            return false
        }
        return queue.add(provider)
    }

    fun poll(): Provider? {
        if (queue.size == 0) {
            return null
        }
        val provider = queue[selectionStrategy.selectIndex(queue.size)]
        queue.remove(provider)
        logger.debug { "Select provider=$provider" }
        return provider
    }

    fun size() = queue.size

}
