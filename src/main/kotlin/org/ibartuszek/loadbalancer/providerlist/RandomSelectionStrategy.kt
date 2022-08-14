package org.ibartuszek.loadbalancer.providerlist

class RandomSelectionStrategy: ProviderSelectionStrategy {

    override fun selectIndex(maximumIndex: Int) = (0..maximumIndex).random()

}
