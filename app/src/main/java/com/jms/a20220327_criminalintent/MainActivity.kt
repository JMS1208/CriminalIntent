package com.jms.a20220327_criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jms.a20220327_criminalintent.Fragment.CrimeFragment
import com.jms.a20220327_criminalintent.Fragment.CrimeListFragment
import com.jms.a20220327_criminalintent.databinding.ActivityMainBinding
import java.util.*

private val TAG = "메인 액티비티"
class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    lateinit var binding: ActivityMainBinding

    private fun onCreateFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack(null)
            .commit()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if(currentFragment == null) {
            val fragment = CrimeListFragment()
            onCreateFragment(fragment)
        }
    }

    override fun onCrimeSelected(uuid: UUID) {
        val fragment = CrimeFragment.newInstance(uuid)
        onCreateFragment(fragment)

    }

    override fun onReplaceFragmentFromCrimeFragment() {
        val fragment = CrimeListFragment()
        onCreateFragment(fragment)
    }




}