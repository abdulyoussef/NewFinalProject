package com.example.newfinalproject.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.auth.User

class Post(
    var description: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimeMs: Long = 0,
    var user: com.example.newfinalproject.model.User? = null
    )
