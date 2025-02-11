package com.example.proyectocita.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectocita.models.Usuario

object FirestoreDB {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun saveUsuario(usuario: Usuario, onResult: (Boolean) -> Unit) {
        db.collection("usuarios").document(usuario.id_usuario).set(usuario)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getUsuarioById(userId: String, onResult: (Usuario?) -> Unit) {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    onResult(usuario)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }
}
