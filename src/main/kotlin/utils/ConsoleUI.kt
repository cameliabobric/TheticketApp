package utils


object ConsoleUI {

    fun showMessage(message: String) {
        println(message)
    }

    fun showError(message: String) {
        println("❌ ERROR: $message")
    }

    fun showSuccess(message: String) {
        println("✅ SUCCESS: $message")
    }

    fun showWarning(message: String) {
        println("⚠️ WARNING: $message")
    }

    fun showInfo(message: String) {
        println("ℹ️ INFO: $message")
    }

    fun getUserInput(prompt: String): String {
        print(prompt)
        return readLine() ?: ""
    }

    fun clearScreen() {
        repeat(50) { println() }
    }

    fun showSeparator(length: Int = 50) {
        println("=".repeat(length))
    }
}
