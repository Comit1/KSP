package com.comit.ksp

import android.app.Application
import com.comit.service.ServiceProviders

/*
 * Created by Comit on 2022/8/27.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceProviders.init()
    }

}