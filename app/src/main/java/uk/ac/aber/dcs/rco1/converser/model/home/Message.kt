package uk.ac.aber.dcs.rco1.converser.model.home

/**
 * TODO
 *
 */
class Message() {

    var originalMessage: String? = null
    var translatedMessage: String? = null
    var language: Char? = null

    /**
     *
     */
    constructor(originalMessage: String?, translatedMessage: String?, language: Char?) : this() {
        this.originalMessage = originalMessage
        this.translatedMessage = translatedMessage
        this.language = language
    }
}