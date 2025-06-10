package com.dilekbaykara.tasky.features.shared.domain.model
data class OptionItem(
    val type: OptionItemType
)
sealed class OptionItemType {
    data object Open : OptionItemType()
    data object Edit : OptionItemType()
    data object Delete : OptionItemType()
}
