package com.droibit.quickly.data.provider.eventbus

import rx.Observable

interface RxBus {

    val hasObservers: Boolean

    fun asObservable(): Observable<Any>

    fun call(value: Any)
}