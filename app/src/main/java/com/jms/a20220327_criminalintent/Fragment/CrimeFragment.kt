package com.jms.a20220327_criminalintent.Fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jms.a20220327_criminalintent.Crime
import com.jms.a20220327_criminalintent.ViewModel.CrimeDetailViewModel
import com.jms.a20220327_criminalintent.databinding.FragmentCrimeBinding
import java.util.*
private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_CODE = 0
private const val DIALOG_DATE = "DialogDate"
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    private var callbacks : Callbacks? = null

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy{
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }


    interface Callbacks {
        fun onReplaceFragmentFromCrimeFragment()
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentCrimeBinding.inflate(layoutInflater,container,false)

        titleField = binding.crimeTitle
        dateButton = binding.crimeDate
        solvedCheckBox = binding.crimeSolved

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

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply{
                setTargetFragment(this@CrimeFragment, REQUEST_CODE)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
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