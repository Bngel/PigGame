package cn.bngel.piggame.dao
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import cn.bngel.piggame.dao.postUserLogin.PostUserLogin
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface LoginDao {

    @FormUrlEncoded
    @POST("user/login")
    fun postUserLogin(
        @Field("student_id") studentId: String,
        @Field("password") password: String
    ): Call<DefaultData<PostUserLogin>>

    companion object {
        fun create(): LoginDao {
            val baseUrl = "http://172.17.173.97:8080/api/"
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
            return retrofit.create(LoginDao::class.java)
        }
    }
}