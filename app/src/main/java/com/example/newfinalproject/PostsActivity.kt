package com.example.newfinalproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newfinalproject.databinding.ActivityPostsBinding
import com.example.newfinalproject.databinding.ItemPostBinding
import com.example.newfinalproject.model.Post
import com.example.newfinalproject.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


private const val TAG = "PostsActivity"
const val EXTRA_USERNAME ="EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {

    private lateinit var rvPosts: RecyclerView
    private lateinit var firestoreDb: FirebaseFirestore
    private var signedInUser: User? = null
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter
    private lateinit var binding: ActivityPostsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
         rvPosts = findViewById<RecyclerView>(R.id.rvPosts)

        // Creating the data source
        posts = mutableListOf()
        // Creating the adapter
        adapter = PostsAdapter(this, posts)
        // Bind the adapter and layout manager to the RV
        rvPosts.layoutManager = LinearLayoutManager(this)
        rvPosts.adapter = adapter


        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "singed in user: $signedInUser")
            }
            .addOnFailureListener {exception ->
                Log.i("Failure fetching singed in user", exception.toString())
            }

        val postsReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRA_USERNAME)

        if( username != null){
            supportActionBar?.title = username
            postsReference.whereEqualTo("user.username", username)
        }
        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }

//          val postList = snapshot.toObjects(Post::class.java)
//            posts.clear()
//            posts.addAll(postList)
//            adapter.notifyDataSetChanged()
//            for(post in postList) {
//                Log.i(TAG, "Post ${post}")
//            }
        }



       // binding.fabCreate.setOnClickListener{
         //   val intent = Intent(this,CreateActivity::class.java)
       //    startActivity(intent)
      //  }
    }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_posts, menu)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == R.id.menu_profile) {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                startActivity(intent)
            }
            return super.onOptionsItemSelected(item)
        }

    }
