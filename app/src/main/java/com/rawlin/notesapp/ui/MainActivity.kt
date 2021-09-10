package com.rawlin.notesapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rawlin.notesapp.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}