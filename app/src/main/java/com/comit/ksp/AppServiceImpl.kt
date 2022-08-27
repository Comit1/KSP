package com.comit.ksp

import android.util.Log
import com.comit.ksp.annotation.Provider

/*
 * Created by Comit on 2022/8/27.
 */
@Provider
class AppServiceImpl : IAppService {

    override fun test() {
        Log.d("TTT", "AppServiceImpl test")
    }
}