package com.nikita.tryar.navigation.ui

import android.app.Activity
import android.os.Bundle
import com.nikita.tryar.R

class NavActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)
    }
}