package homework.autoreviewer.core.ai.model

import homework.autoreviewer.core.types.AiName

object AiModel {
    private var model: AiName = AiName.CHATGPT
    fun getModel(): AiName {
        return model
    }

    fun setModel(model: AiName) {
        this.model = model
    }
}