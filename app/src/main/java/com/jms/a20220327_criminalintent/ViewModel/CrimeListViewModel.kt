package com.jms.a20220327_criminalintent.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jms.a20220327_criminalintent.Model.Crime
import com.jms.a20220327_criminalintent.database.CrimeRepository

class CrimeListViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    val crimeListLiveData : LiveData<MutableList<Crime>> = crimeRepository.getCrimes()


    fun deleteCrime(crime: Crime) {
        crimeRepository.deleteCrime(crime)
    }

    fun addCrime(crime: Crime){
        crimeRepository.addCrime(crime)
    }

}