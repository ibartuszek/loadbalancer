package org.ibartuszek.loadbalancer.provider

interface Provider {

    /**
     * Return the id of the provider.
     */
    fun get(): String

}
