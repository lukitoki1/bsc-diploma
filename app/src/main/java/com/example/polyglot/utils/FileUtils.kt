import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.DateFormat
import java.util.*

fun createFileUri(context: Context): Uri {
    val timeStamp: String = DateFormat.getDateTimeInstance().format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
    return FileProvider.getUriForFile(context, "com.example.polyglot.fileprovider", file)
}