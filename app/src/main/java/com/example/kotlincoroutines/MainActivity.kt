package com.example.kotlincoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    //lateinit var txt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //txt = findViewById(R.id.tXt)
        //Log.d("utpal", "${Thread.currentThread().name}")


        /* CoroutineScope(Dispatchers.Main).launch {
            Task1()
           // Task2()
        }
        CoroutineScope(Dispatchers.Main).launch {
            Task2()
        }*/

        CoroutineScope(Dispatchers.IO).launch {
            printFollowers()
        }
    }

    /*private suspend fun printFollowers() {  //launch use...............
        var fbfollower = 0
        val job = CoroutineScope(Dispatchers.IO).launch {
            fbfollower = getFbfollowers()
        }
        job.join()
        Log.d("utpal", fbfollower.toString())
    }

    private suspend fun getFbfollowers(): Int {
        delay(1000)
        return 54
    }
}*/
    private suspend fun printFollowers() {

      val deffertObj=CoroutineScope(Dispatchers.IO).async {
          getfollowers()
      }
        Log.d("utpal",deffertObj.await().toString())
    }

    private suspend fun getfollowers():Int{

        delay(1000)
        return 54
    }
}


    /*fun counter(view: View) {
        txt.text = "${txt.text.toString().toInt() + 1}"
        Log.d("utpal1", "${Thread.currentThread().name}")
    }

    fun Haripada() {
        for (i in 1..1000000000L) {

        }
    }

    fun haripada(view: View) {
        /*   thread(start = true) { //background thread....
            Haripada()
        }*/
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("utpal1", "${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.Main) {
            Log.d("utpal2", "${Thread.currentThread().name}")
        }
        MainScope().launch(Dispatchers.Default) {
            Log.d("utpal3", "${Thread.currentThread().name}")
        }
    }*/

   /* suspend fun Task1()
    {
        Log.d("utpal1","I am a boy")
        yield()
        Log.d("utpal1","I am a boy")
    }
    suspend fun Task2()
    {
        Log.d("utpal12","I am a app developer")
        yield()
        Log.d("utpal12","I am a app developer")
    }*/
