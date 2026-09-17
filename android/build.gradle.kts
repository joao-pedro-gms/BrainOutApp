// Top-level build file.
//
// Os plugins são *declarados* aqui (sem `apply`) e aplicados por módulo
// (geralmente :app). Isso habilita versionamento centralizado no
// gradle/libs.versions.toml.
//
// ktlint é aplicado em todos os subprojetos via `subprojects {}` abaixo —
// o CI roda `./gradlew ktlintCheck` (issue #6).
//
// Documentação: https://docs.gradle.org/current/userguide/plugins.html

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    // ktlint lê .editorconfig automaticamente; regras do projeto já estão lá
    // (4 spaces, 120 col, LF, UTF-8).
    // ktlint 12.1.0 tem regras de Kotlin Script que quebram em expressoes
    // multi-linha. Excluimos *.kts porque os build.gradle.kts sao poucos
    // e revisados a mao (config via ktlint.excludes).
    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtlintCheck>().configureEach {
        excludes += "**/*.kts"
    }
}
