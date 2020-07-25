package io.kotlinovsky.appkit.code

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.view.children
import io.kotlinovsky.appkit.AppKitTestRunner
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf

@RunWith(AppKitTestRunner::class)
class CodeEditTextTest {

    @Test
    fun checkEnteringFromClipboardWhenInsertedToStartOfFirstBlock() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        editText.children.forEach { (it as EditText).setText("0") }

        (editText.getChildAt(0) as EditText).let {
            it.requestFocus()

            val watcher = shadowOf(it).watchers[0]

            it.removeTextChangedListener(watcher)
            it.setText("123450")
            it.addTextChangedListener(watcher)
            it.setSelection(5)
            watcher.afterTextChanged(it.text as Editable)
        }

        assertEquals("1", (editText.getChildAt(0) as EditText).text.toString())
        assertEquals("2", (editText.getChildAt(1) as EditText).text.toString())
        assertEquals("3", (editText.getChildAt(2) as EditText).text.toString())
        assertEquals("4", (editText.getChildAt(3) as EditText).text.toString())
        assertEquals("5", (editText.getChildAt(4) as EditText).text.toString())
        assertEquals(1, (editText.getChildAt(4) as EditText).selectionStart)
        assertFalse(editText.getChildAt(0).isFocused)
        assertFalse(editText.getChildAt(1).isFocused)
        assertFalse(editText.getChildAt(2).isFocused)
        assertFalse(editText.getChildAt(3).isFocused)
        assertTrue(editText.getChildAt(4).isFocused)
    }

    @Test
    fun checkEnteringFromClipboardWhenInsertedToEndOfFirstBlock() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        editText.children.forEach { (it as EditText).setText("0") }
        (editText.getChildAt(0) as EditText).let {
            it.requestFocus()
            it.setText("012345")
        }

        assertEquals("0", (editText.getChildAt(0) as EditText).text.toString())
        assertEquals("1", (editText.getChildAt(1) as EditText).text.toString())
        assertEquals("2", (editText.getChildAt(2) as EditText).text.toString())
        assertEquals("3", (editText.getChildAt(3) as EditText).text.toString())
        assertEquals("4", (editText.getChildAt(4) as EditText).text.toString())
        assertEquals(1, (editText.getChildAt(4) as EditText).selectionStart)
        assertFalse(editText.getChildAt(0).isFocused)
        assertFalse(editText.getChildAt(1).isFocused)
        assertFalse(editText.getChildAt(2).isFocused)
        assertFalse(editText.getChildAt(3).isFocused)
        assertTrue(editText.getChildAt(4).isFocused)
    }

    @Test
    fun checkEnteringNotFullyCodeFromClipboard() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        editText.children.forEach { (it as EditText).setText("0") }
        (editText.getChildAt(0) as EditText).let {
            it.requestFocus()
            it.setText("123")
        }

        assertEquals("1", (editText.getChildAt(0) as EditText).text.toString())
        assertEquals("2", (editText.getChildAt(1) as EditText).text.toString())
        assertEquals("3", (editText.getChildAt(2) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(3) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(4) as EditText).text.toString())
        assertEquals(1, (editText.getChildAt(2) as EditText).selectionStart)
        assertFalse(editText.getChildAt(0).isFocused)
        assertFalse(editText.getChildAt(1).isFocused)
        assertTrue(editText.getChildAt(2).isFocused)
        assertFalse(editText.getChildAt(3).isFocused)
        assertFalse(editText.getChildAt(4).isFocused)
    }

    @Test
    fun checkThatCallbackInvokedWhenAllBlocksFilledByClipboard() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)
        var code: String? = null
        var invokesCount = 0

        editText.blocksFilledCallback = { code = it; invokesCount++ }
        activity.setContentView(editText)
        controller.setup()

        (editText.getChildAt(0) as EditText).setText("12345")

        assertEquals("12345", code)
        assertEquals(1, invokesCount)
    }

    @Test
    fun checkThatCallbackInvokedWhenAllBlocksFilled() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)
        var code: String? = null
        var invokesCount = 0

        editText.blocksFilledCallback = { code = it; invokesCount++ }
        activity.setContentView(editText)
        controller.setup()

        (editText.getChildAt(0) as EditText).setText("1")
        (editText.getChildAt(1) as EditText).setText("2")
        (editText.getChildAt(2) as EditText).setText("3")
        (editText.getChildAt(3) as EditText).setText("4")
        (editText.getChildAt(4) as EditText).setText("5")

        assertEquals("12345", code)
        assertEquals(1, invokesCount)
    }

    @Test
    fun checkThatCallbackNotInvokedWhenAllBlocksFilledButFocusNotOnLast() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)
        var invokesCount = 0

        editText.blocksFilledCallback = { invokesCount++ }
        activity.setContentView(editText)
        controller.setup()

        (editText.getChildAt(0) as EditText).setText("1")
        (editText.getChildAt(1) as EditText).setText("2")
        (editText.getChildAt(2) as EditText).setText("3")
        (editText.getChildAt(4) as EditText).setText("5")
        (editText.getChildAt(3) as EditText).setText("4")

        assertEquals(0, invokesCount)
    }

    @Test
    fun checkThatDigitInsertedWhenCursorLocatesAtEndOfBlock() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        editText.children.forEach { (it as EditText).setText("0") }
        (editText.getChildAt(0) as EditText).setText("01")

        assertEquals("0", (editText.getChildAt(0) as EditText).text.toString())
        assertEquals("1", (editText.getChildAt(1) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(2) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(3) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(4) as EditText).text.toString())
        assertEquals(1, (editText.getChildAt(1) as EditText).selectionStart)
        assertFalse(editText.getChildAt(0).isFocused)
        assertFalse(editText.getChildAt(2).isFocused)
        assertFalse(editText.getChildAt(3).isFocused)
        assertFalse(editText.getChildAt(4).isFocused)
        assertTrue(editText.getChildAt(1).isFocused)
    }

    @Test
    fun checkThatDigitReplacedWhenCursorLocatesAtStartOfBlock() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        editText.children.forEach { (it as EditText).setText("0") }
        (editText.getChildAt(0) as EditText).let {
            val watcher = shadowOf(it).watchers[0]

            it.removeTextChangedListener(watcher)
            it.setText("10")
            it.setSelection(1)
            watcher.afterTextChanged(it.text as Editable)
        }

        assertEquals("1", (editText.getChildAt(0) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(1) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(2) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(3) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(4) as EditText).text.toString())
        assertEquals(0, (editText.getChildAt(1) as EditText).selectionStart)
        assertFalse(editText.getChildAt(0).isFocused)
        assertFalse(editText.getChildAt(2).isFocused)
        assertFalse(editText.getChildAt(3).isFocused)
        assertFalse(editText.getChildAt(4).isFocused)
        assertTrue(editText.getChildAt(1).isFocused)
    }

    @Test
    fun checkThatAfterDigitRemovingPreviousBlockIsFocused() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        editText.children.forEach { (it as EditText).setText("0") }
        (editText.getChildAt(4) as EditText).let {
            it.setSelection(1)
            editText.onKey(it, KeyEvent.KEYCODE_DEL, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            it.text = null
        }

        assertEquals("0", (editText.getChildAt(0) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(1) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(2) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(3) as EditText).text.toString())
        assertEquals("", (editText.getChildAt(4) as EditText).text.toString())
        assertEquals(1, (editText.getChildAt(3) as EditText).selectionStart)
        assertFalse(editText.getChildAt(0).isFocused)
        assertFalse(editText.getChildAt(2).isFocused)
        assertTrue(editText.getChildAt(3).isFocused)
        assertFalse(editText.getChildAt(4).isFocused)
        assertFalse(editText.getChildAt(1).isFocused)
    }

    @Test
    fun checkThatPreviousBlockFocusedAndCleanedAfterDeleteKeyDown() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        for (i in 0 until 4) {
            (editText.getChildAt(i) as EditText).setText("0")
        }

        editText.onKey(editText.getChildAt(4) as EditText, KeyEvent.KEYCODE_DEL, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))

        assertEquals("0", (editText.getChildAt(0) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(1) as EditText).text.toString())
        assertEquals("0", (editText.getChildAt(2) as EditText).text.toString())
        assertEquals("", (editText.getChildAt(3) as EditText).text.toString())
        assertEquals("", (editText.getChildAt(4) as EditText).text.toString())
        assertFalse(editText.getChildAt(0).isFocused)
        assertFalse(editText.getChildAt(2).isFocused)
        assertFalse(editText.getChildAt(4).isFocused)
        assertFalse(editText.getChildAt(1).isFocused)
        assertTrue(editText.getChildAt(3).isFocused)
    }

    @Test
    fun testStateSaving() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = CodeEditText(activity)

        editText.id = View.generateViewId()
        activity.setContentView(editText)
        controller.setup()
        editText.children.forEachIndexed { index, view -> (view as EditText).setText(index.toString()) }

        val bundle = Bundle()
        controller.saveInstanceState(bundle)
        controller.pause()
        controller.stop()
        controller.destroy()

        val newController = Robolectric.buildActivity(Activity::class.java)
        val newActivity = newController.get()
        val newEditText = CodeEditText(newActivity)

        newEditText.id = editText.id
        newActivity.setContentView(newEditText)
        newController.setup(bundle)

        assertEquals("0", (newEditText.getChildAt(0) as EditText).text.toString())
        assertEquals("1", (newEditText.getChildAt(1) as EditText).text.toString())
        assertEquals("2", (newEditText.getChildAt(2) as EditText).text.toString())
        assertEquals("3", (newEditText.getChildAt(3) as EditText).text.toString())
        assertEquals("4", (newEditText.getChildAt(4) as EditText).text.toString())
        assertFalse(newEditText.getChildAt(0).isFocused)
        assertFalse(newEditText.getChildAt(1).isFocused)
        assertFalse(newEditText.getChildAt(2).isFocused)
        assertFalse(newEditText.getChildAt(3).isFocused)
        assertTrue(newEditText.getChildAt(4).isFocused)
    }
}