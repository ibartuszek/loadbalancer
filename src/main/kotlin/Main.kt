import mu.KotlinLogging

class Main {
    companion object {

        private val logger = KotlinLogging.logger { }
        @JvmStatic
        fun main(@Suppress("unused_parameter") args: Array<String>) {
            logger.info("Hello World!")
        }
    }

}
