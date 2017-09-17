package com.example.kb.tictactoe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var buttonList: Array<Button>? = null
    private var gameBoard: Array<Array<Int>> = arrayOf(arrayOf(0, 0, 0),
            arrayOf(0, 0, 0),
            arrayOf(0, 0, 0))
    private var isCrossMove: Boolean = true
    private val CROSS = 1
    private val CIRCLE = -1
    private val NO_WINNER = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        buttonList = arrayOf(b1, b2, b3, b4, b5, b6, b7, b8, b9)
        setUpButtonsListener()
    }

    private fun setUpButtonsListener() {
        buttonList?.forEachIndexed { index, button -> button.setOnClickListener { buttonClick(index) } }
    }

    private fun buttonClick(buttonID: Int) {
        val row = buttonID / 3
        val cell = buttonID % 3
        if (isCrossMove) {
            gameBoard[row][cell] = CROSS
            buttonList?.get(buttonID)?.text = "X"
        } else {
            gameBoard[row][cell] = CIRCLE
            buttonList?.get(buttonID)?.text = "O"
        }
        buttonList?.get(buttonID)?.isEnabled = false
        isCrossMove = !isCrossMove
        checkForWinnerOrGameEnded()
    }

    private fun checkForWinnerOrGameEnded() {
        var winner = NO_WINNER
        gameBoard.forEach {
            if (hasWinner(it))
                winner = it[0]
        }
        if (winner == NO_WINNER) {
            winner = checkWinnerInColumns()
        }
        if (winner == NO_WINNER) {
            winner = checkDiagonals()
        }
        if (winner != NO_WINNER) {
            showWinner(winner)
            return
        } else if (gameBoard.all { it.all { it != 0 } }) {
            showDrawMessage()
            resetGameStatus()
        }
    }

    private fun showWinner(winner: Int) {
        var resource = if (winner == CIRCLE) R.string.circle_wins else R.string.cross_wins
        Toast.makeText(this, resource, Toast.LENGTH_SHORT).show()
        resetGameStatus()
    }

    private fun showDrawMessage() {
        Toast.makeText(this, R.string.draw_message, Toast.LENGTH_SHORT).show()
        resetGameStatus()
    }

    private fun checkDiagonals(): Int {
        var diagonal: Array<Int>?
        var diagonal2: Array<Int>?
        diagonal = arrayOf(gameBoard[0][0], gameBoard[1][1], gameBoard[2][2])
        diagonal2 = arrayOf(gameBoard[0][2], gameBoard[1][1], gameBoard[2][0])
        return when {
            hasWinner(diagonal) -> diagonal[0]
            hasWinner(diagonal2) -> diagonal2[0]
            else -> NO_WINNER
        }
    }

    private fun hasWinner(values: Array<Int>): Boolean {
        return values.all { it == CROSS } || values.all { it == CIRCLE }
    }

    private fun checkWinnerInColumns(): Int {
        var column: Array<Int>?
        for (i in 0..2) {
            column = arrayOf(gameBoard[0][i], gameBoard[1][i], gameBoard[2][i])
            if (hasWinner(column))
                return gameBoard[0][i]
        }
        return NO_WINNER
    }

    private fun resetGameStatus() {
        gameBoard.map { it.fill(0) }
        buttonList?.map {
            it.isEnabled = true
            it.text = ""
        }
    }
}
