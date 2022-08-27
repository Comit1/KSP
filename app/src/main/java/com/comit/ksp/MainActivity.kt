package com.comit.ksp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.comit.service.ServiceProviders

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = ServiceProviders.getService<IAppService>()
        service?.test()

    }
}