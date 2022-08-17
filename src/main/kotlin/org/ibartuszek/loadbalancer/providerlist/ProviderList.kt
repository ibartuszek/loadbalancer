package org.ibartuszek.loadbalancer.providerlist

import org.ibartuszek.loadbalancer.provider.Provider
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class ProviderList(
    private val maximumNumberOfProviders: Int,
    private val selectionStrategy: ProviderSelectionStrategy,
    private val queue: BlockingQueue<Provider>
) {

    fun add(provider: Provider): Boolean {
        if (queue.size >= maximumNumberOfProviders) {
            return false
        }
        return queue.add(provider)
    }

    fun remove(provider: Provider): Boolean = queue.remove(provider)

    fun remove(providerSet: Set<Provider>) {
        queue.removeAll(providerSet)
    }

    fun get(): Provider {
        val provider = queue.elementAt(selectionStrategy.selectIndex(maximumIndex = queue.size - 1))
        if (selectionStrategy.shouldRotate()) {
            queue.remove(provider)
            queue.add(provider)
        }
        return provider
    }

    fun check(): Map<Provider, Boolean> = queue.associateWith { it.check() }

    fun aliveProviders() = queue.size

}
