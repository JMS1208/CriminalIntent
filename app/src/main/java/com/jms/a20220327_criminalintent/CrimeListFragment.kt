package com.jms.a20220327_criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jms.a20220327_criminalintent.ViewModel.CrimeListViewModel
import com.jms.a20220327_criminalintent.databinding.FragmentCrimeListBinding
import com.jms.a20220327_criminalintent.databinding.ListItemCrimeBinding


class CrimeListFragment : Fragment() {
    private lateinit var crimeRecyclerView: RecyclerView

    private var adapter: CrimeAdapter? = null
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener{
                Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            }

            //registerForContextMenu(view)
            //onCreateContextMenu()

        }
        private val titleTextView: TextView = view.findViewById(R.id.crime_title)
        private val dateTextView: TextView = view.findViewById(R.id.crime_date)

        private lateinit var crime: Crime

        fun bind(crime: Crime){
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toString()
        }


    }

    private inner class CrimeAdapter(var crimes: List<Crime>)
        : RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val binding = ListItemCrimeBinding.inflate(layoutInflater, parent,false)
            return CrimeHolder(binding.root)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int = crimes.size


    }

    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)

        crimeRecyclerView = binding.crimeRecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return binding.root
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}