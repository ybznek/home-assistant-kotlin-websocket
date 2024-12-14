import com.ybznek.ha.core.HaClient
import kotlinx.coroutines.*
import java.io.File

fun envVar(name: String): String? = System.getenv(name)?.trim()?.ifEmpty { null }

fun main(args: Array<String>) {

    val token = envVar("TOKEN").orEmpty().ifEmpty {
        val tokenFile = args.getOrNull(0) ?: "/tmp/token.txt"
        File(tokenFile).readText().trim()
    }

    // setup connection
    val ha = HaClient(
        host = envVar("HOST") ?: "ha.local",
        port = envVar("PORT")?.toInt() ?: 80,
        path = "/api/websocket",
        token = token
    )

    runBlocking {
        CoroutineScope(Dispatchers.Default).launch {
            ha.start()
        }

        ha.waitForStart()
        println("Started")

        // watch & print changed attributes
        ha.changeListener += ::onChange

        blockForever()
    }
}

private suspend fun blockForever(): Nothing = CompletableDeferred<Nothing>().await()
