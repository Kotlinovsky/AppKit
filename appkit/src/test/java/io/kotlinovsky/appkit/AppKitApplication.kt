package io.kotlinovsky.appkit

import android.app.Application

/**
 * Тестовый загрузчик приложения.
 * Устанавливает тему по-умолчанию.
 */
open class AppKitApplication : Application() {

    override fun onCreate() {
        setTheme(R.style.Theme_AppCompat)
        super.onCreate()
    }
}