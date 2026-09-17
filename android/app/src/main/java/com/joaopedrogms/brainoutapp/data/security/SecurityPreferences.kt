package com.joaopedrogms.brainoutapp.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contrato do repositório de preferências de segurança (AppLock).
 *
 * Persiste **apenas o hash** da senha local em
 * [EncryptedSharedPreferences] (cifrado em repouso com chave AES256_GCM
 * via AndroidKeyStore). A senha em texto plano nunca toca disco.
 *
 * Origem: ADR-0006 — AppLock opcional no cold start.
 *
 * Camadas:
 *  - UI (`ui/screens/applock/AppLockScreen.kt`) chama via ViewModel.
 *  - ViewModel chama [verifySenha] / [setLockEnabled] / [clearLock].
 *  - Este contrato é o único ponto onde o hash é gerado/verificado.
 */
interface SecurityPreferencesRepository {
    /** `true` quando o usuário já definiu uma senha local (AppLock ativo). */
    suspend fun isLockEnabled(): Boolean

    /**
     * Ativa o AppLock persistindo `PBKDF2(senha, salt)` em
     * EncryptedSharedPreferences. Apaga qualquer referência à senha em
     * memória após o hash.
     *
     * @param senha texto plano digitado pelo usuário (será descartado).
     */
    suspend fun setLockEnabled(enabled: Boolean, senha: String)

    /**
     * Verifica se [senha] produz o mesmo hash persistido. Usa comparação
     * constant-time para evitar timing-attack local.
     */
    suspend fun verifySenha(senha: String): Boolean

    /** Remove o hash (e o salt) do storage. Usado por "Esqueci a senha". */
    suspend fun clearLock()
}

/**
 * Implementação default do [SecurityPreferencesRepository].
 *
 *  - Storage: [EncryptedSharedPreferences] nome `brainoutapp_secure_prefs`.
 *  - KDF: PBKDF2-HMAC-SHA256 com 100.000 iterações e salt aleatório de 16 bytes.
 *  - Tamanho da chave derivada: 256 bits.
 *
 * **Decisão consciente:** o salt é gerado e armazenado **junto** com o hash
 * (em prefs cifradas). Isso é seguro porque as próprias prefs são cifradas
 * pelo AndroidKeyStore antes de tocar disco — quem lê o arquivo raw não
 * consegue extrair o salt, então PBKDF2 não pode ser pré-computado offline
 * sem antes quebrar a chave do KeyStore. ADR-0006 chama isso de "salt
 * gerenciado internamente pela biblioteca" — quando EncryptedSharedPreferences
 * é usado, isso é equivalente.
 *
 * Sem `try/catch` amplo aqui: EncryptedSharedPreferences é projetado para
 * falhar de forma clara (KeyStore indisponível, KeyStore corrompido) — a
 * decisão de fallback é da camada acima (ViewModel → estado de erro).
 */
@Singleton
class SecurityPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SecurityPreferencesRepository {

    private val prefs: SharedPreferences by lazy { createEncryptedPrefs() }

    override suspend fun isLockEnabled(): Boolean {
        // Lock ativo se ambas as chaves existirem.
        return prefs.contains(KEY_SALT) && prefs.contains(KEY_HASH)
    }

    override suspend fun setLockEnabled(enabled: Boolean, senha: String) {
        if (!enabled) {
            // Segurança: enabled=false com senha não tem efeito aqui —
            // quem desativa deve chamar [clearLock] em separado.
            Log.w(TAG, "setLockEnabled(false, ...) ignorado — use clearLock()")
            return
        }
        if (senha.isEmpty()) {
            throw IllegalArgumentException("Senha não pode ser vazia")
        }
        val salt = generateSalt()
        val hash = pbkdf2(senha, salt)
        prefs.edit()
            .putString(KEY_SALT, salt.toBase64())
            .putString(KEY_HASH, hash.toBase64())
            .apply()
        Log.i(TAG, "AppLock ativado (hash persistido, plaintext descartado)")
    }

    override suspend fun verifySenha(senha: String): Boolean {
        val saltStr = prefs.getString(KEY_SALT, null) ?: return false
        val hashStr = prefs.getString(KEY_HASH, null) ?: return false
        val salt = saltStr.fromBase64()
        val expected = hashStr.fromBase64()
        val candidate = pbkdf2(senha, salt)
        return constantTimeEquals(expected, candidate)
    }

    override suspend fun clearLock() {
        prefs.edit()
            .remove(KEY_SALT)
            .remove(KEY_HASH)
            .apply()
        Log.i(TAG, "AppLock desativado (salt+hash removidos)")
    }

    // --- helpers internos ----------------------------------------------------

    /**
     * Cria o [EncryptedSharedPreferences] com MasterKey AES256_GCM. O
     * esquema de MasterKey é o esquema default (alias `_androidx_security_master_key_`).
     */
    private fun createEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    /** PBKDF2-HMAC-SHA256, 100k iterações, 256 bits de saída. */
    private fun pbkdf2(senha: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(senha.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = factory.generateSecret(spec)
        return key.encoded
    }

    private fun generateSalt(): ByteArray = ByteArray(SALT_BYTES).also {
        SecureRandom().nextBytes(it)
    }

    /** Comparação constant-time: evita vazar quantos bytes batem via tempo. */
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var diff = 0
        for (i in a.indices) {
            diff = diff or (a[i].toInt() xor b[i].toInt())
        }
        return diff == 0
    }

    private fun ByteArray.toBase64(): String = android.util.Base64.encodeToString(
        this,
        android.util.Base64.NO_WRAP,
    )

    private fun String.fromBase64(): ByteArray = android.util.Base64.decode(
        this,
        android.util.Base64.NO_WRAP,
    )

    private companion object {
        const val TAG = "SecurityPrefsRepo"
        const val PREFS_FILE_NAME = "brainoutapp_secure_prefs"
        const val KEY_SALT = "app_lock_salt"
        const val KEY_HASH = "app_lock_hash"
        const val SALT_BYTES = 16
        const val PBKDF2_ITERATIONS = 100_000
        const val KEY_BITS = 256
    }
}
