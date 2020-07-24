package io.kotlinovsky.appkit

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Стартер тестов.
 * Устанавливает конфигурацию тестов.
 */
class AppKitTestRunner(
    testClass: Class<*>
) : RobolectricTestRunner(testClass) {

    override fun buildGlobalConfig(): Config {
        return Config.Builder()
            .setApplication(AppKitApplication::class.java)
            .setPackageName("io.kotlinovsky.appkit")
            .build()
    }
}