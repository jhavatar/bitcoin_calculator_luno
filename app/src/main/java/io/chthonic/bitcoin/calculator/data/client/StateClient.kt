package io.chthonic.bitcoin.calculator.data.client

import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.Store
import kotlinx.coroutines.experimental.ThreadPoolDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by jhavatar on 3/14/17.
 */
class StateClient<T>(appStateReducer: Reducer<T>) {

    private var _lastState: T? = null

    private val store: Store<T> = Store.create(appStateReducer)

    private val _publisherList: MutableList<StateChangePublisher<T>> = Collections.synchronizedList(mutableListOf())
    private val _persistenceList: MutableList<StateChangePersister<T>> = Collections.synchronizedList(mutableListOf())

    private val persistenceDispatcher: ThreadPoolDispatcher by lazy {
        newSingleThreadContext("persistence")
    }

    val state: T
        get() = store.state


    init {

        store.subscribe { state: T ->
            val lastState: T? = _lastState
            val publisherList: List<StateChangePublisher<T>> = synchronized(_publisherList) {
                ArrayList(_publisherList)
            }
            val persistencerList: List<StateChangePersister<T>> = synchronized(_publisherList) {
                ArrayList(_persistenceList)
            }

            persistencerList.forEach { pers: StateChangePersister<T> ->
                if (pers.shouldPersist(state, lastState)) {
                    launch(persistenceDispatcher) {
//                        Timber.d("do persist of $state, mainThread = ${Looper.myLooper() == Looper.getMainLooper()}, thread = ${java.lang.Thread.currentThread()}")
                        pers.persist(state)
                    }
                }

            }

            publisherList.forEach { pub: StateChangePublisher<T> ->
                if (pub.shouldPublish(state, lastState)) {
                    pub.publish(state)
                }
            }

            _lastState = state
        }
    }

    fun dispatch(action: Any) {
        store.dispatch(action)
    }

    fun addPublisher(notifier: StateChangePublisher<T>) {
        _publisherList.add(notifier)
    }

    fun removePublisher(notifier: StateChangePublisher<T>) {
        _publisherList.remove(notifier)
    }

    fun addPersister(persister: StateChangePersister<T>) {
        _persistenceList.add(persister)
    }

    fun removePersister(persister: StateChangePersister<T>) {
        _persistenceList.remove(persister)
    }

    interface StateChangePublisher<in S> {
        fun shouldPublish(state: S, oldState: S?): Boolean
        fun publish(state: S)
    }

    interface StateChangePersister<in S> {
        fun shouldPersist(state: S, oldState: S?): Boolean
        fun persist(state: S)
    }

}