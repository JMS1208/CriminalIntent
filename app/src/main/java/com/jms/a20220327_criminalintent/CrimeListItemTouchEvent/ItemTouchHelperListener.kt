package com.jms.a20220327_criminalintent.CrimeListItemTouchEvent

interface ItemTouchHelperListener {
    fun onItemMove(from_position: Int, to_position: Int): Boolean
    fun onItemSwipe(position: Int)
}