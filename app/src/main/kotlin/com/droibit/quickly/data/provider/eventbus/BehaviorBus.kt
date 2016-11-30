package com.droibit.quickly.data.provider.eventbus

import com.jakewharton.rxrelay.BehaviorRelay
import rx.Observable


class BehaviorBus : RxBus {

    object Nothing

    private val relay: BehaviorRelay<Any> = BehaviorRelay.create()

    override val hasObservers: Boolean
        get() = relay.hasObservers()

    override fun asObservable(): Observable<Any> {
        return relay.filter { it != Nothing }
                .doOnNext { relay.call(Nothing) }
    }

    override fun call(value: Any) = relay.call(value)
}