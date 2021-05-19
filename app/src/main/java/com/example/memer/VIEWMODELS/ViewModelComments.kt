package com.example.memer.VIEWMODELS

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelComments : ViewModel() {

    private val _data: ArrayList<Int> = ArrayList()
    private val _dataLiveData : MutableLiveData<ArrayList<Int>> = MutableLiveData<ArrayList<Int>>()




}