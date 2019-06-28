package com.unimapa.unimapa

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ListView

import com.unimapa.unimapa.domain.Post
import com.unimapa.unimapa.domain.PostAdapter

class PostListActivity: AppCompatActivity(){

    private var lv: ListView? = null
    private var customAdapter: PostAdapter? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.activity_post_list)

        var posts = ArrayList<Post>()

        posts = getPosts()

        this.lv = findViewById(R.id.list_view)
        this.customAdapter = PostAdapter(this, posts)

        lv!!.adapter = this.customAdapter

        toolbar = findViewById(R.id.toolbar)
        toolbar!!.setTitle("Suas Publicações")
    }

    private fun getPosts(): ArrayList<Post> {
        var posts = ArrayList<Post>()
        var postsFromServer = ServerConnection(this).getMyPosts("/my_posts")

        posts.add(Post("", "", 10f, 10f))

        for(post: Post in postsFromServer) {
            posts.add(post)
        }

        return posts
    }


}