package ch.rmy.android.http_shortcuts.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.utils.SimpleTextWatcher
import ch.rmy.curlcommand.CurlParser
import kotterknife.bindView

class CurlImportActivity : BaseActivity() {

    private var commandEmpty = true

    val curlCommand: EditText by bindView(R.id.curl_import_command)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curl_import)

        curlCommand.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                checkIfCommandEmpty()
            }
        })
    }

    private fun checkIfCommandEmpty() {
        val commandEmpty = curlCommand.text.isEmpty()
        if (this.commandEmpty != commandEmpty) {
            this.commandEmpty = commandEmpty
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.curl_import_activity_menu, menu)
        menu.findItem(R.id.action_create_from_curl).isVisible = curlCommand.text.isNotEmpty()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create_from_curl -> {
                startImport()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startImport() {
        val commandString = curlCommand.text.toString()
        val command = CurlParser.parse(commandString)

        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(EditorActivity.EXTRA_CURL_COMMAND, command)
        startActivityForResult(intent, REQUEST_CREATE_SHORTCUT)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CREATE_SHORTCUT) {
            val shortcutId = intent.getLongExtra(EditorActivity.EXTRA_SHORTCUT_ID, 0)
            val returnIntent = Intent()
            returnIntent.putExtra(EditorActivity.EXTRA_SHORTCUT_ID, shortcutId)
            setResult(Activity.RESULT_OK, returnIntent)
        }
        finish()
    }

    override val navigateUpIcon = R.drawable.ic_clear

    companion object {

        private const val REQUEST_CREATE_SHORTCUT = 1

    }

}
