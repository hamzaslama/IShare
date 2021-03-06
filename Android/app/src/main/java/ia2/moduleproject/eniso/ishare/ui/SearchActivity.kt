package ia2.moduleproject.eniso.ishare.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import ia2.moduleproject.eniso.ishare.adapter.SearchAdapter
import ia2.moduleproject.eniso.ishare.model.UserInfo
import ia2.moduleproject.eniso.ishare.R
import ia2.moduleproject.eniso.ishare.utils.BottomNavigationViewHelper
import ia2.moduleproject.eniso.ishare.utils.localhost
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.util.*

class SearchActivity : AppCompatActivity() {
    private val TAG = "SearchActivity"
    private val ACTIVITY_NUM = 2

    var ListUserInfoSearch=ArrayList<UserInfo>()
    var adpater: SearchAdapter?=null
    private val mContext = this@SearchActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        Log.d(TAG, "onCreate: starting.")

        setupBottomNavigationView()
//        search.addTextChangedListener(
//                object : TextWatcher {
//                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//
//                    }
//
//                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                    }
//
//                    override fun afterTextChanged(s: Editable) {
//
//                        val text = search.text.toString().toLowerCase(Locale.getDefault())
//                        searchForMatch(text)
//                    }
//                })
        click_search.setOnClickListener {
            val text = search.text.toString()
            searchForMatch(text) }

        var layoutManager = LinearLayoutManager(mContext)
        recycler_search.layoutManager = layoutManager
        adpater = SearchAdapter(ListUserInfoSearch)
        recycler_search.adapter = adpater

    }
//
//fun searchForMatch(text : String){
//
//}

    /**
     * BottomNavigationView setup
     */
    private fun setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView")
        val bottomNavigationViewEx = findViewById<View>(R.id.bottomNavViewBar) as BottomNavigationViewEx
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx)
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx)
        val menu = bottomNavigationViewEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
    }

    private fun searchForMatch(nameSearch:String) {
        //http://localhost/IshareServer/TweetList.php?op=4&user_name=
        val url= localhost +"/IshareServer/TweetList.php?op=4&user_name="+"\""+nameSearch+"\""


        val jsonObjReq = object : JsonObjectRequest(Method.POST,
                url, null, Response.Listener { response ->
            try {
                if ( response.getString("msg")=="has tweet"){
                    ListUserInfoSearch.clear()
                    val userinfo = JSONArray(response.getString("info"))
                    for (i in 0..userinfo.length()-1){
                        val singleTweet= userinfo.getJSONObject(i)
                        ListUserInfoSearch.add(UserInfo(singleTweet.getString("user_id"),singleTweet.getString("first_name"), singleTweet.getString("email"), singleTweet.getString("picture_path")))
                        adpater!!.notifyDataSetChanged()

                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
                try {
                    var cacheEntry: Cache.Entry? = HttpHeaderParser.parseCacheHeaders(response)
                    if (cacheEntry == null) {
                        cacheEntry = Cache.Entry()
                    }
                    val cacheHitButRefreshed = (3 * 60 * 1000).toLong() // in 3 minutes cache will be hit, but also refreshed on background
                    val cacheExpired = (24 * 60 * 60 * 1000).toLong() // in 24 hours this cache entry expires completely
                    val now = System.currentTimeMillis()
                    val softExpire = now + cacheHitButRefreshed
                    val ttl = now + cacheExpired
                    cacheEntry.data = response.data
                    cacheEntry.softTtl = softExpire
                    cacheEntry.ttl = ttl
                    var headerValue: String?
                    headerValue = response.headers["Date"]
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue)
                    }
                    headerValue = response.headers["Last-Modified"]
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue)
                    }
                    cacheEntry.responseHeaders = response.headers
                    val jsonString = String(response.data)
//                            Charset( HttpHeaderParser.parseCharset(response.headers)))
//                            HttpHeaderParser.parseCacheHeaders(response)
                    return Response.success(JSONObject(jsonString), cacheEntry)
                } catch (e: UnsupportedEncodingException) {
                    return Response.error(ParseError(e))
                } catch (e: JSONException) {
                    return Response.error(ParseError(e))
                }

            }

            override fun deliverResponse(response: JSONObject) {
                super.deliverResponse(response)
            }

            override fun deliverError(error: VolleyError) {
                super.deliverError(error)
            }

            override fun parseNetworkError(volleyError: VolleyError): VolleyError {
                return super.parseNetworkError(volleyError)
            }
        }

        Volley.newRequestQueue(this).add(jsonObjReq)
    }
}