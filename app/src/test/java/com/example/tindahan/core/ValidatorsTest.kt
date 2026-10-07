package com.example.tindahan.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test fun acceptsValidProduct() {
        assertEquals(ValidationResult.Valid, Validators.product("Coke", 2500, 12, 5))
    }

    @Test fun rejectsBlankName() {
        assertTrue(Validators.product("  ", 2500, 12, 5) is ValidationResult.Invalid)
    }

    @Test fun rejectsNegatives() {
        assertTrue(Validators.product("Coke", -1, 12, 5) is ValidationResult.Invalid)
        assertTrue(Validators.product("Coke", 100, -1, 5) is ValidationResult.Invalid)
        assertTrue(Validators.product("Coke", 100, 1, -1) is ValidationResult.Invalid)
    }
}
