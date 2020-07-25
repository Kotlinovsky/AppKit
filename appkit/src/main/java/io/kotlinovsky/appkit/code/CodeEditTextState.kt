package io.kotlinovsky.appkit.code

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class CodeEditTextState : View.BaseSavedState {

    lateinit var ids: IntArray

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        ids = source.createIntArray()!!
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeIntArray(ids)
    }

    companion object CREATOR : Parcelable.Creator<CodeEditTextState> {
        override fun createFromParcel(parcel: Parcel): CodeEditTextState = CodeEditTextState(parcel)
        override fun newArray(size: Int): Array<CodeEditTextState?> = arrayOfNulls(size)
    }
}