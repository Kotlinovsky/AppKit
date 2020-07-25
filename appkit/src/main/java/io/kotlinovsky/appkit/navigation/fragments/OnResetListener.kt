package io.kotlinovsky.appkit.navigation.fragments

interface OnResetListener {

    /**
     * Вызывается при сбросе состояния.
     *
     * @return True - если было выполнено действие по сбросу,
     * False - если не было выполнено никаких действий.
     */
    fun onReset(): Boolean
}