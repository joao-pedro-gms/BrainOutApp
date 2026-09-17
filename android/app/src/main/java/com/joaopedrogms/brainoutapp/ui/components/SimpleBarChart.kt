package com.joaopedrogms.brainoutapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Gráfico de barras simples, **sem dependências externas** —
 * renderizado em [Canvas] puro (issue #13). Inspirado no wireframe
 * 06-dashboard.svg.
 *
 * Características:
 *  - **7 barras verticais**, uma por entrada de [data].
 *  - Altura proporcional à contagem (`max(count)` define a altura máxima).
 *  - Cor base: `MaterialTheme.colorScheme.primary` (token semântico
 *    `accentPrimary` do design system — vide `ui/theme/Color.kt`).
 *    Aceita override opcional via [barColor] para customizações locais.
 *  - Rótulo do dia (1-char: "S T Q Q S S D") abaixo de cada barra.
 *  - Valor numérico em cima (quando `count > 0`).
 *
 * > Decisão de design: optamos por Canvas em vez de uma lib externa
 * > (MPAndroidChart, Vico, etc.) para manter o app 100% offline e
 * > sem dependências opagas. Para gráficos mais densos no futuro,
 * > a lane pode introduzir Vico — este componente cobre o dashboard
 * > simples da issue #13.
 *
 * @param data          Lista de pares `(label, count)`. Espera-se 7 itens.
 *                      Não impose limite rígido — aceita qualquer tamanho
 *                      e distribui as colunas uniformemente.
 * @param barColor      Cor das barras (default = `colorScheme.primary`).
 * @param labelColor    Cor do rótulo (default = `colorScheme.onSurfaceVariant`).
 * @param height        Altura total do gráfico (barras + rótulos).
 * @param modifier      Modifier externo.
 */
@Composable
fun SimpleBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    height: Dp = 140.dp,
) {
    if (data.isEmpty()) return

    val maxCount = data.maxOf { it.second }.coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        // Área do Canvas: barras em si.
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height - 24.dp), // reserva ~24.dp para a linha de labels
        ) {
            drawBarChart(
                values = data.map { it.second },
                maxValue = maxCount,
                barColor = barColor,
            )
        }

        // Linha de labels (1-char por dia) abaixo do Canvas.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(24.dp),
                )
            }
        }
    }
}

/**
 * Desenha as barras dentro do [DrawScope] do Canvas.
 *
 * Cada barra ocupa um "slot" horizontal de largura `slotWidth`,
 * com `gapRatio` (~30%) de espaço entre barras para dar respiro.
 * A altura de cada barra é `value / maxValue * (this.size.height - topPadding)`,
 * onde `topPadding` reserva espaço para o número em cima.
 *
 * > Por que `drawRect` em vez de `drawLine`? `drawRect` com offset+size
 * > é a primitiva mais barata para barras preenchidas; `drawLine` foi
 * > considerado mas produziria apenas contornos, exigindo um segundo
 * > `drawRect` para preencher.
 */
private fun DrawScope.drawBarChart(
    values: List<Int>,
    maxValue: Int,
    barColor: Color,
) {
    val n = values.size
    if (n == 0) return

    val gapRatio = 0.30f
    val slotWidth = size.width / n
    val barWidth = slotWidth * (1f - gapRatio)
    val barLeftOffset = (slotWidth - barWidth) / 2f

    val topPadding = size.height * 0.20f
    val drawableHeight = size.height - topPadding

    values.forEachIndexed { index, value ->
        if (value <= 0) return@forEachIndexed

        val ratio = value.toFloat() / maxValue.toFloat()
        val barHeight = drawableHeight * ratio

        val x = index * slotWidth + barLeftOffset
        val y = topPadding + (drawableHeight - barHeight)

        drawRect(
            color = barColor,
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
        )
    }
}
