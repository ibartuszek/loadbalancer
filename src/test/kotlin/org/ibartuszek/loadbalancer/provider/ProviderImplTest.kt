package org.ibartuszek.loadbalancer.provider

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProviderImplTest {

    @Test
    fun testGetShouldReturnTheIdOfTheProvider() {
        // given
        val id = "4f354562-7bab-4698-9241-4e4b1f602da5"
        val provider = ProviderImpl(id)
        // when
        val actual = provider.get()
        // then
        assertEquals(id, actual, "The provided id and the return value should be the same!")
    }

}
