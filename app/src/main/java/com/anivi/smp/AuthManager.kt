package com.anivi.smp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun register(
        ign: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        if (ign.trim().length < 3) {
            onResult(false, "IGN must be at least 3 characters")
            return
        }

        if (email.trim().isEmpty()) {
            onResult(false, "Enter your email")
            return
        }

        if (password.length < 6) {
            onResult(false, "Password must be at least 6 characters")
            return
        }

        auth.createUserWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (!task.isSuccessful) {
                onResult(
                    false,
                    task.exception?.message ?: "Registration failed"
                )
                return@addOnCompleteListener
            }

            val uid = auth.currentUser?.uid

            if (uid == null) {
                onResult(false, "Account creation failed")
                return@addOnCompleteListener
            }

            val userData = hashMapOf(
                "ign" to ign.trim(),
                "email" to email.trim(),
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener {
                    onResult(true, "Account created successfully")
                }
                .addOnFailureListener {
                    onResult(false, "Profile setup failed")
                }
        }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                onResult(true, "Login successful")
            } else {
                onResult(false, "Invalid email or password")
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun resetPassword(
        email: String,
        onResult: (Boolean, String) -> Unit
    ) {

        auth.sendPasswordResetEmail(
            email.trim()
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                onResult(true, "Password reset email sent")
            } else {
                onResult(false, "Could not send reset email")
            }
        }
    }

    fun getIGN(
        onResult: (String) -> Unit
    ) {

        val uid = auth.currentUser?.uid

        if (uid == null) {
            onResult("Player")
            return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                onResult(
                    document.getString("ign") ?: "Player"
                )
            }
            .addOnFailureListener {
                onResult("Player")
            }
    }
}
