package io.chthonic.bitcoin.calculator.business.observer


/**
 * Created by jhdevaal on 2018/03/24.
 */
interface AppStateChangeSubject {
    fun addPublisher(publisher: AppStateChangePublisher<*>)
}