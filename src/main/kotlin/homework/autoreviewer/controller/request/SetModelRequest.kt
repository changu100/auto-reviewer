package homework.autoreviewer.controller.request

import homework.autoreviewer.core.types.AiName

data class SetModelRequest(
    val model: AiName,
)