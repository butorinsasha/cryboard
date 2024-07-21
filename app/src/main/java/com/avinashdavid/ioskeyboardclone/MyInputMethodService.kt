package com.avinashdavid.ioskeyboardclone

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View

private const val KEY: Int = 1

class MyInputMethodService : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private var keyboardView: KeyboardView? = null
    private var keyboard: Keyboard? = null
    private var caps = false
    private var cryptogram = charArrayOf()
    private var message = charArrayOf()

    override fun onPress(p0: Int) {
    }

    override fun onRelease(p0: Int) {
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val inputConnection = currentInputConnection
        if (inputConnection != null) {
            when (primaryCode) {

                Keyboard.KEYCODE_DELETE -> {
                    val selectedText = inputConnection.getSelectedText(0)
                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(2, 0)
                    } else {
                        inputConnection.commitText("", 1)
                    }
                }

                Keyboard.KEYCODE_SHIFT -> {
                    caps = !caps
                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }

                Keyboard.KEYCODE_DONE -> inputConnection.sendKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER
                    )
                )

                44 -> {
                    Log.i(this.toString(), "cryptogram = ${cryptogram.concatToString()}")
                    Log.i(this.toString(), "message = ${message.concatToString()}")
                    inputConnection.commitText(cryptogram.concatToString(), 1)
                    cryptogram = charArrayOf()
                    message = charArrayOf()
                }

                else -> {
                    var code = primaryCode.toChar()
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code)
                    }
                    val encryptedCode = encryptChar(code, KEY)
                    this.cryptogram = cryptogram.plus(encryptedCode)
                    this.message = message.plus(code)
                    updateInputKeyView()
                }
            }
        }
    }
    private fun updateInputKeyView() {
        if (keyboardView == null) return
        val currentKeyboard: Keyboard? = keyboardView?.keyboard
        val keys = currentKeyboard?.keys
        keys?.get(0)?.label = message.concatToString()
        keyboardView?.invalidateKey(0)
    }


    override fun onText(p0: CharSequence?) {
    }

    override fun swipeLeft() {
    }

    override fun swipeRight() {
    }

    override fun swipeDown() {
    }

    override fun swipeUp() {
    }

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        keyboard = Keyboard(this, R.xml.keys_layout)
        keyboardView!!.apply {
            keyboard = this@MyInputMethodService.keyboard
            setOnKeyboardActionListener(this@MyInputMethodService)
        }
        return keyboardView!!
    }

    private fun encryptMessage(message: String, key: Int): String {
        val messageCharArray = message.toCharArray()
        val cryptogramCharArray = charArrayOf()
        for (ch in messageCharArray) {
            cryptogramCharArray.plus(ch + key)
        }
        return cryptogramCharArray.toString()
    }

    private fun encryptChar(char: Char, key: Int): Char {
        return char + key
    }
}