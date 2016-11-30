package com.droibit.quickly.data.provider.eventbus

import com.jakewharton.rxrelay.PublishRelay
import com.jakewharton.rxrelay.SerializedRelay
import rx.Observable

class PublishBus : RxBus {

    private val relay = SerializedRelay(PublishRelay.create<Any>())

    override val hasObservers: Boolean
        get() = relay.hasObservers()

    override fun asObservable(): Observable<Any> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun call(value: Any) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
