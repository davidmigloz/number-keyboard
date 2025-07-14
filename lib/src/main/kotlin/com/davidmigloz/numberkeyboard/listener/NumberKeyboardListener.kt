package com.davidmigloz.numberkeyboard.listener

import com.davidmigloz.numberkeyboard.data.NumberKeyboardData

interface NumberKeyboardListener {
    fun onUpdated(data: NumberKeyboardData)
}