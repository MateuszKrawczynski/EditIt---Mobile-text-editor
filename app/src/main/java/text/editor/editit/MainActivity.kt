package text.editor.editit

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import text.editor.editit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>
    private var fileContent: String = ""
    private var fileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the UI binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register the file picker launcher
        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                handleFileUri(uri)
            }
        }

        // Set up the "Save" button
        binding.save.setOnClickListener {
            val content = binding.mainedit.text.toString()
            val intent = Intent(applicationContext, SaveActivity::class.java)
            intent.putExtra("CX", content)
            startActivity(intent)
        }

        // Set up the "Load" button
        binding.load.setOnClickListener {
            openFilePicker()
        }
    }

    // Open the file picker
    private fun openFilePicker() {
        val mimeTypes = arrayOf("*/*") // Allow all file types
        filePickerLauncher.launch(mimeTypes)
    }

    // Handle the selected file
    private fun handleFileUri(uri: Uri) {
        // Get the file name from the URI (if possible)
        val name = getFileName(uri)
        fileName = name ?: ""

        // Read the content of the selected file
        contentResolver.openInputStream(uri)?.use { inputStream ->
            fileContent = inputStream.bufferedReader().use { it.readText() }
        }

        // Determine the file type based on the name or content
        if (fileName.endsWith(".editit.txt")) {
            loadEditItFile(fileContent)
        } else if (fileName.endsWith(".txt")) {
            loadPlainTextFile(fileContent)
        } else {
            binding.mainedit.setText("Unsupported file format")
        }
    }

    // Helper function to get the file name from the URI
    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    fileName = it.getString(it.getColumnIndexOrThrow("_display_name"))
                }
            }
        }
        if (fileName == null) {
            fileName = uri.path?.substringAfterLast('/')
        }
        return fileName
    }

    // Load ".editit.txt" file
    private fun loadEditItFile(content: String) {
        try {
            val arrayContent = content.split("NIG**||{}||**NIG")
            val bgColorContent = arrayContent[0].split(";")[0]
            val fgColorContent = arrayContent[0].split(";")[1]
            val contentPlain = arrayContent[1]

            val bgColor = when (bgColorContent) {
                "black" -> "#000000"
                "white" -> "#ffffff"
                "red" -> "#ca0707"
                "green" -> "#099100"
                "blue" -> "#003ac2"
                "pink" -> "#ff5bf2"
                else -> "#ffffff" // Default background color
            }

            val fgColor = when (fgColorContent) {
                "black" -> "#000000"
                "white" -> "#ffffff"
                "red" -> "#ca0707"
                "green" -> "#099100"
                "blue" -> "#003ac2"
                "pink" -> "#ff5bf2"
                else -> "#000000" // Default text color
            }

            binding.mainedit.setBackgroundColor(Color.parseColor(bgColor))
            binding.mainedit.setTextColor(Color.parseColor(fgColor))
            binding.mainedit.setHintTextColor(Color.parseColor(fgColor))
            binding.mainedit.setText(contentPlain)

        } catch (e: Exception) {
            binding.mainedit.setText("Error loading .editit.txt file")
        }
    }

    // Load plain ".txt" file
    private fun loadPlainTextFile(content: String) {
        binding.mainedit.setBackgroundColor(Color.parseColor("#ffffff"))
        binding.mainedit.setTextColor(Color.parseColor("#000000"))
        binding.mainedit.setHintTextColor(Color.parseColor("#000000"))
        binding.mainedit.setText(content)
    }
}
