package com.jms.a20220327_criminalintent

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jms.a20220327_criminalintent.ViewModel.CrimeListViewModel
import com.jms.a20220327_criminalintent.databinding.FragmentCrimeListBinding
import com.jms.a20220327_criminalintent.databinding.ListItemCrimeBinding
import com.jms.a20220327_criminalintent.databinding.ListItemCrimeRequirePoliceBinding


class CrimeListFragment : Fragment() {
    private lateinit var crimeRecyclerView: RecyclerView

    private var adapter: CrimeAdapter? = null
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.recy_menu,menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu1 -> {
                Toast.makeText(context, "contextMenu pressed!", Toast.LENGTH_SHORT).show()

                true
            }
            else-> super.onContextItemSelected(item)
        }

    }


    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.crime_title)
        private val dateTextView: TextView = view.findViewById(R.id.crime_date)
        private val isSolved: ImageView = view.findViewById(R.id.isSolved)
        private val callPolice: ImageView? = view.findViewById(R.id.callPoliceButton)

        private lateinit var crime: Crime

        private val positiveListener = DialogInterface.OnClickListener {
                p0, p1 ->
                Toast.makeText(context,R.string.policeIntentMessage,Toast.LENGTH_SHORT).show()
                val uri = Uri.parse(getText(R.string.policeNumber) as String?)
                val intent = Intent(Intent.ACTION_DIAL,uri)
                startActivity(intent)
        }

        init {
            view.setOnClickListener {
                Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
                }
            callPolice?.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setMessage(R.string.callPoliceMessage)
                    setPositiveButton("예",positiveListener)
                    setNegativeButton("아니요",null)
                    setIcon(R.drawable.alert_sign)
                    setTitle("알림")
                    show()
                }

            }
            isSolved.setOnClickListener {
                Toast.makeText(context,R.string.isSolvedMessage,Toast.LENGTH_SHORT).apply{
                    setGravity(Gravity.CENTER,0,0)
                    show()
                }
            }


            registerForContextMenu(view)
        }


            fun bind(crime: Crime) {
                this.crime = crime
                titleTextView.text = crime.title
                dateTextView.text = crime.date.toString()
                if(crime.isSolved) {
                    isSolved.visibility = View.VISIBLE
                } else {
                    isSolved.visibility = View.INVISIBLE
                }

            }


        }

        private inner class CrimeAdapter(var crimes: List<Crime>) :
            RecyclerView.Adapter<CrimeHolder>() {

            private val CRIME_REQUIRE_POLICE = 1
            private val CRIME_NOT_REQUIRE_POLICE = 2

            override fun getItemViewType(position: Int): Int {

                return if(crimes[position].requiresPolice){
                    CRIME_REQUIRE_POLICE
                } else {
                    CRIME_NOT_REQUIRE_POLICE // 이게 뷰타입
                }

            }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {

                return when(viewType) {
                    CRIME_REQUIRE_POLICE-> {
                        val binding = ListItemCrimeRequirePoliceBinding.inflate(layoutInflater, parent, false)
                        CrimeHolder(binding.root)
                    }
                    else-> {
                        val binding = ListItemCrimeBinding.inflate(layoutInflater, parent, false)
                        CrimeHolder(binding.root)
                    }
                }

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
