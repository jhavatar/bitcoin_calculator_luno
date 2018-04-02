package io.chthonic.bitcoin.calculator.business.service

import io.chthonic.bitcoin.calculator.business.observer.AppStateChangePublisher
import io.chthonic.bitcoin.calculator.business.observer.AppStateChangeSubject
import io.chthonic.bitcoin.calculator.business.reducer.CalculatorReducer
import io.chthonic.bitcoin.calculator.business.reducer.ExchangeReducer
import io.chthonic.bitcoin.calculator.data.client.StateClient
import io.chthonic.bitcoin.calculator.data.model.AppState
import io.chthonic.bitcoin.calculator.data.model.AppStateReducer

/**
 * Created by jhavatar on 3/1/17.
 */
class StateService: AppStateChangeSubject {

    private val stateClient: StateClient<AppState> by lazy {
        val appStateReducer: AppStateReducer = AppStateReducer.builder()
                .getExchangeStateReducer(ExchangeReducer.create())
                .getCalculatorStateReducer(CalculatorReducer.create())
                .build()
        StateClient<AppState>(appStateReducer)
    }

    val state: AppState
        get() {
            return stateClient.state
        }

    override fun addPublisher(publisher: AppStateChangePublisher<*>) {
        stateClient.addPublisher(publisher)
    }

    fun removePublisher(publisher: AppStateChangePublisher<*>) {
        stateClient.removePublisher(publisher)
    }

    fun addPersister(persister: AppStateChangePersister<*>) {
        stateClient.addPersister(persister)
    }

    fun removePersister(persister: AppStateChangePersister<*>) {
        stateClient.removePersister(persister)
    }

    fun dispatch(action: Any) {
        stateClient.dispatch(action)
    }


    abstract class AppStateChangePersister<P> : StateClient.StateChangePersister<AppState>

}