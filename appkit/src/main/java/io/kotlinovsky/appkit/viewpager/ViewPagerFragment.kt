package io.kotlinovsky.appkit.viewpager

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Фрагмент, реализующий переключение посредством ViewPager
 * Также связывает ViewPager и TabLayout.
 *
 * @param layoutId ID верстки экрана.
 * @param viewPagerId ID ViewPager'а
 * @param tabLayoutId ID TabLayout'а
 */
abstract class ViewPagerFragment(
    @LayoutRes layoutId: Int,
    @IdRes private val viewPagerId: Int,
    @IdRes private val tabLayoutId: Int
) : Fragment(layoutId) {

    private var mediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = view.findViewById<ViewPager2>(viewPagerId)
        val tabLayout = view.findViewById<TabLayout>(tabLayoutId)
        viewPager.adapter = ViewPagerAdapter(this)

        mediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = view.context.getString(getFragmentTitleId(position))
        }

        mediator!!.attach()
    }

    override fun onDestroyView() {
        view?.findViewById<ViewPager2>(viewPagerId)?.adapter = null
        mediator!!.detach()
        mediator = null

        super.onDestroyView()
    }

    /**
     * Выдает фрагмент на указанной позиции
     *
     * @param position Позиция, на которой нужно получить фрагмент
     * @return Фрагмент, который должен быть на указанной позиции
     */
    abstract fun getFragment(position: Int): Fragment

    /**
     * Возвращает ID строки с названием фрагмента.
     *
     * @param position Позиция, на которой нужно узнать название фрагмента.
     * @return ID строки с названием фрагмента.
     */
    @StringRes
    abstract fun getFragmentTitleId(position: Int): Int

    /**
     * Возвращает количество фрагментов, которые
     * будут отображены во ViewPAger.
     *
     * @return Количество фрагментов.
     */
    abstract fun getFragmentsCount(): Int
}