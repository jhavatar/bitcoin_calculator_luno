package io.chthonic.price_converter.data.rest

import io.chthonic.price_converter.data.model.TickerLot
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by jhavatar on 3/25/2018.
 */
interface LunoApi {

    @GET("tickers")
    fun getTickers(): Single<TickerLot>

}