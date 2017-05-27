package com.nikita.tryar

import io.reactivex.subjects.PublishSubject

object Events {
    val recognitions = PublishSubject.create<String>()
}
