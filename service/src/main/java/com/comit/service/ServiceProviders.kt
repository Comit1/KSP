package com.comit.service

import android.util.Log
import java.lang.IllegalArgumentException

/*
 * Created by Comit on 2022/8/26.
 */
object ServiceProviders {

    private val services = HashMap<String, IService>()

    @Volatile
    private var isInit = false

    @Suppress("UNCHECKED_CAST")
    fun init() {
        if (isInit) {
            return
        }
        isInit = true
        val clazz = Class.forName("com.comit.service.ServiceProvidersImpl")
        val method = clazz.getMethod("getServices")
        method.isAccessible = true
        val services = method.invoke(clazz.newInstance()) as Map<String, IService>
        this.services.putAll(services)
    }

    inline fun <reified T : IService> getService(): T? {
        return getService(T::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IService> getService(clazz: Class<T>): T? {
        val superType = clazz.genericInterfaces[0] as Class<*>
        if (!clazz.isInterface || superType != IService::class.java) {
            throw IllegalArgumentException("clazz is not correct.")
        }
        val name = clazz.canonicalName!!
        return services[name] as? T
    }

}