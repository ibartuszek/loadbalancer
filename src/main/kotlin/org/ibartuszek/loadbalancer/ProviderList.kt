package org.ibartuszek.loadbalancer

import org.ibartuszek.loadbalancer.provider.Provider

class ProviderList(private val maximumNumberOfProviders: Int) {

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
        val provider = queue.first()
        queue.remove(provider)
        return provider
    }

    fun size() = queue.size

}
