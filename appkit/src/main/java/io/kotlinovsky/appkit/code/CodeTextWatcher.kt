package io.kotlinovsky.appkit.code

import android.text.Editable
import android.text.TextWatcher

/**
 * Наблюдатель для блока цифры кода
 *
 * @param index Индекс блока
 * @param requestFocus Функция для запроса фокусировки блока по его индексу
 * @param setText Функция для установки текста в блок по его индексу
 * @param setTextForAll Функция для установки текста в блоки по их индексам
 * @param areBlocksFilled Функция для проверки заполненности блоков
 * @param notifyThatBlocksFilled Функция для вызова кэллбэка о завершении ввода кода
 * @param setSelection Функция для установки выделения в блоке по его индексу
 * @param getSelection Функция для получения выделения в блоке по его индексу
 * @param isPositioningDisabled Функция для проверки отключенности позиционирования блоков
 */
class CodeTextWatcher(
    private val index: Int,
    private val requestFocus: (Int) -> (Unit),
    private val setText: (Int, CharSequence) -> (Unit),
    private val setTextForAll: (Int, CharSequence) -> (Unit),
    private val areBlocksFilled: () -> (Boolean),
    private val notifyThatBlocksFilled: () -> (Unit),
    private val setSelection: (Int, Int) -> (Unit),
    private val getSelection: (Int) -> (Int),
    private val isPositioningDisabled: () -> (Boolean),
    private val codeLength: Int
) : TextWatcher {

    private var areEventsBlocking = false

    override fun afterTextChanged(source: Editable) {
        if (!areEventsBlocking && !isPositioningDisabled()) {
            if (source.length > 2) {
                val selection = getSelection(index)
                val inserted: String

                areEventsBlocking = true

                if (selection == source.length - 1) {
                    inserted = source.subSequence(1, source.length - 1).toString()
                    source.delete(1, source.length)
                } else {
                    inserted = source.subSequence(1, source.length).toString()
                    source.delete(1, source.length)
                }

                areEventsBlocking = false

                if (index + 1 < codeLength) {
                    setTextForAll(index + 1, inserted)
                }
            } else if (source.length == 2) {
                val selection = getSelection(index)

                if (selection == 1) {
                    if (index + 1 < codeLength) {
                        setSelection(index + 1, 0)
                        requestFocus(index + 1)
                    }
                } else {
                    val inserted = source.subSequence(1, source.length).toString()

                    if (index + 1 < codeLength) {
                        setText(index + 1, inserted)
                        setSelection(index + 1, 1)
                        requestFocus(index + 1)
                    }
                }

                areEventsBlocking = true
                source.delete(1, source.length)
                areEventsBlocking = false
            } else if (source.length == 1) {
                if (index + 1 < codeLength) {
                    setSelection(index + 1, 0)
                    requestFocus(index + 1)
                } else if (areBlocksFilled()) {
                    notifyThatBlocksFilled()
                }
            } else if (index > 0) {
                setSelection(index - 1, 1)
                requestFocus(index - 1)
            }
        }
    }

    override fun beforeTextChanged(source: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}