package io.chthonic.template_kotlin.ui.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.LoaderManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import io.chthonic.mythos.mvp.MVPDispatcher
import io.chthonic.mythos.mvp.Presenter
import io.chthonic.template_kotlin.R
import io.chthonic.template_kotlin.data.model.AppState
import io.chthonic.template_kotlin.ui.vu.DrawerVu
import kotlinx.android.synthetic.main.activity_drawer.*
import timber.log.Timber

/**
 * Created by jhavatar on 3/8/17.
 */
abstract class DrawerMVPActivity<P, V>: BaseActivity(), NavigationView.OnNavigationItemSelectedListener where P : Presenter<V>, V : DrawerVu {

    val mvpDispatcher: MVPDispatcher<P, V> by lazy {
        createMVPDispatcher()
    }

    var toggle: ActionBarDrawerToggle? = null

    /**
     * @return MVPDispatcher instance used to coordinate MVP pattern.
     */
    protected abstract fun createMVPDispatcher(): MVPDispatcher<P, V>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mvpDispatcher.restorePresenterState(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        Timber.d("drawer_layout = ${this.drawer_layout}")
        mvpDispatcher.createVu(this.layoutInflater, this, parentView = this.drawer_layout)

        val vu: V = mvpDispatcher.vu!!
        if (vu.toolbar != null) {
            Timber.d("init toolbar")
            this.setSupportActionBar(vu.toolbar)

            toggle = ActionBarDrawerToggle(this, this.drawer_layout, vu.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            this.drawer_layout.addDrawerListener(toggle!!)
            toggle!!.syncState()
        }

        initNavMenu()
        this.nav_view.setNavigationItemSelectedListener(this)

        supportLoaderManager.initLoader(mvpDispatcher.uid,
                null,
                mvpDispatcher.presenterCache as LoaderManager.LoaderCallbacks<P>)
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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        closeDrawer(GravityCompat.START)
        when (item.itemId) {
        }

        return false
    }



    override fun onBackPressed() {
        if (!this.closeDrawer(GravityCompat.START)) {
            super.onBackPressed()
        }
    }


    fun closeDrawer(drawerGravity: Int): Boolean {
        if (this.drawer_layout.isDrawerOpen(drawerGravity)) {
            this.drawer_layout.closeDrawer(drawerGravity)
            return true
        }

        return false
    }

    fun lockDrawer(lockType: Int) {
        Timber.d("lockDrawer $lockType")
        this.drawer_layout.setDrawerLockMode(lockType)
        toggle?.isDrawerIndicatorEnabled = ((lockType == DrawerLayout.LOCK_MODE_UNLOCKED) || (lockType == DrawerLayout.LOCK_MODE_LOCKED_OPEN))
    }

    fun initNavMenu() {
        val menu: Menu = this.nav_view.menu
        (0 until menu.size())
                .map { menu.getItem(it) }
                .forEach {
                    when (it.itemId) {
                    }
                }
    }


    fun updateNavVisibility(item: MenuItem, isVisible: Boolean): Boolean {
        val change = isVisible != item.isVisible
        if (change) {
            item.isVisible = isVisible
        }
        Timber.d("menuitem ${item.title} visible $isVisible")
        return change
    }

    fun updateNavActions(appState: AppState) {
        updateNavActions(this.nav_view.menu, appState)
    }

    /**
     * recursively travels through menu and submenus
     */
    fun updateNavActions(menu: Menu, appState: AppState): Boolean {
        var change: Boolean = false
        (0 until menu.size())
                .asSequence()
                .map {
                    menu.getItem(it)
//            Timber.d("pos:$pos, ${item.title}, subMenu? = ${item.hasSubMenu()}")
                }
                .forEach {
                    if (it.hasSubMenu()) {
                        change = updateNavActions(it.subMenu, appState) || change

                    } else {
                        when (it.itemId) {
                        }

                    }
                }
//        Timber.d("updateNavActions, change = $change, menu size = ${menu.size()}")
        return change
    }

}