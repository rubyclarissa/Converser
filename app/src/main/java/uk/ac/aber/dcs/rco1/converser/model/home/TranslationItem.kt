package uk.ac.aber.dcs.rco1.converser.model.home

/**
 * TODO
 *
 */
class TranslationItem() {

    var originalTranslationItem: String? = null
    var translatedTranslationItem: String? = null
    var language: Char? = null

    /**
     *
     */
    constructor(originalMessage: String?, translatedMessage: String?, language: Char?) : this() {
        this.originalTranslationItem = originalMessage
        this.translatedTranslationItem = translatedMessage
        this.language = language
    }
}