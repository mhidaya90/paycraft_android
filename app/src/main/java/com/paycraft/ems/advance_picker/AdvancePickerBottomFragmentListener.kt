package com.paycraft.ems.advance_picker

import com.paycraft.ems.advance.list.Advance

interface AdvancePickerBottomFragmentListener {
    fun onAdvanceSelected(advance: Advance)
}