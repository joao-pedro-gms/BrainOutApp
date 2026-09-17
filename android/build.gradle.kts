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
    // A regra "multiline-expression" do ktlint 12.1 dispara em qualquer
    // expressao multi-linha (como `Projeto(\n  ...args\n)`), conflitando
    // com o estilo do projeto. Desabilitamos via editorconfig.
}
