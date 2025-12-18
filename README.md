Coroutine কী?

Coroutine হলো lightweight thread।
অর্থাৎ, তুমি একাধিক কাজ ব্যাকগ্রাউন্ডে খুব কম রিসোর্স ব্যবহার করে চালাতে পারো।
➡️ Main thread ব্লক না করে ব্যাকগ্রাউন্ডে কাজ করতে coroutine ব্যবহার করা হয়।
Android-এ:
1.API call
2.Database operation
3.File read/write
4.Heavy processing
5.এসব করতে coroutine খুবই দরকারি।

⭐ কেন coroutine ব্যবহার করবো?
Problem	                                               Coroutine Solution
Thread ব্যবহার করলে বেশি রিসোর্স লাগে	                 Coroutine lightweight
Callback hell তৈরি হয়	                            Coroutine clean & readable
UI thread block হলে app lag করে	               Coroutine async ভাবে কাজ করে
Exception handle করা কঠিন	                    Coroutine structured concurrency

⚡ কিভাবে Coroutine কাজ করে?
Coroutine তিনটি জিনিস নিয়ে কাজ করে:

1️⃣ CoroutineScope
Scope মানে হচ্ছে — coroutine কোথায় চলবে তার একটা সীমানা।
Android-এ scope:
GlobalScope (avoid)
viewModelScope (viewmodel)
lifecycleScope (activity/fragment)

Example:
lifecycleScope.launch {
    // Coroutine code
}

2️⃣ Dispatcher
Dispatcher বলে দেবে coroutine কোন thread-এ চলবে।

Dispatcher Types:
    Dispatcher	                   কাজ
Dispatchers.Main	            UI thread-এ চলে
Dispatchers.IO	              Network, Database, File ops
Dispatchers.Default	          Heavy CPU কাজ
Dispatchers.Unconfined	      advanced case

Example:
lifecycleScope.launch(Dispatchers.IO) {
    val data = api.getData()
}

5️⃣ Switching Dispatcher
withContext দিয়ে কাজের thread change করা যায়

lifecycleScope.launch(Dispatchers.Main) {
    val data = withContext(Dispatchers.IO) {
        fetchDataFromNetwork()
    }
    textView.text = data
}

withContext suspends coroutine, ব্যাকগ্রাউন্ডে কাজ করে, তারপর Main thread এ ফিরিয়ে আন

3️⃣ Builder (launch / async)
Coroutine start করার জন্য builder লাগে।

launch
কিছু return করে না
সাধারণ কাজের জন্য

viewModelScope.launch {
    fetchUser()
}

async
value return করে
await() দিয়ে value পাওয়া যায়

viewModelScope.launch {
    val result = async { api.getData() }
    val data = result.await()
}

⭐ 5. Delay (non-blocking sleep)
delay() coroutine use করে
➡️ Thread block না করে wait করে।

Example:
lifecycleScope.launch {
    println("Start")
    delay(2000)
    println("Finish")
}

⭐ 6. suspend function (Basic Idea)
suspend মানে হলো coroutine বাদে call করা যাবে না।

Example:
suspend fun getUser(): String {
    delay(1000)
    return "Utpal"
}
Call:
lifecycleScope.launch {
    val name = getUser()
    println(name)
}
⭐ Complete Basic Example (Real Android Style)
lifecycleScope.launch {
    println("Main Thread Start")
    val data = withContext(Dispatchers.IO) {
        // background work
        delay(2000)
        "Hello from IO Thread"
    }
    // Back to Main Thread
    println(data)
}

1️⃣ Structured Concurrency কী?
Coroutine-গুলোকে parent-child relationship এ organize করা
Parent coroutine শেষ হলে সব child coroutine automatically cancel হয়
Android lifecycle safe code এর জন্য essential

Example:
lifecycleScope.launch { // parent coroutine
    val job1 = launch {
        delay(1000)
        println("Job 1 finished")
    }
    val job2 = launch {
        delay(2000)
        println("Job 2 finished")
    }
    println("Parent coroutine finished")
}
Output:
Parent coroutine finished
Job 1 finished
Job 2 finished
এখানে parent coroutine শেষ হলেও child coroutine চলতে থাকে।
যদি parent cancel হয়, সব child automatically cancel হবে।

2️⃣ Job
Coroutine সব সময় Job return করে
Job দিয়ে coroutine cancel বা join করা যায়

Example:
val job = lifecycleScope.launch {
    repeat(5) { i ->
        println("Task $i")
        delay(500)
    }
}
// Cancel after 1.2 sec
lifecycleScope.launch {
    delay(1200)
    job.cancel()
    println("Job cancelled")
}
Output:
Task 0
Task 1
Task 2
Job cancelled
Job canceled হয়ে গেলে remaining task stop হয়ে যায়।

3️⃣ Cancellation
Coroutine cancel করার জন্য job.cancel() ব্যবহার হয়
Suspend function automatically check করে cancellation
Blocking code cancelable করতে isActive check করা হয়
Example with isActive:
val job = lifecycleScope.launch {
    for (i in 1..10) {
        if (!isActive) break // check cancellation
        println("Task $i")
        delay(500)
    }
}

4️⃣ SupervisorJob (Advanced)
Normal Job: child cancel হলে parent cancel করে

SupervisorJob: child cancel হলেও parent & other child চলতে থাকে

Example:
val supervisor = SupervisorJob()

val scope = CoroutineScope(Dispatchers.Main + supervisor)

scope.launch {
    val child1 = launch {
        throw Exception("Child1 failed")
    }
    val child2 = launch {
        delay(1000)
        println("Child2 still running")
    }
}


Output:

Child2 still running


Child1 failed হলেও Child2 চলতে থাকে।

5️⃣ Timeout / withTimeout

Coroutine automatic cancel করতে timeout ব্যবহার করা যায়

lifecycleScope.launch {
    try {
        withTimeout(1500) {
            repeat(5) { i ->
                println("Task $i")
                delay(1000)
            }
        }
    } catch (e: TimeoutCancellationException) {
        println("Task timed out")
    }
}


Output:

Task 0
Task 1
Task timed out
