package com.github.mantasjasikenas.core.domain.model.space

import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.period.generateCurrentPeriod
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

@Serializable
data class Space(
    @DocumentId
    val spaceId: String = "",
    var spaceName: String = "",
    var memberIds: List<String> = emptyList(),
    var destinations: List<Destination> = emptyList(),
    var createdBy: String = "",
    var recurrenceStart: Int = 1,
    var recurrenceUnit: RecurrenceUnit = RecurrenceUnit.MONTHLY
) {
    @Exclude
    fun isValid(): Boolean {
        return memberIds.isNotEmpty() && spaceName.isNotBlank() && (destinations.isEmpty() || destinations.all { it.isValid() }) &&
                recurrenceStart in recurrenceUnit.allowedStartValues()
    }

    @Exclude
    fun currentPeriod(): Period {
        return generateCurrentPeriod(
            recurrenceStartValue = recurrenceStart,
            recurrenceUnit = recurrenceUnit
        )
    }
}

