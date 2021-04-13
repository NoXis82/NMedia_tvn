package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.application.NMediaApplication
import ru.netology.nmedia.fragments.AddNewPost.Companion.textArg
import ru.netology.nmedia.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment_container).navigate(
                R.id.action_feedFragment_to_postReview,
                Bundle().apply {
                    textArg = text
                }
            )
        }
        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }
        checkGoogleApiAvailability()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                findNavController(R.id.nav_host_fragment_container)
                    .navigate(R.id.action_feedFragment_to_signInFragment)
                true
            }
            R.id.signup -> {
                findNavController(R.id.nav_host_fragment_container)
                    .navigate(R.id.action_feedFragment_to_signUpFragment)
                true
            }
            R.id.signout -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.sign_out)
                    .setMessage(R.string.message_sign_out_dialog)
                    .setPositiveButton(R.string.dialog_btn_yes) { _, _ ->
                        NMediaApplication.appAuth.removeAuth()
                        findNavController(R.id.nav_host_fragment_container).navigateUp()
                    }
                    .setNegativeButton(R.string.dialog_btn_no) { _, _ ->
                        return@setNegativeButton
                    }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@MainActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@MainActivity, code, 9000).show()
                return
            }
            Toast.makeText(this@MainActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }
}