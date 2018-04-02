package io.chthonic.price_converter.ui.activity

import android.graphics.PorterDuff
import android.view.Menu
import android.view.MenuItem
import io.chthonic.mythos.mvp.MVPDispatcher
import io.chthonic.mythos.mvp.PresenterCacheLoaderCallback
import io.chthonic.price_converter.R
import io.chthonic.price_converter.ui.presenter.MainPresenter
import io.chthonic.price_converter.ui.vu.MainVu
import timber.log.Timber


class MainActivity : MVPActivity<MainPresenter, MainVu>() {

    companion object {
        private val MVP_UID by lazy {
            MainActivity::class.hashCode()
        }
    }

    override fun createMVPDispatcher(): MVPDispatcher<MainPresenter, MainVu> {

        return MVPDispatcher(MVP_UID,
                PresenterCacheLoaderCallback(this, { MainPresenter() }),
                ::MainVu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        // manually tint icon since xml tint does not work
        val item = menu.findItem(R.id.action_refresh)
        val icon = resources.getDrawable(R.drawable.ic_refresh_black_24dp)
        icon.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        item.icon = icon
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Timber.d("onOptionsItemSelected: id = ${item.itemId}, action_refresh = ${R.id.action_refresh}")
        when (item.itemId) {
            R.id.action_refresh -> {
                mvpDispatcher.presenter?.fetchLatestTickers(true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
