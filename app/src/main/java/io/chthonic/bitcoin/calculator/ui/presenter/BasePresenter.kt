package io.chthonic.bitcoin.calculator.ui.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * Created by jhavatar on 3/7/17.
 */
abstract class BasePresenter<V>: io.chthonic.mythos.mvp.Presenter<V>() where V : io.chthonic.mythos.mvp.Vu {

    protected val rxSubs : io.reactivex.disposables.CompositeDisposable by lazy {
        io.reactivex.disposables.CompositeDisposable()
    }

    protected val coroutineScope: CoroutineScope by lazy {
            object: CoroutineScope {
                private val job = SupervisorJob()

                override val coroutineContext: CoroutineContext
                    get() = Dispatchers.Main + job

            }
    }

    override fun onUnlink() {
        rxSubs.clear()
        super.onUnlink()
    }

}