package org.ibartuszek.loadbalancer.provider

class ProviderImpl(private val id: String) : Provider {

    override fun get(): String = id

}
