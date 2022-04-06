package com.jms.a20220327_criminalintent.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.format.DateFormat
import android.util.LayoutDirection
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jms.a20220327_criminalintent.Crime
import com.jms.a20220327_criminalintent.R
import com.jms.a20220327_criminalintent.ViewModel.CrimeListViewModel
import com.jms.a20220327_criminalintent.databinding.FragmentCrimeListBinding
import com.jms.a20220327_criminalintent.databinding.ListItemCrimeBinding
import com.jms.a20220327_criminalintent.databinding.ListItemCrimeRequirePoliceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
        val pullToRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.pullToRefreshLayout)
        pullToRefreshLayout.setOnRefreshListener {
            pullToRefreshLayout.isRefreshing = true
            CoroutineScope(Dispatchers.Main).launch {
                async {
                    updateUI()
                    true
                }.join()
                launch {
                    pullToRefreshLayout.isRefreshing = false
                }

            }


        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.crime_title)
        private val dateTextView: TextView = view.findViewById(R.id.crime_date)
        private val isSolved: ImageView = view.findViewById(R.id.isSolved)
        private val callPolice: ImageView? = view.findViewById(R.id.callPoliceButton)

        private lateinit var crime: Crime

        private val positiveListener = DialogInterface.OnClickListener {
                p0, p1 ->
                Toast.makeText(context, R.string.policeIntentMessage,Toast.LENGTH_SHORT).show()
                val uri = Uri.parse(getText(R.string.policeNumber) as String?)
                val intent = Intent(Intent.ACTION_DIAL,uri)
                startActivity(intent)
        }

        init {
            view.setOnClickListener{
                callbacks?.onCrimeSelected(crime.id)
                // 디테일로 들어가는 거
            }

            view.setOnLongClickListener{ it ->
                val popupMenu : PopupMenu = PopupMenu(context,it)
                activity?.menuInflater?.inflate(R.menu.recy_menu,popupMenu.menu)


                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.deleteDataMenu -> {
                            Toast.makeText(context,"삭제합니다",Toast.LENGTH_SHORT).show()
                            crimeListViewModel.deleteCrime(crime)
                            true
                        }
                        else -> true
                    }
                }
                popupMenu.gravity = Gravity.END

                //popupMenu.show()
                //팝업 메뉴 띄우는거
                true
            }

            view.findViewById<ImageView>(R.id.trashBinImage)?.setColorFilter(
                Color.parseColor("#FFFFFFFF"),
                PorterDuff.Mode.SRC_OUT)

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
                Toast.makeText(context, R.string.isSolvedMessage,Toast.LENGTH_SHORT).apply{
                    setGravity(Gravity.CENTER,0,0)
                    show()
                }
            }


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
            @RequiresApi(Build.VERSION_CODES.M)
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

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
                val crime = crimes[position]
                holder.bind(crime)
            }

            override fun getItemCount(): Int = crimes.size


        }

        private fun updateUI(crimes: List<Crime> = crimeListViewModel.crimeListLiveData.value?: emptyList()) {

            adapter = CrimeAdapter(crimes)
            crimeRecyclerView.adapter = adapter
            crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        }




        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)
            crimeRecyclerView = binding.crimeRecyclerView

            val list = emptyList<Crime>()

            updateUI(list)



            binding.addDatabaseBtn.setOnClickListener {
                crimeListViewModel.putCrimes()
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
