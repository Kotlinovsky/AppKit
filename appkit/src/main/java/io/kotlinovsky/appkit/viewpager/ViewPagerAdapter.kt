package io.kotlinovsky.appkit.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Адаптер для ViewPager'а, находящегося в ViewPagerFragment
 *
 * @param fragment Связанный ViewPagerFragment.
 */
class ViewPagerAdapter(
    private val fragment: ViewPagerFragment
) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment = fragment.getFragment(position)
    override fun getItemCount(): Int = fragment.getFragmentsCount()
}