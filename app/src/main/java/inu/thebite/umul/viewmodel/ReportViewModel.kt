package inu.thebite.umul.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class ReportViewModel : ViewModel() {
    private val _touchedDay = MutableLiveData<LocalDate>()
    val touchedDay: LiveData<LocalDate> = _touchedDay

    fun updateCalendarDay(newData: LocalDate){
        _touchedDay.value = newData
    }
}