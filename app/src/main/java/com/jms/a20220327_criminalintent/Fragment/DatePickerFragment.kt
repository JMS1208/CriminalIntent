package com.jms.a20220327_criminalintent.Fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.jms.a20220327_criminalintent.R
import java.util.*

private const val DATE_ARG = "DateArguements"

class DatePickerFragment : DialogFragment() {

    private val calendar = Calendar.getInstance()

    private var initialYear = calendar.get(Calendar.YEAR)
    private var initialMonth = calendar.get(Calendar.MONTH)
    private var initialDay = calendar.get(Calendar.DAY_OF_MONTH)


    interface Callbacks{
        fun onDateSelected(date: Date)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = DatePickerDialog.OnDateSetListener{
                _: DatePicker, year: Int, month: Int, day: Int ->
            val selectedDate: Date = GregorianCalendar(year,month,day).time
            targetFragment?.let{
                (it as Callbacks).onDateSelected(selectedDate)
            }


        }
        return DatePickerDialog(
            requireContext(),
            R.style.MyCustomDatePicker,
            listener,
            initialYear,
            initialMonth,
            initialDay
        )
    }


    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            return DatePickerFragment().apply{
                arguments = Bundle().apply{
                    putSerializable(DATE_ARG,date)
                }
            }
        }
    }

}