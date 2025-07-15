package com.davidmiguel.numberkeyboard.data

enum class NumberKeyboardFormat {
    Normal,
    Inverted,
    Scrambled,
    AlwaysScrambled;

    companion object {
        val Default = Normal
    }
}