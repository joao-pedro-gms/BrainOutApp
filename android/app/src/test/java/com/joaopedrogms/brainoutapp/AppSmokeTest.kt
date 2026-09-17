package com.joaopedrogms.brainoutapp

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Smoke test para validar o pipeline `./gradlew testDebugUnitTest`.
 * Testes reais das camadas virão nas próximas issues.
 */
class AppSmokeTest {
    @Test
    fun `package constants are wired`() {
        assertEquals("com.joaopedrogms.brainoutapp", "com.joaopedrogms.brainoutapp")
    }
}
