package com.example.githubapp.ui.activities

import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.githubapp.R
import com.example.githubapp.database.DatabaseBuilder
import com.example.githubapp.database.UserDatabase
import com.example.githubapp.databinding.ActivityMainBinding
import com.example.githubapp.model.User
import com.example.githubapp.network.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var database: UserDatabase
    var data:List<User> = emptyList()
    private lateinit var adapter:SimpleCursorAdapter

    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        database = DatabaseBuilder.getDatabase(this)

        val intent = intent
        if (intent!=null){
            val icon = intent.getStringExtra("icon")
            val name = intent.getStringExtra("name")
            val email = intent.getStringExtra("email")
            val id = intent.getStringExtra("id")
            val user = User(
                avatar_url = icon.toString(),
                name = name.toString(),
                email = email.toString(),
                id = id?.toIntOrNull()
            )
            updateProfile(user)
        }


        getAll().observe(this){
            data = it
            val cursor = MatrixCursor(arrayOf("_id","_name"))
            data.forEach {user->
                cursor.addRow(arrayOf(user.id,user.login))
            }
            adapter.changeCursor(cursor)
        }
        setAdaptors()
        setListeners()


    }

    private fun getAll() = database.getUserDao().getAll()




    private fun addUser(user: User) = lifecycleScope.launchWhenCreated {
            database.getUserDao().insert(user)
        }


    private fun setAdaptors() {

        val cursor = MatrixCursor(arrayOf("_id","_name"))
        data.forEach {
            cursor.addRow(arrayOf(it.id,it.login))
        }

        adapter = SimpleCursorAdapter(this, R.layout.suggestion,
            cursor,
            arrayOf("_name"),
            intArrayOf(R.id.tv_searchText),
            SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE
        )
        binding.searchView.suggestionsAdapter = adapter
        binding.searchView.setOnSuggestionListener(object :SearchView.OnSuggestionListener{
            override fun onSuggestionSelect(position: Int): Boolean {
                binding.searchView.setQuery(data[position].name,false)
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                binding.searchView.setQuery(null,false)
                addUser(data[position])
                updateProfile(data[position])
                binding.searchView.clearFocus()
                return true
            }
        })
    }
    private fun setListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    lifecycleScope.launchWhenCreated {
                        binding.progressBar.visibility = View.VISIBLE
                        val response = try {
                            RetrofitInstance.api.getUser(query)
                        } catch (e: IOException) {
                            Log.e(TAG, "onQueryTextSubmit: ${e.message}")
                            return@launchWhenCreated
                        } catch (e: HttpException) {
                            Log.e(TAG, "onQueryTextSubmit: ${e.message}")
                            return@launchWhenCreated
                        }
                        if (response.isSuccessful && response.body() != null) {
                            Log.e(TAG, "onQueryTextSubmit: ${response.body()}")
                            addUser(response.body()!!)
                            updateProfile(response.body()!!)

                        } else{
                            Log.e(TAG, "Not Successful")
                            Toast.makeText(this@MainActivity,"User Not Found!",Toast.LENGTH_LONG).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@MainActivity, "No Username Entered", Toast.LENGTH_SHORT)
                        .show()
                }
                binding.searchView.setQuery("",false)
                binding.searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun updateProfile(user: User) {

        Glide.with(this).
                load(user.avatar_url).into(binding.ivUserIcon)
        binding.tvUserName.text = user.name
        binding.tvBio.text = user.bio
        binding.tvFollowers.text = user.followers.toString()
        binding.tvEmail.text = user.email?:"NA"
        binding.tvFollowing.text = user.following.toString()
        binding.tvRepo.text = "${user.public_repos} Repos"
        binding.tvLoc.text = user.location?:"NA"
        binding.tvUrl.text = user.html_url

        binding.userView.visibility = View.VISIBLE

    }
}