package org.ibartuszek.loadbalancer

import org.ibartuszek.loadbalancer.provider.Provider

class LoadBalancerImpl(
    private val providerList: ProviderList
): LoadBalancer {

    override fun accept(provider: Provider): Boolean = providerList.add(provider)

    override fun get(): String =
        providerList.poll()?.get() ?: throw ProviderListEmptyException()

}
