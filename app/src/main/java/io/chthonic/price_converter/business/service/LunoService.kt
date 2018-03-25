package io.chthonic.price_converter.business.service

import io.chthonic.price_converter.data.client.RestClient
import io.chthonic.price_converter.data.model.Ticker
import io.chthonic.price_converter.data.rest.LunoApi
import io.reactivex.Single
import timber.log.Timber

/**
 * Created by jhavatar on 3/25/2018.
 */
class LunoService(private val stateService: StateService) {

    companion object {
        const val BASE_URL = "https://api.mybitx.com/api/1/"
    }

//    val tickers: List<Ticker>
//        get() = stateService.state.tickers.tickers

    private val restClient: RestClient by lazy {
        RestClient(BASE_URL)
    }

    private val lunoApi: LunoApi by lazy {
        restClient.restAdapter.create(LunoApi::class.java)
    }

    fun fetchTickerLot(): Single<List<Ticker>> {
        return lunoApi.getTickers().map {
            it.tickers
        }
    }

}