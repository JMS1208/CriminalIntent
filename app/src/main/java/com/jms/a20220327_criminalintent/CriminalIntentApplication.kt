package com.jms.a20220327_criminalintent

import android.app.Application
import com.jms.a20220327_criminalintent.database.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}