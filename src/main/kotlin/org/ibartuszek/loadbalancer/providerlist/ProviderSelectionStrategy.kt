package org.ibartuszek.loadbalancer.providerlist

interface ProviderSelectionStrategy {

    /**
     * The function should point out which Provider should be selected on the next call.
     */
    fun selectIndex(maximumIndex: Int): Int

    /**
     * The function should tell if the element should be put at the end of the list
     */
    fun shouldRotate(): Boolean

}
