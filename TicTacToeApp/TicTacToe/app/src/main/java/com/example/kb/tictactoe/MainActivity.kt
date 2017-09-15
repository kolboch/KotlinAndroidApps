package com.example.kb.tictactoe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val buttonList: Array<Button> = arrayOf(b1, b2, b3, b4, b5, b6, b7, b8, b9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun buttonClick() {

    }
}
