package com.joaopedrogms.brainoutapp.data.local

import android.util.Log
import java.util.UUID

/**
 * Gerador de IDs para entidades locais.
 *
 * **Pendência (issue #8 — registro em `RESULTADO.md`):**
 * o `docs/modelo-dados.md` exige **UUID v7** (ordenável por tempo, melhor
 * indexação local). A biblioteca `uuid-v7` ainda não foi adicionada ao
 * catálogo desta lane, então o helper usa `java.util.UUID.randomUUID()`
 * como **fallback transparente** (UUID v4).
 *
 * Quando a lib uuid-v7 entrar em outra lane:
 *  1. adicionar `uuid-v7` em `android/gradle/libs.versions.toml`;
 *  2. substituir o corpo de [novo] pela chamada da lib;
 *  3. nenhuma outra camada precisa mudar (todas consomem o helper).
 *
 * Para evitar silêncio em produção, registramos um aviso único no Logcat
 * na primeira chamada — ajuda a identificar quando a pendência precisa ser
 * resolvida sem poluir o log a cada insert.
 */
object UuidV7 {

    private const val TAG = "UuidV7"
    private var loggedFallback: Boolean = false

    /**
     * Retorna um novo ID para o projeto.
     *
     * @return string UUID (atualmente v4 via `UUID.randomUUID()`).
     */
    fun novo(): String {
        if (!loggedFallback) {
            Log.w(
                TAG,
                "uuid-v7 ainda não disponível — usando UUID.randomUUID() (v4). " +
                    "Issue a abrir: substituir por lib uuid-v7 quando entrar no catálogo.",
            )
            loggedFallback = true
        }
        return UUID.randomUUID().toString()
    }
}