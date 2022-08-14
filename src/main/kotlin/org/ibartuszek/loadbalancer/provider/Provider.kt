package org.ibartuszek.loadbalancer.provider

interface Provider {

    /**
     * Return the id of the provider.
     */
    fun get(): String

    /**
     * Return true if the provider is alive false if it cannot be reached
     */
    fun check(): Boolean

}
