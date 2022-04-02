package com.jms.a20220327_criminalintent.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jms.a20220327_criminalintent.Crime
import com.jms.a20220327_criminalintent.database.CrimeRepository

class CrimeListViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    val crimeListLiveData : LiveData<List<Crime>> = crimeRepository.getCrimes()
}