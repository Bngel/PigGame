package cn.bngel.piggame.dao

import cn.bngel.piggame.dao.getGameUUID.GetGameUUID
import cn.bngel.piggame.dao.getGameUUIDLast.GetGameUUIDLast
import cn.bngel.piggame.dao.postGame.PostGame
import cn.bngel.piggame.dao.postGameUUID.PostGameUUID
import cn.bngel.piggame.dao.putGameUUID.PutGameUUID
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface GameDao {

    @POST("game")
    fun postGame(
        @Field("private") private: Boolean,
        @Header("Authorization") token: String
    ): Call<DefaultData<PostGame>>

    @POST("game/:{uuid}")
    fun postGameUUID(
        @Path("uuid") uuid: String,
        @Header("Authorization") token: String
    ): Call<DefaultData<PostGameUUID>>

    @PUT("game/:{uuid}")
    fun putGameUUID(
        @Query("type") type: Int,
        @Query("card") card: String,
        @Path("uuid") uuid: String,
        @Header("Authorization") token: String
    ): Call<DefaultData<PutGameUUID>>

    @GET("game/:{uuid}")
    fun getGameUUID(
        @Path("uuid") uuid: String,
        @Header("Authorization") token: String
    ): Call<DefaultData<GetGameUUID>>

    @GET("game/:{uuid}/last")
    fun getGameUUIDLast(
        @Path("uuid") uuid: String,
        @Header("Authorization") token: String
    ): Call<DefaultData<GetGameUUIDLast>>

    @GET("game/index")
    fun getGameIndex(
        @Query("page_size") page_size: String,
        @Query("page_num") page_num: String,
        @Header("Authorization") token: String
    ): Call<DefaultData<GetGameUUIDLast>>

    companion object {
        fun create(): GameDao {
            val baseUrl = "http://172.17.173.97:9000/api/"
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val mHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(mHttpClient)
                .build()
            return retrofit.create(GameDao::class.java)
        }
    }
}