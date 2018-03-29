package io.chthonic.price_converter.ui.activity

import android.view.Menu
import io.chthonic.mythos.mvp.MVPDispatcher
import io.chthonic.mythos.mvp.PresenterCacheLoaderCallback
import io.chthonic.price_converter.R
import io.chthonic.price_converter.ui.presenter.ConverterPresenter
import io.chthonic.price_converter.ui.vu.ConverterVu


class MainActivity : MVPActivity<ConverterPresenter, ConverterVu>() {

    companion object {
        private val MVP_UID by lazy {
            MainActivity::class.hashCode()
        }
    }

    override fun createMVPDispatcher(): MVPDispatcher<ConverterPresenter, ConverterVu> {

        return MVPDispatcher(MVP_UID,
                PresenterCacheLoaderCallback(this, { ConverterPresenter() }),
                ::ConverterVu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//
//
//        if (id == R.id.action_settings) {
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
}
