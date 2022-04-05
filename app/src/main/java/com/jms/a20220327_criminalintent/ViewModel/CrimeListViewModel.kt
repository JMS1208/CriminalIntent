package com.jms.a20220327_criminalintent.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jms.a20220327_criminalintent.Crime
import com.jms.a20220327_criminalintent.database.CrimeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimeListViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    val crimeListLiveData : LiveData<List<Crime>> = crimeRepository.getCrimes()

    fun putCrimes(){
        CoroutineScope(Dispatchers.IO).launch{
            val list = mutableListOf<Crime>()
            val size = crimeListLiveData.value?.size ?: 1

            for(i in size+1 until (size+11)){
                list += Crime(title="Title$i", requiresPolice = true, isSolved = true)
            }
            crimeRepository.putCrimes(list)

        }
    }

    fun deleteCrime(crime: Crime) {
        crimeRepository.deleteCrime(crime)
    }

}