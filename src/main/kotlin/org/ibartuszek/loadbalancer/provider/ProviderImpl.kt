package org.ibartuszek.loadbalancer.provider

data class ProviderImpl(private val id: String) : Provider {

    override fun get(): String = id

    override fun check(): Boolean = true

}
