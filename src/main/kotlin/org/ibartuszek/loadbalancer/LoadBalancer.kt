package org.ibartuszek.loadbalancer

import org.ibartuszek.loadbalancer.provider.Provider

interface LoadBalancer {

    /**
     * The method add the provider to the list.
     * If the list was not empty it returns true if it is full false.
     */
    fun accept(provider: Provider): Boolean

    /**
     * The method return an id provided one of the Providers
     * or throw ProviderListEmptyException if there is no available provider.
     */
    @Throws(ProviderListEmptyException::class)
    fun get(): String

}

class ProviderListEmptyException: java.lang.RuntimeException()
