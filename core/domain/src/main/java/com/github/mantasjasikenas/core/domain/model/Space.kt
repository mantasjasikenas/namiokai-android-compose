package com.github.mantasjasikenas.core.domain.model

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
    var createdBy: String = "",
    var startPeriod: Int = 1,
    var duration: Int = 1,
    var durationUnitType: DurationUnit = DurationUnit.MONTH
) {
    @Exclude
    fun isValid(): Boolean {
        return memberIds.isNotEmpty() && spaceName.isNotBlank()
    }
}

@Serializable
enum class DurationUnit(val title: String, val unit: DateTimeUnit) {
    DAY("Day", DateTimeUnit.DAY),
    WEEK("Week", DateTimeUnit.WEEK),
    MONTH("Month", DateTimeUnit.MONTH),
    YEAR("Year", DateTimeUnit.YEAR)
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