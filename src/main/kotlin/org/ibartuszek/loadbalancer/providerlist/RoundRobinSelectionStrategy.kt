package org.ibartuszek.loadbalancer.providerlist

class RoundRobinSelectionStrategy: ProviderSelectionStrategy {

    override fun selectIndex(maximumIndex: Int) = 0

    override fun shouldRotate(): Boolean = true

}
