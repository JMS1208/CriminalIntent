package com.jms.a20220327_criminalintent

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.DateFormat.getDateInstance
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jms.a20220327_criminalintent.ViewModel.CrimeListViewModel
import com.jms.a20220327_criminalintent.database.CrimeRepository
import com.jms.a20220327_criminalintent.databinding.FragmentCrimeListBinding
import com.jms.a20220327_criminalintent.databinding.ListItemCrimeBinding
import com.jms.a20220327_criminalintent.databinding.ListItemCrimeRequirePoliceBinding
import java.text.DateFormat.LONG
import java.text.DateFormat.getDateInstance
import java.util.*


class CrimeListFragment : Fragment() {
    private lateinit var crimeRecyclerView: RecyclerView

    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    private var callbacks : Callbacks? = null

    interface Callbacks {
        fun onCrimeSelected(uuid: UUID)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { crimes ->
                crimes?.let{
                    Log.i("확인","개수: ${crimes.size}, 타입: ${crimes::class.java.simpleName}")
                    updateUI(crimes)
            } }
        )
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


    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view) {
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
                callbacks?.onCrimeSelected(crime.id)
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
                DateFormat.format("yyyy.MM.dd hh:mm:ss",crime.date)
                dateTextView.text = DateFormat.format("yyyy.MM.dd(E) a hh:mm",crime.date)
                isSolved.visibility = if(crime.isSolved) {
                    View.VISIBLE
                } else {
                    View.GONE
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

        private fun updateUI(crimes: List<Crime>) {

            adapter = CrimeAdapter(crimes)
            crimeRecyclerView.adapter = adapter
            crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        }


    // 얘는 나중에 지워야됨
        fun addDatabase(){
            val crimeRepository = CrimeRepository.get()

            val list = mutableListOf<Crime>()

            for(i in 0..10){
                list += Crime(title="Crime$i", isSolved = i%2 != 0 , requiresPolice = i%2 != 1)
            }

            crimeRepository.putCrimes(list as List<Crime>)

        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)


            //binding.crimeRecyclerView.adapter = CrimeAdapter(list as List<Crime>)
            //binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)

            crimeRecyclerView = binding.crimeRecyclerView
            crimeRecyclerView.layoutManager = LinearLayoutManager(context)
            crimeRecyclerView.adapter = adapter

            //updateUI()

            binding.addDatabaseBtn.setOnClickListener {
                Thread {
                    addDatabase()
                }.start()
                Toast.makeText(context,"DB추가 완료",Toast.LENGTH_SHORT).show()
            }

            return binding.root
        }


        companion object {
            fun newInstance(): CrimeListFragment {
                return CrimeListFragment()
            }
        }
    }
