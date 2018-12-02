package ia2.moduleproject.eniso.ishare

import android.app.Application
import ia2.moduleproject.eniso.ishare.preffManager.SharedPrefsManager

class MyApplication : Application(){

    companion object {

    }
    override fun onCreate() {
        super.onCreate()
        SharedPrefsManager.create(this)
    }
}