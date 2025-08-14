package com.back.global.exception

import com.back.global.rsData.RsData

class ServiceException(
    private val resultCode: String,
    private val msg: String
) : RuntimeException("$resultCode : $msg") {

    val rsData: RsData<Unit>
        get() = RsData(resultCode, msg)
}
