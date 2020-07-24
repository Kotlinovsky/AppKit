package io.kotlinovsky.appkit.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.kotlinovsky.appkit.AppKitTestRunner
import io.kotlinovsky.appkit.R
import io.kotlinovsky.appkit.navigation.fragments.ContainerFragment
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AppKitTestRunner::class)
class NavigationHelperTest : Assert() {

    @Test
    fun checkThatContainerOnBackPressedCalledAndRemovedFromManager() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()

        val firstFragment = Fragment(R.layout.layout_container)
        val secondFragment = TestContainerFragment()
        val thirdFragment = TestContainerFragment()

        activity.setContentView(R.layout.layout_container)
        controller.setup()

        activity
            .supportFragmentManager
            .beginTransaction()
            .add(R.id.container, firstFragment, "1")
            .commit()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, secondFragment, "2")
            .addToBackStack(null)
            .commit()

        secondFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, thirdFragment, "3")
            .commit()

        thirdFragment.onBackPressedResult = false

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertEquals(1, thirdFragment.onBackPressedCallsCount)
        assertEquals(0, secondFragment.onBackPressedCallsCount)
        assertFalse(thirdFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(firstFragment.isVisible)

        assertFalse(popBackstack(activity.supportFragmentManager))
    }

    @Test
    fun checkThatNestedBackstackPopped() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()

        val firstFragment = Fragment(R.layout.layout_container)
        val secondFragment = Fragment(R.layout.layout_container)
        val thirdFragment = Fragment(R.layout.layout_container)
        val fourthFragment = Fragment(R.layout.layout_container)
        val fifthFragment = Fragment(R.layout.layout_container)
        val sixthFragment = Fragment(R.layout.layout_container)
        val seventhFragment = Fragment(R.layout.layout_container)
        val eighthFragment = Fragment(R.layout.layout_container)

        activity.setContentView(R.layout.layout_container)
        controller.setup()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, firstFragment)
            .commit()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, secondFragment)
            .addToBackStack(null)
            .commit()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, thirdFragment, "2")
            .addToBackStack(null)
            .commit()

        thirdFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, fourthFragment)
            .commit()

        thirdFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, fifthFragment)
            .addToBackStack(null)
            .commit()

        fifthFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, sixthFragment)
            .commit()

        sixthFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, seventhFragment)
            .commit()

        sixthFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, eighthFragment)
            .addToBackStack(null)
            .commit()

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(thirdFragment.isVisible)
        assertFalse(fourthFragment.isVisible)
        assertTrue(fifthFragment.isVisible)
        assertTrue(sixthFragment.isVisible)
        assertTrue(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(thirdFragment.isVisible)
        assertTrue(fourthFragment.isVisible)
        assertFalse(fifthFragment.isVisible)
        assertFalse(sixthFragment.isVisible)
        assertFalse(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)
        assertFalse(fourthFragment.isVisible)
        assertFalse(fifthFragment.isVisible)
        assertFalse(sixthFragment.isVisible)
        assertFalse(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertTrue(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)
        assertFalse(fourthFragment.isVisible)
        assertFalse(fifthFragment.isVisible)
        assertFalse(sixthFragment.isVisible)
        assertFalse(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertFalse(popBackstack(activity.supportFragmentManager))
    }

    class TestContainerFragment : Fragment(R.layout.layout_container),
        ContainerFragment {

        var onBackPressedCallsCount: Int = 0
        var onBackPressedResult = false

        override fun onBackPressed(): Boolean {
            onBackPressedCallsCount++
            return onBackPressedResult
        }
    }
}