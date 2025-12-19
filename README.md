# 🚀 Kotlin Coroutine – Complete Guide (Bangla)

এই ডকুমেন্টটি Kotlin Coroutine সম্পর্কে **beginner → intermediate** লেভেলের একটি complete, clean এবং practical গাইড। Android (MVVM) প্রজেক্টে সরাসরি ব্যবহার করার মতো করে সাজানো হয়েছে।

---

## 🧵 Coroutine কী?

Coroutine হলো **lightweight thread**।
অর্থাৎ, তুমি একাধিক কাজ ব্যাকগ্রাউন্ডে খুব কম রিসোর্স ব্যবহার করে চালাতে পারো।

➡️ Main thread ব্লক না করে ব্যাকগ্রাউন্ডে কাজ করতে coroutine ব্যবহার করা হয়।

### Android-এ Coroutine কোথায় ব্যবহার হয়?

* API call
* Database operation (Room)
* File read / write
* Heavy processing (calculation, parsing)

---

## ⭐ কেন Coroutine ব্যবহার করবো?

| Problem                               | Coroutine Solution               |
| ------------------------------------- | -------------------------------- |
| Thread ব্যবহার করলে বেশি রিসোর্স লাগে | Coroutine lightweight            |
| Callback hell তৈরি হয়                | Coroutine clean & readable       |
| UI thread block হলে app lag করে       | Coroutine async ভাবে কাজ করে     |
| Exception handle করা কঠিন             | Coroutine structured concurrency |

---

## ⚡ Coroutine কীভাবে কাজ করে?

Coroutine মূলত ৩টি জিনিসের উপর ভিত্তি করে কাজ করে:

1. **CoroutineScope**
2. **Dispatcher**
3. **Coroutine Builder (launch / async)**

---

## 1️⃣ CoroutineScope

Scope মানে হচ্ছে — coroutine কোথায় চলবে তার একটা সীমানা।

### Android-এ ব্যবহৃত Scope

* `GlobalScope` ❌ (Avoid)
* `viewModelScope` ✅ (ViewModel)
* `lifecycleScope` ✅ (Activity / Fragment)

```kotlin
lifecycleScope.launch {
    // Coroutine code
}
```

---

## 2️⃣ Dispatcher

Dispatcher বলে দেয় coroutine কোন thread-এ চলবে।

| Dispatcher             | কাজ                         |
| ---------------------- | --------------------------- |
| Dispatchers.Main       | UI thread                   |
| Dispatchers.IO         | Network, Database, File ops |
| Dispatchers.Default    | Heavy CPU work              |
| Dispatchers.Unconfined | Advanced case               |

```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    val data = api.getData()
}
```

---

## 🔄 Dispatcher Switching (withContext)

`withContext` ব্যবহার করা হয় একই coroutine এর ভিতরে thread change করার জন্য।

* নতুন coroutine বানায় না
* Coroutine suspend করে
* কাজ শেষ হলে আগের thread-এ ফিরে আসে

```kotlin
lifecycleScope.launch(Dispatchers.Main) {
    val data = withContext(Dispatchers.IO) {
        fetchDataFromNetwork()
    }
    textView.text = data
}
```

---

## 3️⃣ Coroutine Builder

### 🔹 launch

* কিছু return করে না
* Normal কাজের জন্য

```kotlin
viewModelScope.launch {
    fetchUser()
}
```

### 🔹 async

* Value return করে
* `await()` দিয়ে result পাওয়া যায়

```kotlin
viewModelScope.launch {
    val result = async { api.getData() }
    val data = result.await()
}
```

---

## ⏱️ delay (Non‑blocking sleep)

```kotlin
lifecycleScope.launch {
    println("Start")
    delay(2000)
    println("Finish")
}
```

➡️ Thread block করে না

---

## 🧩 suspend function

`suspend` function শুধুমাত্র coroutine থেকেই call করা যায়।

```kotlin
suspend fun getUser(): String {
    delay(1000)
    return "Utpal"
}

lifecycleScope.launch {
    val name = getUser()
    println(name)
}
```

---

## 🧪 Complete Basic Example

```kotlin
lifecycleScope.launch {
    println("Main Thread Start")

    val data = withContext(Dispatchers.IO) {
        delay(2000)
        "Hello from IO Thread"
    }

    println(data)
}
```

---

## 🧱 Structured Concurrency

* Coroutine গুলো parent–child relation এ কাজ করে
* Parent cancel হলে সব child cancel হয়
* Lifecycle safe code লেখার জন্য খুব গুরুত্বপূর্ণ

```kotlin
lifecycleScope.launch {
    launch {
        delay(1000)
        println("Job 1 finished")
    }
    launch {
        delay(2000)
        println("Job 2 finished")
    }
}
```

---

## 🧵 Job & Cancellation

### Job cancel

```kotlin
val job = lifecycleScope.launch {
    repeat(5) {
        println("Task $it")
        delay(500)
    }
}

lifecycleScope.launch {
    delay(1200)
    job.cancel()
}
```

### isActive check

```kotlin
if (!isActive) return
```

---

## 🛡️ SupervisorJob

Child coroutine fail হলেও অন্যগুলো চলতে থাকে।

```kotlin
val supervisor = SupervisorJob()
val scope = CoroutineScope(Dispatchers.Main + supervisor)
```

---

## ⏳ withTimeout

```kotlin
withTimeout(1500) {
    delay(2000)
}
```

---

## 🌊 Flow

Flow হলো coroutine ভিত্তিক **asynchronous data stream**।

* Multiple value emit করতে পারে
* Cold stream
* LiveData-এর modern alternative

```kotlin
fun simpleFlow(): Flow<Int> = flow {
    emit(1)
    delay(1000)
    emit(2)
}
```

```kotlin
lifecycleScope.launch {
    simpleFlow().collect { println(it) }
}
```

---

## 🔥 Flow Operators

* `map` – transform data
* `filter` – condition
* `debounce` – delay input
* `distinctUntilChanged` – avoid duplicate
* `flatMapLatest` – cancel old

---

## 🔁 StateFlow

* Hot Flow
* Always latest value রাখে
* UI state এর জন্য best

```kotlin
private val _state = MutableStateFlow(0)
val state: StateFlow<Int> = _state
```

---

## 📣 SharedFlow

* Hot Flow
* Event-এর জন্য best (Toast, Navigation)

```kotlin
private val _event = MutableSharedFlow<String>()
```

---

## ✅ Final Notes

✔️ Coroutine = modern async solution
✔️ Flow = stream based data
✔️ StateFlow = UI state
✔️ SharedFlow = UI event
✔️ MVVM + Coroutine = clean architecture

---

⭐ এই README ফাইলটি Android Coroutine শেখার জন্য একটি complete reference হিসেবে ব্যবহার করা যাবে।
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}
