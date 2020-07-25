package io.kotlinovsky.appkit.navigation.controllers

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.kotlinovsky.appkit.navigation.fragments.OnResetListener
import io.kotlinovsky.appkit.navigation.popBackstack
import java.util.*

private const val BACKSTACK_LENGTH_BUNDLE_KEY = "backstack_length"
private const val BACKSTACK_ITEM_BUNDLE_KEY = "backstack_item_"

/**
 * Контроллер навигации BottomNavigationView
 *
 * @param containerId ID контейнера
 * @param fragmentManager Менеджер фрагментов
 * @param createFragment Функция для создания фрагмента.
 */
class BottomNavigationController(
    @IdRes private val containerId: Int,
    private val fragmentManager: FragmentManager,
    private val createFragment: (Int) -> (Fragment)
) {

    private var isCallbackBlocked = false
    private var bottomNavigationView: BottomNavigationView? = null

    @VisibleForTesting
    val backstack = Stack<Int>()

    /**
     * Связывает BottomNavigationView и контроллер
     * Запускает стартовый фрагмент если ни один фрагмент не запущен.
     *
     * @param bottomNavigationView BottomNavigationView для связки.
     * @param savedInstanceState Bundle с состоянием.
     */
    fun setup(bottomNavigationView: BottomNavigationView, savedInstanceState: Bundle?) {
        this.bottomNavigationView = bottomNavigationView

        if (savedInstanceState != null) {
            repeat(savedInstanceState.getInt(BACKSTACK_LENGTH_BUNDLE_KEY, 0)) {
                backstack.push(savedInstanceState.getInt("$BACKSTACK_ITEM_BUNDLE_KEY$it"))
            }
        }

        if (fragmentManager.fragments.isEmpty()) {
            fragmentManager
                .beginTransaction()
                .add(
                    containerId,
                    createFragment(bottomNavigationView.selectedItemId),
                    bottomNavigationView.selectedItemId.toString()
                ).commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (isCallbackBlocked) {
                return@setOnNavigationItemSelectedListener true
            }

            val selectedItemId = it.itemId
            val previousItemId = bottomNavigationView.selectedItemId
            var selectedFragment = fragmentManager.findFragmentByTag(selectedItemId.toString())

            if (previousItemId == selectedItemId) {
                if (selectedFragment!!.childFragmentManager.backStackEntryCount > 0) {
                    selectedFragment.childFragmentManager.popBackStack(
                        null,
                        POP_BACK_STACK_INCLUSIVE
                    )
                } else {
                    val fragments = LinkedList<Fragment>().apply { push(selectedFragment) }
                    val listeners = LinkedList<OnResetListener>()

                    while (fragments.isNotEmpty()) {
                        val fragment = fragments.removeFirst()

                        if (fragment.isVisible) {
                            fragment.childFragmentManager.fragments.forEach { child ->
                                fragments.addLast(child)
                            }

                            if (fragment is OnResetListener) {
                                listeners.addFirst(fragment)
                            }
                        }
                    }

                    for (listener in listeners) {
                        if (listener.onReset()) {
                            break
                        }
                    }
                }

                return@setOnNavigationItemSelectedListener true
            }

            val transaction = fragmentManager.beginTransaction()

            if (backstack.size > 0) {
                val backstackIterator = backstack.iterator()
                var backstackIndex = 0

                while (backstackIterator.hasNext()) {
                    val current = backstackIterator.next()

                    if (backstackIndex++ > 0 && current == selectedItemId) {
                        backstackIterator.remove()
                        backstackIndex--
                    }
                }
            }

            if (backstack.lastOrNull() != previousItemId) {
                backstack.push(previousItemId)
            }

            if (selectedFragment == null) {
                selectedFragment = createFragment(selectedItemId)
                transaction.add(containerId, selectedFragment, selectedItemId.toString())
            } else {
                transaction.attach(selectedFragment)
            }

            transaction
                .detach(fragmentManager.findFragmentByTag(previousItemId.toString())!!)
                .commit()

            true
        }
    }

    /**
     * Сохраняет состояние в переданный Bundle.
     * Сохраняет Backstack.
     *
     * @param outState Bundle, в который будет сохранено состояние.
     */
    fun save(outState: Bundle) {
        outState.putInt(BACKSTACK_LENGTH_BUNDLE_KEY, backstack.size)

        repeat(backstack.size) {
            outState.putInt("$BACKSTACK_ITEM_BUNDLE_KEY$it", backstack[it])
        }
    }

    /**
     * Обрабатывает Backstack
     *
     * @return True - если было выполнено действие, False -
     * если не было выполнено действие.
     */
    fun pop(): Boolean {
        if (backstack.isEmpty()) {
            return false
        }

        val currentItemId = bottomNavigationView!!.selectedItemId
        val currentFragment = fragmentManager.findFragmentByTag(currentItemId.toString())

        if (!popBackstack(currentFragment!!.childFragmentManager)) {
            val previousItemId = backstack.pop()
            val previousFragment = fragmentManager.findFragmentByTag(previousItemId.toString())
            val transaction = fragmentManager.beginTransaction()

            if (backstack.contains(currentItemId)) {
                transaction.detach(currentFragment)
            } else {
                transaction.remove(currentFragment)
            }

            transaction
                .attach(previousFragment!!)
                .commit()

            isCallbackBlocked = true
            bottomNavigationView!!.selectedItemId = previousItemId
            isCallbackBlocked = false
        }

        return true
    }
}