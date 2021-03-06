package com.nikita.tryar.item_info

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.nikita.tryar.Events
import com.nikita.tryar.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

data class ItemInfo(val title: String,
                    val subtitle: String,
                    val description: String,
                    val instagramUrl: String,
                    val photos: List<Int>?)

class ItemInfoDelegate(view: View, private val behavior: BottomSheetBehavior<NestedScrollView>, private val context: Context) {

    private val titleText = view.findViewById(R.id.item_title) as TextView
    private val subtitleText = view.findViewById(R.id.item_subtitle) as TextView
    private val descriptionText = view.findViewById(R.id.item_description) as TextView
    private val photoRecycler = view.findViewById(R.id.photo_recycler) as RecyclerView
    private val instagramButton = view.findViewById(R.id.instagram_button) as ImageView
    private val photoContainer = view.findViewById(R.id.photo_container) as LinearLayout

    val events = Events.recognitions
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .subscribe {
                onEvent(it)
            }

    fun init() {
        behavior.peekHeight = 0
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun initViews() {
        photoRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    private fun onEvent(id: String) {
        val data = getData(id)
        initViews()
        setContent(data)
        behavior.peekHeight = 180
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun getData(id: String): ItemInfo {
        return when (id) {
            "VuMark00" -> ItemInfo(
                    title = "Доска",
                    subtitle = "Отличная доска для хакатонов",
                    description = "Можно что-нибудь на ней написать",
                    instagramUrl = "https://www.instagram.com/p/BUl0Sw8Bhjm/",
                    photos = listOf(R.drawable.stand1, R.drawable.stand2, R.drawable.stand3))
            "VuMark01" -> ItemInfo(
                    title = "Телевизор",
                    subtitle = "Отличный телек",
                    description = "Диагональ то какая!",
                    instagramUrl = "https://www.instagram.com/explore/tags/angelhack/",
                    photos = listOf(R.drawable.tv1, R.drawable.tv2, R.drawable.tv3))
            "VuMark02" -> ItemInfo(
                    title = "Canon LV-8235 UST",
                    subtitle = "Мультимедийный проектор",
                    description = "Универсальный ультракороткофокусный LV-8235 UST – 3D-совместимый с поддержкой разрешения WXGA – дает большое изображение с короткого расстояния проекции, что идеально для применения в бизнес- или учебной среде.",
                    instagramUrl = "https://www.instagram.com/canon.russia/?hl=ru",
                    photos = listOf(R.drawable.obj_1_1, R.drawable.obj_1_2, R.drawable.obj_1_3, R.drawable.obj_1_4))

            "VuMark03" -> ItemInfo(
                    title = "Стул разработчика",
                    subtitle = "Четыре исследователя обнаружили этот стул в 201 кабинете Фабрики, в 2017 году.",
                    description = "Этот стул примичателен тем, что на нём сидел один из разработчиков этого приложения. Делал он это с 27.05.2017 по 28.05.2017, после чего связь со стулом была потеряна и они больше не виделись.",
                    instagramUrl = "https://www.instagram.com/appkode/",
                    photos = listOf(R.drawable.photo1, R.drawable.photo2, R.drawable.photo3, R.drawable.photo4))
            else -> ItemInfo("Ошибка", "Не удалось загрузить данные.", "", "", null)
        }
    }

    private fun setContent(data: ItemInfo) {
        data.run {
            titleText.text = title
            subtitleText.text = subtitle
            descriptionText.text = description

            instagramButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(data.instagramUrl)
                context.startActivity(intent)
            }

            if (photos != null && photos.isNotEmpty()) {
                val photoAdapter = PhotoAdapter(photos)
                photoRecycler.adapter = photoAdapter
                photoAdapter.notifyDataSetChanged()
                photoRecycler.visibility = View.VISIBLE
            } else {
                photoContainer.visibility = View.INVISIBLE
            }

        }
    }
}