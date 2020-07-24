package io.kotlinovsky.appkit.navigation.controllers

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.kotlinovsky.appkit.AppKitTestRunner
import io.kotlinovsky.appkit.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AppKitTestRunner::class)
class BottomNavigationControllerTest : Assert() {

    @Test
    fun testIntegrationWithBottomNavigationView() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()

        val fragmentContainer = FrameLayout(activity)
        val container = FrameLayout(activity).apply { id = View.generateViewId() }
        val bottomNavigationView = BottomNavigationView(activity).apply {
            id = View.generateViewId()

            menu.add(0, View.generateViewId(), 1, "1")
            menu.add(0, View.generateViewId(), 2, "2")
            menu.add(0, View.generateViewId(), 3, "3")
        }

        activity.setContentView(container)
        container.addView(fragmentContainer)
        container.addView(bottomNavigationView)
        controller.setup()

        val navController = BottomNavigationController(container.id, activity.supportFragmentManager) {
            when (it) {
                1 -> Fragment(R.layout.layout_container)
                2 -> Fragment(R.layout.layout_container)
                else -> Fragment(R.layout.layout_container)
            }
        }

        navController.setup(bottomNavigationView)

        val firstFragmentTag = bottomNavigationView.menu.getItem(0).itemId
        val firstFragment = activity.supportFragmentManager.findFragmentByTag(firstFragmentTag.toString())
        val firstNestedFragment = Fragment(R.layout.layout_container)
        val firstsSecondNestedFragment = Fragment(R.layout.layout_container)

        firstFragment!!
            .childFragmentManager
            .beginTransaction()
            .add(R.id.container, firstNestedFragment)
            .commit()

        firstFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, firstsSecondNestedFragment)
            .addToBackStack(null)
            .commit()

        assertEquals(0, navController.backstack.size)
        assertTrue(firstFragment.isVisible)

        val secondFragmentTag = bottomNavigationView.menu.getItem(1).itemId
        bottomNavigationView.selectedItemId = secondFragmentTag
        val secondFragment = activity.supportFragmentManager.findFragmentByTag(secondFragmentTag.toString())
        val secondNestedFragment = Fragment(R.layout.layout_container)
        val secondsSecondNestedFragment = Fragment(R.layout.layout_container)

        secondFragment!!
            .childFragmentManager
            .beginTransaction()
            .add(R.id.container, secondNestedFragment)
            .commit()

        secondFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, secondsSecondNestedFragment)
            .addToBackStack(null)
            .commit()

        assertEquals(1, navController.backstack.size)
        assertEquals(firstFragmentTag, navController.backstack[0])
        assertFalse(firstFragment.isVisible)
        assertTrue(firstFragment.isDetached)
        assertTrue(secondFragment.isVisible)
        assertFalse(secondFragment.isDetached)

        val thirdFragmentTag = bottomNavigationView.menu.getItem(2).itemId
        bottomNavigationView.selectedItemId = thirdFragmentTag
        val thirdFragment = activity.supportFragmentManager.findFragmentByTag(thirdFragmentTag.toString())
        val thirdNestedFragment = Fragment(R.layout.layout_container)
        val thirdsSecondNestedFragment = Fragment(R.layout.layout_container)

        thirdFragment!!
            .childFragmentManager
            .beginTransaction()
            .add(R.id.container, thirdNestedFragment)
            .commit()

        thirdFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, thirdsSecondNestedFragment)
            .addToBackStack(null)
            .commit()

        assertEquals(2, navController.backstack.size)
        assertEquals(firstFragmentTag, navController.backstack[0])
        assertEquals(secondFragmentTag, navController.backstack[1])
        assertFalse(firstFragment.isVisible)
        assertTrue(firstFragment.isDetached)
        assertFalse(secondFragment.isVisible)
        assertTrue(secondFragment.isDetached)
        assertTrue(thirdFragment.isVisible)
        assertFalse(thirdFragment.isDetached)

        bottomNavigationView.selectedItemId = secondFragmentTag

        assertEquals(2, navController.backstack.size)
        assertEquals(firstFragmentTag, navController.backstack[0])
        assertEquals(thirdFragmentTag, navController.backstack[1])
        assertFalse(firstFragment.isVisible)
        assertTrue(firstFragment.isDetached)
        assertTrue(secondFragment.isVisible)
        assertFalse(secondFragment.isDetached)
        assertFalse(thirdFragment.isVisible)
        assertTrue(thirdFragment.isDetached)

        bottomNavigationView.selectedItemId = firstFragmentTag

        assertEquals(3, navController.backstack.size)
        assertEquals(firstFragmentTag, navController.backstack[0])
        assertEquals(thirdFragmentTag, navController.backstack[1])
        assertEquals(secondFragmentTag, navController.backstack[2])
        assertTrue(firstFragment.isVisible)
        assertFalse(firstFragment.isDetached)
        assertFalse(secondFragment.isVisible)
        assertTrue(secondFragment.isDetached)
        assertFalse(thirdFragment.isVisible)
        assertTrue(thirdFragment.isDetached)

        bottomNavigationView.selectedItemId = secondFragmentTag

        assertEquals(3, navController.backstack.size)
        assertEquals(firstFragmentTag, navController.backstack[0])
        assertEquals(thirdFragmentTag, navController.backstack[1])
        assertEquals(firstFragmentTag, navController.backstack[2])
        assertFalse(firstFragment.isVisible)
        assertTrue(firstFragment.isDetached)
        assertTrue(secondFragment.isVisible)
        assertFalse(secondFragment.isDetached)
        assertFalse(thirdFragment.isVisible)
        assertTrue(thirdFragment.isDetached)

        assertTrue(navController.pop())
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertTrue(secondNestedFragment.isVisible)
        assertFalse(secondsSecondNestedFragment.isVisible)
        assertFalse(thirdFragment.isVisible)

        assertTrue(navController.pop())
        assertTrue(firstFragment.isVisible)
        assertFalse(firstNestedFragment.isVisible)
        assertTrue(firstsSecondNestedFragment.isVisible)
        assertTrue(isFragmentRemoved(activity.supportFragmentManager, secondFragment))
        assertFalse(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)

        assertTrue(navController.pop())
        assertTrue(firstFragment.isVisible)
        assertTrue(firstNestedFragment.isVisible)
        assertFalse(firstsSecondNestedFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)

        assertTrue(navController.pop())
        assertFalse(firstFragment.isVisible)
        assertFalse(isFragmentRemoved(activity.supportFragmentManager, firstFragment))
        assertFalse(secondFragment.isVisible)
        assertTrue(thirdFragment.isVisible)
        assertFalse(thirdNestedFragment.isVisible)
        assertTrue(thirdsSecondNestedFragment.isVisible)

        assertTrue(navController.pop())
        assertFalse(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(thirdFragment.isVisible)
        assertTrue(thirdNestedFragment.isVisible)
        assertFalse(thirdsSecondNestedFragment.isVisible)

        assertTrue(navController.pop())
        assertTrue(firstFragment.isVisible)
        assertTrue(firstNestedFragment.isVisible)
        assertFalse(firstsSecondNestedFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)
        assertTrue(isFragmentRemoved(activity.supportFragmentManager, thirdFragment))
        assertFalse(thirdNestedFragment.isVisible)
        assertFalse(thirdsSecondNestedFragment.isVisible)

        assertFalse(navController.pop())
    }

    @Test
    fun testCircularBackstackHandling() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()

        val fragmentContainer = FrameLayout(activity)
        val container = FrameLayout(activity).apply { id = View.generateViewId() }
        val bottomNavigationView = BottomNavigationView(activity).apply {
            id = View.generateViewId()

            menu.add(0, View.generateViewId(), 1, "1")
            menu.add(0, View.generateViewId(), 2, "2")
            menu.add(0, View.generateViewId(), 3, "3")
        }

        activity.setContentView(container)
        container.addView(fragmentContainer)
        container.addView(bottomNavigationView)
        controller.setup()

        val navController = BottomNavigationController(container.id, activity.supportFragmentManager) {
            when (it) {
                1 -> Fragment(R.layout.layout_container)
                2 -> Fragment(R.layout.layout_container)
                else -> Fragment(R.layout.layout_container)
            }
        }

        navController.setup(bottomNavigationView)

        val firstFragmentTag = bottomNavigationView.menu.getItem(0).itemId
        val secondFragmentTag = bottomNavigationView.menu.getItem(1).itemId

        bottomNavigationView.selectedItemId = secondFragmentTag
        bottomNavigationView.selectedItemId = firstFragmentTag
        bottomNavigationView.selectedItemId = secondFragmentTag

        assertEquals(1, navController.backstack.size)
        assertEquals(firstFragmentTag, navController.backstack[0])
    }

    private fun isFragmentRemoved(fragmentManager: FragmentManager, fragment: Fragment) =
        fragmentManager.findFragmentByTag(fragment.tag) == null
}