package com.avinashdavid.ioskeyboardclone

import android.content.ClipboardManager
import android.content.Context
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
    private var cryptogramCharArray = charArrayOf()
    private var messageCharArray = charArrayOf()

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
                        inputConnection.deleteSurroundingText(1, 0)
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

                111111111 -> {
                    Log.i(this.toString(), "cryptogram = ${cryptogramCharArray.concatToString()}")
                    Log.i(this.toString(), "message = ${messageCharArray.concatToString()}")
                    inputConnection.commitText(cryptogramCharArray.concatToString(), 1)

                    cryptogramCharArray = charArrayOf()
                    messageCharArray = charArrayOf()
                    updateInputKeyView(messageCharArray.concatToString())
                }

                999999999 -> {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    if (clipboard.hasPrimaryClip()) {
                        val clipData = clipboard.primaryClip
                        if (clipData != null && clipData.itemCount > 0) {
                            if (keyboardView == null) return
                            val currentKeyboard: Keyboard? = keyboardView?.keyboard
                            val keys = currentKeyboard?.keys
                            val cipherText = clipData.getItemAt(0).text?.toString()
                            updateInputKeyView(cipherText)
                        }
                    }
                }

                else -> {
                    var code = primaryCode.toChar()
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code)
                    }
                    val encryptedCode = encryptChar(code, KEY)
                    this.cryptogramCharArray = cryptogramCharArray.plus(encryptedCode)
                    this.messageCharArray = messageCharArray.plus(code)
                    updateInputKeyView(messageCharArray.concatToString())
                }
            }
        }
    }
    private fun updateInputKeyView(message: String?) {
        if (message == null) return
        if (keyboardView == null) return
        val currentKeyboard: Keyboard? = keyboardView?.keyboard
        val keys = currentKeyboard?.keys
        keys?.get(1)?.label = message
        keyboardView?.invalidateKey(1)
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