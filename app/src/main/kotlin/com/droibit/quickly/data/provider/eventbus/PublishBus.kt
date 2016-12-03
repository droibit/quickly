package com.droibit.quickly.data.provider.eventbus

import com.jakewharton.rxrelay.PublishRelay
import com.jakewharton.rxrelay.SerializedRelay
import rx.Observable

class PublishBus : RxBus {

    private val relay = SerializedRelay(PublishRelay.create<Any>())

    override val hasObservers: Boolean
        get() = relay.hasObservers()

    override fun asObservable() = relay

    override fun call(value: Any) {
        relay.call(value)
    }
}
