package com.cuinsolutions.macrosmanager

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val firstNameSignUpEditText = findViewById<EditText>(R.id.firstNameSignUpEditText)
        val lastNameSignUpEditText = findViewById<EditText>(R.id.lastNameSignUpEditText)
        val emailSignUpEditText = findViewById<EditText>(R.id.emailSignUpEditText)
        val passwordSignUpEditText = findViewById<EditText>(R.id.passwordSignUpEditText)
        val confirmPasswordSignUpEditText = findViewById<EditText>(R.id.confirmPasswordSignUpEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        var firstName: String
        var lastName: String
        var email: String
        val intent = intent
        val gson = Gson()

        val dailyMeals: Array<HashMap<String, Any>> = gson.fromJson(intent.getStringExtra("dailyMeals"), object : TypeToken<Array<HashMap<String, Any>>>() {}.type)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        signUpButton.setOnClickListener {

            if (firstNameSignUpEditText.text.toString().equals("")) {

                Toast.makeText(this, "Please enter a valid first name.", Toast.LENGTH_SHORT).show()

                /*val firstNameAlertDialog = AlertDialog.Builder(this).setTitle("Entry Error").
                        setMessage("Please enter a valid first name.").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss()
                })

                firstNameAlertDialog.show()*/
                return@setOnClickListener
            } else {
                firstName = firstNameSignUpEditText.text.toString()

            }

            if (lastNameSignUpEditText.text.toString().equals("")) {
                /*val lastNameAlertDialog = AlertDialog.Builder(this).setTitle("Entry Error").
                        setMessage("Please enter a valid last name.").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss()
                        })

                lastNameAlertDialog.show()*/

                Toast.makeText(this, "Please enter a valid last name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                lastName = lastNameSignUpEditText.text.toString()
            }

            if (emailSignUpEditText.text.toString() == "" && !Regexs().validEmail(emailSignUpEditText.text.toString())) {

                /*val emailAlertDialog = AlertDialog.Builder(this).setTitle("Entry Error").
                        setMessage("Please enter a valid email.").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss()
                        })

                emailAlertDialog.show()*/

                Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                email = emailSignUpEditText.text.toString()
            }

            if (passwordSignUpEditText.text.toString() == "") {

                /*val passwordAlertDialog = AlertDialog.Builder(this).setTitle("Entry Error").
                        setMessage("Please enter a password.").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss()
                        })

                passwordAlertDialog.show()*/

                Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPasswordSignUpEditText.text.toString() == "") {

                /*val passwordAlertDialog = AlertDialog.Builder(this).setTitle("Entry Error").
                        setMessage("Please enter confirm your password.").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss()
                        })

                passwordAlertDialog.show()*/

                Toast.makeText(this, "Please enter confirm your password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordSignUpEditText.text.toString() != confirmPasswordSignUpEditText.text.toString()) {

                /*val passwordAlertDialog = AlertDialog.Builder(this).setTitle("Entry Error").
                        setMessage("Passwords do not match. Please try again.").setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss()
                        })

                passwordAlertDialog.show()*/

                Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, passwordSignUpEditText.text.toString()).addOnCompleteListener { task: Task<AuthResult> ->  

                if (task.isSuccessful) {

                    //val user = auth.currentUser

                    val type = object : TypeToken<Pair<String, Any>>() {}.type
                    val gson = Gson()
                    val currentUser = auth.currentUser!!.uid
                    val users = firestore.collection("users").document(currentUser)
                    val userData = hashMapOf("firstName" to firstName, "lastName" to lastName, "email" to email, "showAds" to true, "gender" to intent.getStringExtra("gender"),
                            "heightMeasurement" to intent.getStringExtra("heightMeasurement"), "weightMeasurement" to intent.getStringExtra("weightMeasurement"),
                            "feet" to intent.getIntExtra("feet", 0), "inches" to intent.getDoubleExtra("inches", 0.0), "cm" to intent.getDoubleExtra("cm", 0.0),
                            "birthDate" to intent.getStringExtra("birthDate"), "dailyActivity" to intent.getStringExtra("dailyActivity"),
                            "physicalActivityLifestyle" to intent.getStringExtra("physicalActivityLifestyle"), "goal" to intent.getStringExtra("goal"),
                            "pounds" to intent.getDoubleExtra("pounds", 0.0), "kg" to intent.getDoubleExtra("kg", 0.0),
                            "stone" to intent.getDoubleExtra("stone", 0.0), "dietFatPercent" to intent.getDoubleExtra("dietFatPercent", 0.0),
                            "calories" to intent.getIntExtra("calories", 0), "carbs" to intent.getIntExtra("carbs", 0),
                            "fat" to intent.getIntExtra("fat", 0), "protein" to intent.getIntExtra("protein", 0), "showAds" to true,
                            "dailyMeals" to (gson.fromJson(intent.getStringExtra("dailyMeals"), object : TypeToken<List<HashMap<String, Any>>>() {}.type)),
                            "currentCalories" to intent.getDoubleExtra("currentCalories", 0.0), "currentCarbs" to intent.getDoubleExtra("currentCarbs", 0.0),
                            "currentFat" to intent.getDoubleExtra("currentFat", 0.0), "currentProtein" to intent.getDoubleExtra("currentProtein", 0.0))
                    users.set(userData as Map<String, Any>).addOnSuccessListener {

                        Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {

                        Toast.makeText(this, "Unable to save data. Please try again later.", Toast.LENGTH_SHORT).show()
                    }


                } else {

                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            finish()
        }
    }
}
