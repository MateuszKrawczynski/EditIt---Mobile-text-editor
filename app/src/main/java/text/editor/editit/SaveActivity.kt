package text.editor.editit

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import text.editor.editit.databinding.ActivitySaveBinding
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer

class SaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveBinding
    private var bgColor = "white"
    private var fgColor = "black"
    private var currentFileExtension = ".txt" // Default to plain text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get content from the Intent, handling null values safely
        val content = intent.getStringExtra("CX") ?: ""

        // Set up color listeners
        setupColorListeners()

        // Trigger saving action on button click for saveIt (editit extension)
        binding.saveIt.setOnClickListener {
            val separator = "NIG**||{}||**NIG"
            // Create reasignedContent (with color info and content)
            val reasignedContent = "$bgColor;$fgColor$separator$content"
            currentFileExtension = ".editit.txt" // Track the current file type
            // Show save dialog with reasignedContent
            showSaveDialog(reasignedContent)
        }

        // Trigger saving action on button click for saveTxt (txt extension)
        binding.saveTxt.setOnClickListener {
            currentFileExtension = ".txt" // Track the current file type
            // Show save dialog with original content
            showSaveDialog(content)
        }
    }

    // Set up color listeners
    private fun setupColorListeners() {
        binding.blackBg.setOnClickListener { bgColor = "black" }
        binding.blackFont.setOnClickListener { fgColor = "black" }
        binding.whiteBg.setOnClickListener { bgColor = "white" }
        binding.whiteFont.setOnClickListener { fgColor = "white" }
        binding.redBg.setOnClickListener { bgColor = "red" }
        binding.redFont.setOnClickListener { fgColor = "red" }
        binding.greenBg.setOnClickListener { bgColor = "green" }
        binding.greenFont.setOnClickListener { fgColor = "green" }
        binding.blueBg.setOnClickListener { bgColor = "blue" }
        binding.blueFont.setOnClickListener { fgColor = "blue" }
        binding.pinkBg.setOnClickListener { bgColor = "pink" }
        binding.pinkFont.setOnClickListener { fgColor = "pink" }
    }

    // Function to show the dialog for saving the file
    private fun showSaveDialog(content: String) {
        val editText = android.widget.EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Save File")
            .setMessage("Enter file name:")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                var fileName = editText.text.toString().trim()
                if (fileName.isEmpty()) {
                    Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Append the current file extension
                fileName = "$fileName$currentFileExtension"

                // Trigger the SAF file creation with the proper content
                createFileWithSAF(fileName, content)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Function to trigger the SAF file creation dialog
    private fun createFileWithSAF(fileName: String, content: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Set the MIME type for the text files
            type = "text/plain"
            // Use the file name provided by the user
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        // Start the activity for result to create or select the file
        startActivityForResult(intent, REQUEST_CREATE_FILE)
    }

    // Handle the result of the SAF file creation
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CREATE_FILE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Save content based on the current file type
                val content = if (currentFileExtension == ".editit.txt") {
                    // Save reasignedContent for .editit.txt files
                    val separator = "NIG**||{}||**NIG"
                    "$bgColor;$fgColor$separator${intent.getStringExtra("CX") ?: ""}"
                } else {
                    // Save plain content for .txt files
                    intent.getStringExtra("CX") ?: ""
                }

                // Save the content to the selected file
                saveContentToUri(uri, content)
            }
        }
    }

    // Save the content to the given URI using the Storage Access Framework
    private fun saveContentToUri(uri: Uri, content: String) {
        try {
            // Open an OutputStream for the selected file URI
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val writer: Writer = OutputStreamWriter(outputStream)
                writer.write(content) // Write the content to the file
                writer.close()

                Toast.makeText(this, "File saved successfully.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error accessing file output stream.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CREATE_FILE = 1
    }
}
