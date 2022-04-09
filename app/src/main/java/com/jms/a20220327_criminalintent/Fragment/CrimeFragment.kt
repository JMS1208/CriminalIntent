package com.jms.a20220327_criminalintent.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.jms.a20220327_criminalintent.Model.Crime
import com.jms.a20220327_criminalintent.MainActivity
import com.jms.a20220327_criminalintent.R
import com.jms.a20220327_criminalintent.ViewModel.CrimeDetailViewModel
import com.jms.a20220327_criminalintent.databinding.FragmentCrimeBinding
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_CODE = 0
private const val DIALOG_DATE = "DialogDate"
private const val DATE_FORMAT = "yyyy.MM.dd(E) hh:mm"
private const val REQUEST_CONTACT = 1


class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var backToListBtn: Button
    private lateinit var saveThisDetail: Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button

    private var callbacks : Callbacks? = null

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy{
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }


    interface Callbacks {
        fun onReplaceFragmentFromCrimeFragment()
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if(crime.suspect.isBlank()) {
          getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)

    }

    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = DateFormat.format("yyyy.MM.dd(E) hh:mm",crime.date)
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if(crime.suspect.isNotEmpty()){
            suspectButton.text = getString(R.string.crime_suspect_button_text_whole,crime.suspect)
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer{
                it?.let{
                    this.crime = it
                    updateUI()
                }
            }
        )
        (context as MainActivity).supportActionBar?.hide()
    }

    private fun initView(binding: FragmentCrimeBinding){
        titleField = binding.crimeTitle
        dateButton = binding.crimeDate
        solvedCheckBox = binding.crimeSolved
        backToListBtn = binding.backToListBtn
        saveThisDetail = binding.saveThisDetailBtn
        reportButton = binding.crimeReport
        suspectButton = binding.crimeSuspectButton

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentCrimeBinding.inflate(layoutInflater,container,false)

        initView(binding)

        dateButton.apply {
            text = crime.date.toString()

        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked

            }
        }

        backToListBtn.setOnClickListener {
            callbacks?.onReplaceFragmentFromCrimeFragment()
            Toast.makeText(context,"저장되었습니다.",Toast.LENGTH_SHORT).show()
        }

        saveThisDetail.setOnClickListener {
            callbacks?.onReplaceFragmentFromCrimeFragment()
            Toast.makeText(context,"저장되었습니다.",Toast.LENGTH_SHORT).show()
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply{
                setTargetFragment(this@CrimeFragment, REQUEST_CODE)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply{
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.crime_report_subject))
            }.also{
                intent ->
                val chooserIntent = Intent.createChooser(intent,getString(R.string.send_report))

                startActivity(chooserIntent)
            }
        }

        suspectButton.setOnClickListener {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(pickContactIntent, REQUEST_CONTACT)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data?.data != null -> {

                val uri:Uri = data.data ?: return

                val queryFields = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER)

                val cursor = requireActivity().contentResolver.query(
                    uri, queryFields, null, null, null)

                cursor?.use {
                    if(it.count == 0) return

                    it.moveToFirst()
                    val name = it.getString(0)
                    val phoneNumber = it.getString(1)

                    val suspect = getString(R.string.crime_suspect,name,phoneNumber)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    updateUI()


                }

            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
        (context as MainActivity).supportActionBar?.show()
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID,crimeId)
            }

            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}