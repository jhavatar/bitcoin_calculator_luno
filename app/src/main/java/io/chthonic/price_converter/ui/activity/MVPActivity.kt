package io.chthonic.price_converter.ui.activity

import android.os.Bundle
import android.support.v4.app.LoaderManager
import io.chthonic.mythos.mvp.MVPDispatcher
import io.chthonic.mythos.mvp.Presenter
import io.chthonic.mythos.mvp.Vu

/**
 * Created by jhavatar on 3/6/17.
 */
abstract class MVPActivity<P, V> : BaseActivity() where P : Presenter<V>, V : Vu {
    val mvpDispatcher: MVPDispatcher<P, V> by lazy {
        createMVPDispatcher()
    }

    /**
     * @return MVPDispatcher instance used to coordinate MVP pattern.
     */
    protected abstract fun createMVPDispatcher(): MVPDispatcher<P, V>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mvpDispatcher.restorePresenterState(savedInstanceState)
        mvpDispatcher.createVu(this.layoutInflater, this)
        setContentView(mvpDispatcher.vu!!.rootView)

        if (mvpDispatcher.presenterCache is LoaderManager.LoaderCallbacks<*>) {
            supportLoaderManager.initLoader(mvpDispatcher.uid,
                    null,
                    mvpDispatcher.presenterCache as LoaderManager.LoaderCallbacks<P>)
        }
    }

    override fun onResume() {
        super.onResume()
        mvpDispatcher.linkPresenter(this.intent.extras)
    }

    override fun onPause() {
        mvpDispatcher.unlinkPresenter()
        super.onPause()
    }

    override fun onDestroy() {
        mvpDispatcher.destroyVu()
        super.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        mvpDispatcher.savePresenterState(outState)
    }
}