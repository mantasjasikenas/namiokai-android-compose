package com.github.mantasjasikenas.core.domain.model

import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.period.generateCurrentPeriod
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.datetime.DateTimeUnit
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
        return memberIds.isNotEmpty() && spaceName.isNotBlank() && (destinations.isEmpty() || destinations.all { it.isValid() })
    }

    @Exclude
    fun currentPeriod(): Period {
        return generateCurrentPeriod(
            recurrenceStartValue = recurrenceStart,
            recurrenceUnit = recurrenceUnit
        )
    }
}

@Serializable
enum class RecurrenceUnit(val title: String, val unit: DateTimeUnit) {
    DAILY("Daily", DateTimeUnit.DAY),
    WEEKLY("Weekly", DateTimeUnit.WEEK),
    MONTHLY("Monthly", DateTimeUnit.MONTH),
}

data class Invitation(
    @DocumentId
    val invitationId: String = "",
    val spaceId: String = "",
    val invitedUserId: String = "",
    val invitingUserId: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING
)

enum class InvitationStatus {
    PENDING, ACCEPTED, DECLINED
}