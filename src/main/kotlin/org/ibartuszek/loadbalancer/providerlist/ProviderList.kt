package org.ibartuszek.loadbalancer.providerlist

import org.ibartuszek.loadbalancer.provider.Provider
import java.util.concurrent.ArrayBlockingQueue

class ProviderList(
    private val maximumNumberOfProviders: Int,
    private val selectionStrategy: ProviderSelectionStrategy
) {

    private val queue = ArrayBlockingQueue<Provider>(maximumNumberOfProviders)

    fun add(provider: Provider): Boolean {
        if (queue.size >= maximumNumberOfProviders) {
            return false
        }
        return queue.add(provider)
    }

    fun exclude(provider: Provider): Boolean = queue.remove(provider)

    fun exclude(providerSet: Set<Provider>) {
        queue.removeAll(providerSet)
    }

    fun poll(): Provider? {
        if (queue.size == 0) {
            return null
        }
        val provider = queue.elementAt(selectionStrategy.selectIndex(maximumIndex = queue.size - 1))
        queue.remove(provider)
        return provider
    }

    fun check(): Map<Provider, Boolean> = queue.associateWith { it.check() }

    fun size() = queue.size

}
