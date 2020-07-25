package io.kotlinovsky.appkit

import java.math.RoundingMode
import java.text.DecimalFormat

private val DECIMAL_FORMAT_WITH_FRACTION_PART = DecimalFormat("#.#").apply { roundingMode = RoundingMode.FLOOR }
private val DECIMAL_FORMAT_WITHOUT_FRACTION_PART = DecimalFormat("#")

/**
 * Форматирует число в строку, содержащую количество разрядов
 *
 * @param number Число для форматирования
 * @param useCommaAsSeparator Использовать запятую в качестве разделителя?
 * @return Отформатированное число
 */
fun formatNumber(number: Int, useCommaAsSeparator: Boolean = true): String {
    if (number in 0..999) {
        return number.toString()
    }

    val divider = when (number) {
        in 1000..999_999 -> 1000
        in 1_000_000..999_999_999 -> 1_000_000
        else -> 1_000_000_000
    }

    val format = if (number % divider == 0) {
        DECIMAL_FORMAT_WITHOUT_FRACTION_PART
    } else {
        DECIMAL_FORMAT_WITH_FRACTION_PART
    }

    var result =  when (number) {
        in 1000..999_999 -> "${format.format(number / 1000.0)}K"
        in 1_000_000..999_999_999 -> "${format.format(number / 1_000_000.0)}M"
        else ->  "${format.format(number / 1_000_000_000.0)}B"
    }

    if (!useCommaAsSeparator) {
        result = result.replace(',', '.')
    }

    return result
}