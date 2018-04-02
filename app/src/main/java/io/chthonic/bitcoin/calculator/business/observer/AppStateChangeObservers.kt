package io.chthonic.bitcoin.calculator.business.observer

/**
 * Created by jhdevaal on 2018/03/24.
 */
abstract class AppStateChangeObservers {

    abstract protected val publishersToRegister: List<AppStateChangePublisher<*>>

    fun registerPublishers(subject: AppStateChangeSubject) {
        publishersToRegister.forEach {
            subject.addPublisher(it)
        }
    }

}