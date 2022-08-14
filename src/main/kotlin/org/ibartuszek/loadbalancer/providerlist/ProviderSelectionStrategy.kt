package org.ibartuszek.loadbalancer.providerlist

interface ProviderSelectionStrategy {

    /**
     * The function should point out which Provider should be selected on the next call.
     */
    fun selectIndex(maximumIndex: Int): Int

}
