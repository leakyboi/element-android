/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.riotx.features.autocomplete.emoji

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import im.vector.riotx.EmojiCompatFontProvider
import im.vector.riotx.features.autocomplete.AutocompleteClickListener
import im.vector.riotx.features.reactions.ReactionClickListener
import im.vector.riotx.features.reactions.data.EmojiItem
import javax.inject.Inject

class AutocompleteEmojiController @Inject constructor(
        private val fontProvider: EmojiCompatFontProvider
) : TypedEpoxyController<List<EmojiItem>>() {

    var emojiTypeface: Typeface? = fontProvider.typeface

    private val fontProviderListener = object : EmojiCompatFontProvider.FontProviderListener {
        override fun compatibilityFontUpdate(typeface: Typeface?) {
            emojiTypeface = typeface
        }
    }

    init {
        fontProvider.addListener(fontProviderListener)
    }

    var listener: AutocompleteClickListener<String>? = null

    override fun buildModels(data: List<EmojiItem>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        data
                .take(MAX)
                .forEach { emojiItem ->
                    autocompleteEmojiItem {
                        id(emojiItem.name)
                        emojiItem(emojiItem)
                        emojiTypeFace(emojiTypeface)
                        onClickListener(
                                object : ReactionClickListener {
                                    override fun onReactionSelected(reaction: String) {
                                        listener?.onItemClick(reaction)
                                    }
                                }
                        )
                    }
                }

        if (data.size > MAX) {
            autocompleteMoreResultItem {
                id("more_result")
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        fontProvider.removeListener(fontProviderListener)
    }

    companion object {
        const val MAX = 50
    }
}
