package io.chthonic.price_converter.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.price_converter.App
import io.chthonic.price_converter.business.service.DroidPermissionsService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.SingleSubject
import timber.log.Timber


/**
 * Created by jhavatar on 3/6/17.
 */
abstract class BaseActivity : AppCompatActivity() {

    private val _lastRequestCode: Short = 0 // must be 16 bit
    private val nextRequestCode: Int
        get() {
            return (_lastRequestCode + 1) % Short.MAX_VALUE
        }

    val permissionRequestBus: DroidPermissionsService by App.kodein.lazy.instance<DroidPermissionsService>()

    val permissionResponsePublishers: MutableMap<String, MutableSet<SingleSubject<DroidPermissionsService.PermissionResponse>>> =
            permissionRequestBus.createResponsePublisherMap()

    protected val rxSubs : CompositeDisposable = CompositeDisposable()

    override fun onResume() {
        super.onResume()

        rxSubs.add(permissionRequestBus.permissionRequestObserver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({request: DroidPermissionsService.PermissionRequest ->
                    Timber.d("permission request: " + request)
                    requestPermission(request.permission, nextRequestCode, request.message, request.responsePublisher)
                }, {t: Throwable ->
                    Timber.e(t, "permission request failed")
                })
        )
    }

    override fun onPause() {
        Timber.d("onStop")
        rxSubs.clear()

        super.onPause()
    }


    fun requestPermission(permission: String, requestCode: Int, message: String, responsePublisher: SingleSubject<DroidPermissionsService.PermissionResponse>) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                Timber.d("request permission with rationale")

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertDialog.Builder(this)
                        .setMessage(message)
                        .setPositiveButton("OK", { dialog: DialogInterface, which: Int ->
                            permissionRequestBus.holdResponsePublisher(permissionResponsePublishers, permission, responsePublisher)
                            ActivityCompat.requestPermissions(this,
                                    arrayOf(permission),
                                    requestCode)
                        })
                        .show()

            } else {
                Timber.d("request permission")

                // No explanation needed, we can request the permission.

                permissionRequestBus.holdResponsePublisher(permissionResponsePublishers, permission, responsePublisher)
                ActivityCompat.requestPermissions(this,
                        arrayOf(permission),
                        requestCode)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            Timber.d("permission allready granted")
            permissionRequestBus.submitResponse(responsePublisher, permission, true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {

        Timber.d("onRequestPermissionsResult:  requestCode = $requestCode, permission = ${permissions[0]}, grantResults.size = ${grantResults.size}")
        permissionRequestBus.submitResponse(permissionResponsePublishers, permissions[0], grantResults)
    }

}