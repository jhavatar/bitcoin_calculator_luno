package io.chthonic.template_kotlin.business.service

import android.content.res.Resources
import android.Manifest
import android.content.pm.PackageManager
import io.chthonic.template_kotlin.R
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject

/**
 * Created by jhavatar on 3/6/17.
 */
class DroidPermissionsService(val appResources: Resources) {
    companion object {
        const val BARCODE_PERMISSION = Manifest.permission.CAMERA
        const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private val requestPublisher: PublishSubject<PermissionRequest> by lazy {
        PublishSubject.create<PermissionRequest>()
    }

    val permissionRequestObserver: Observable<PermissionRequest>
    get() {
        return requestPublisher.hide()
    }


    fun requestPermission(permission: String, meassage: String): Single<PermissionResponse> {
        val responsepublisher: SingleSubject<PermissionResponse> = SingleSubject.create<PermissionResponse>()
        requestPublisher.onNext(PermissionRequest(permission, meassage, responsepublisher))
        return responsepublisher.hide()
    }

    fun requestBarcodePermission(): Single<PermissionResponse> {
        return requestPermission(BARCODE_PERMISSION, appResources.getString(R.string.barcode_permission_message))
    }

    fun requestLocationPermission(): Single<PermissionResponse> {
        return requestPermission(LOCATION_PERMISSION, appResources.getString(R.string.location_permission_message))
    }


    fun createResponsePublisherMap(): MutableMap<String, MutableSet<SingleSubject<PermissionResponse>>> {
        return mutableMapOf(BARCODE_PERMISSION to HashSet<SingleSubject<PermissionResponse>>(),
                LOCATION_PERMISSION to HashSet<SingleSubject<PermissionResponse>>())
    }

    fun holdResponsePublisher(permissionResponsePublishers: MutableMap<String, MutableSet<SingleSubject<PermissionResponse>>>,
                                  permission: String,
                                  responsePublisher: SingleSubject<PermissionResponse>) {

        synchronized(permissionResponsePublishers) {
            permissionResponsePublishers[permission]!!.add(responsePublisher)
        }
    }

    fun submitResponse(permissionResponsePublishers: MutableMap<String, MutableSet<SingleSubject<PermissionResponse>>>,
                                 permission: String,
                                 grantResults: IntArray) {

        val responsePublisherList: MutableList<SingleSubject<PermissionResponse>> = mutableListOf()
        synchronized(permissionResponsePublishers) {
            val responsePublisherSet = permissionResponsePublishers[permission]!!
            responsePublisherList.addAll(responsePublisherSet)
            responsePublisherSet.clear()
        }

        val granted = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        responsePublisherList.forEach { responsePublisher: SingleSubject<DroidPermissionsService.PermissionResponse> ->
            responsePublisher.onSuccess(DroidPermissionsService.PermissionResponse(permission, granted))
        }
    }

    fun submitResponse(responsePublisher: SingleSubject<PermissionResponse>, permission: String, granted:Boolean) {
        responsePublisher.onSuccess(PermissionResponse(permission, granted))
    }

    data class PermissionRequest(val permission: String,
                                 val message: String,
                                 val responsePublisher: SingleSubject<PermissionResponse>)


    data class PermissionResponse(val permission: String,
                                  val granted: Boolean)
}