package com.joaopedrogms.brainoutapp.data.local.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Conversores Room para `Instant` / `LocalDate`.
 *
 * Hoje o schema guarda `Long` (epoch millis) diretamente na entity, então
 * estes conversores **não estão registrados** no `AppDatabase`. Eles existem
 * porque:
 *  - o ciclo 3 (issue TarefaEntity) trará campos `LocalDate` que valem a pena
 *    serializar como `Long` legível (`YYYY-MM-DD`-style via epoch);
 *  - queremos a infra pronta para essa migração sem mudanças espalhadas.
 *
 * Estilo: anotações finas e funções `object`/`companion`-style dentro de uma
 * classe `object` para serem referenciadas em `@TypeConverters(...)` quando
 * precisarmos.
 */
object Converters {

    /** `Instant` (UTC) ↔ `Long` (epoch millis). */
    @TypeConverter
    @JvmStatic
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    @JvmStatic
    fun toInstant(value: Long?): Instant? =
        value?.let { Instant.ofEpochMilli(it) }

    /** `LocalDate` (representado como 00:00 UTC do dia) ↔ `Long`. */
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(value: LocalDate?): Long? =
        value?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: Long?): LocalDate? =
        value?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
}