package com.davidmiguel.numberkeyboard.listener

import com.davidmiguel.numberkeyboard.data.NumberKeyboardData

interface NumberKeyboardListener {
    fun onUpdated(data: NumberKeyboardData)
}