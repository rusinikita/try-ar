package com.nikita.tryar.item_info

import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nikita.tryar.Events
import com.nikita.tryar.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

data class ItemInfo(val title: String,
                    val subtitle: String,
                    val description: String)

class ItemInfoDelegate(view: View, private val behavior: BottomSheetBehavior<LinearLayout>) {

    val titleText = view.findViewById(R.id.item_title) as TextView
    val subtitleText = view.findViewById(R.id.item_subtitle) as TextView
    val descriptionText = view.findViewById(R.id.item_description) as TextView

    val events = Events.recognitions
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                onEvent(it)
            }

    private fun onEvent(id: String) {
        val data = getData(id)
        setContent(data)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getData(id: String): ItemInfo {
        return when (id.toLowerCase()) {
            "test" -> ItemInfo(
                    title = "Title",
                    subtitle = "Subtitle",
                    description = "Description")
            else -> ItemInfo("Ошибка", "Не удалось загрузить данные.", "")
        }
    }

    private fun setContent(data: ItemInfo) {
        data.run {
            titleText.text = title
            subtitleText.text = subtitle
            descriptionText.text = description
        }
    }
}