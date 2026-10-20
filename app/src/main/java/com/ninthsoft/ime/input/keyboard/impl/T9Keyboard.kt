package com.ninthsoft.ime.input.keyboard.impl

import android.annotation.SuppressLint
import android.content.Context
import com.ninthsoft.ime.data.PunctuationMode
import com.ninthsoft.ime.data.keyboard.theme.KeyboardColors
import com.ninthsoft.ime.engine.data.CandidatePinYin
import com.ninthsoft.ime.input.keyboard.key.KeyboardAction
import com.ninthsoft.ime.input.keyboard.key.KeyDef
import com.ninthsoft.ime.input.keyboard.key.KeyDef.Appearance.Variant
import com.ninthsoft.ime.input.keyboard.key.backspaceKey
import com.ninthsoft.ime.input.keyboard.key.layoutSwitchKey
import com.ninthsoft.ime.input.keyboard.key.mixedAlphabetKey
import com.ninthsoft.ime.input.keyboard.key.returnKey
import com.ninthsoft.ime.input.keyboard.key.schemaSwitchKey
import com.ninthsoft.ime.input.keyboard.key.segmentKey
import com.ninthsoft.ime.input.keyboard.key.sidePannelKey
import com.ninthsoft.ime.input.keyboard.key.spaceKey
import com.ninthsoft.ime.input.keyboard.key.newlineKey

@SuppressLint("ViewConstructor")
class T9Keyboard(
    context: Context,
    colors: KeyboardColors.ColorScheme,
) : BaseKeyboard(context, colors, Layout), ISidePanelKeyboard {
    private val fullWidthPunctuations = listOf("，", "。", "！", "？", "：", "~", "...")
    private val halfWidthPunctuations = listOf(",", ".", "!", "?", ":", "~", "...")
    var punctuations = fullWidthPunctuations
    private var state: PunctuationMode = PunctuationMode.FullWidth

    init {
        this.updatePunctuationMode(state)
        this.setSidePanelItemListener { action -> this.onAction(action) }
    }

    override fun onPossibleCandidatePinYin(data: List<CandidatePinYin>) {
        if (data.isEmpty()) {
            super.updateSidePanel(
                punctuations.map { ch ->
                    KeyDef(
                        appearance = KeyDef.Appearance.Text(
                            displayText = ch,
                            textSize = 15f,
                            percentWidth = 0.5f,
                            margin = false,
                            variant = Variant.Alternative
                        ),
                        behaviors = setOf(KeyDef.Behavior.Press(KeyboardAction.CommitAction(ch))),
                    )
                })
            return
        }
        super.updateSidePanel(data.map { pinYin ->
            KeyDef(
                appearance = KeyDef.Appearance.Text(
                    displayText = pinYin.pinYin,
                    textSize = 15f,
                    percentWidth = 0.5f,
                    margin = false,
                ),
                behaviors = setOf(
                    KeyDef.Behavior.Press(KeyboardAction.SelectCandidatePinYin(pinYin = pinYin))
                ),
            )
        })
    }

    companion object {
        const val NAME = "T9"

        val Layout: List<List<KeyDef>> = listOf(
    listOf(
        sidePannelKey(percentWidth = 0.09f, rowSpan = 3, visableRow = 4),
        segmentKey(percentWidth = 0.27f),
        mixedAlphabetKey("2", "ABC", percentWidth = 0.27f),
        mixedAlphabetKey("3", "DEF", percentWidth = 0.27f),
        backspaceKey(percentWidth = 0.10f),
    ),
    listOf(
        mixedAlphabetKey("4", "GHI", percentWidth = 0.27f),
        mixedAlphabetKey("5", "JKL", percentWidth = 0.27f),
        mixedAlphabetKey("6", "MNO", percentWidth = 0.27f),
        newlineKey(percentWidth = 0.10f),
    ),
    listOf(
        mixedAlphabetKey("7", "PQRS", percentWidth = 0.27f),
        mixedAlphabetKey("8", "TUV", percentWidth = 0.27f),
        mixedAlphabetKey("9", "WXYZ", percentWidth = 0.27f),
        returnKey(percentWidth = 0.10f, rowSpan = 2),
    ),
    listOf(
        layoutSwitchKey("?123", NumberKeyboard.NAME, percentWidth = 0.12f),
        layoutSwitchKey("!#?", SymbolKeyboard.NAME, percentWidth = 0.10f),
        spaceKey(percentWidth = 0.55f),
        schemaSwitchKey(0.13f),
    ),
)
    }

    override fun name(): String {
        return NAME
    }

    override fun onAttach() {
        this.onPossibleCandidatePinYin(emptyList())
        super.onAttach()
    }

    override fun updatePunctuationMode(mode: PunctuationMode) {
        val changed = mode != state
        state = mode
        punctuations = when (mode) {
            PunctuationMode.FullWidth -> fullWidthPunctuations
            PunctuationMode.HalfWidth -> halfWidthPunctuations
        }
        if (changed) {
            this.onPossibleCandidatePinYin(emptyList())
        }
        super.updatePunctuationMode(mode)
    }
}
