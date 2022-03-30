package com.jms.a20220327_criminalintent

import java.util.*

data class Crime(val id: UUID = UUID.randomUUID(),
                var title: String ="",
                var date: Date = Date(),
                 var isSolved: Boolean = false,
                var requiresPolice: Boolean = false)