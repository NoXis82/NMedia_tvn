package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.fragments.AddNewPost.Companion.textArg
import ru.netology.nmedia.repository.IPostRepository
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var repository: IPostRepository

    @Inject
    lateinit var auth: AppAuth

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
                        auth.removeAuth()
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

}