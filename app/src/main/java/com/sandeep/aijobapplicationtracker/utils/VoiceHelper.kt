package com.sandeep.aijobapplicationtracker.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import timber.log.Timber
import java.util.Locale
import android.os.Handler
import android.os.Looper

class VoiceHelper(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsReady = false
    private var pendingText: String? = null
    
    // We keep track of whether we are in a "listening" loop
    private var isContinuousListening = false
    
    var onSpeechPartial: ((String) -> Unit)? = null
    var onSpeechFinal: ((String) -> Unit)? = null
    var onSpeechError: ((String) -> Unit)? = null
    var onTtsFinished: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    Handler(Looper.getMainLooper()).post {
                        onTtsFinished?.invoke()
                    }
                }
                override fun onError(utteranceId: String?) {
                    Handler(Looper.getMainLooper()).post {
                        onTtsFinished?.invoke()
                    }
                }
            })
            isTtsReady = true
            pendingText?.let {
                speak(it)
                pendingText = null
            }
        } else {
            Timber.e("TTS Initialization failed")
        }
    }

    fun speak(text: String) {
        isContinuousListening = false
        speechRecognizer?.stopListening()
        
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_id_1")
        } else {
            pendingText = text
        }
    }

    private fun startRecognizerIntent() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Extra speech duration hints (often ignored by some OEMs, so we loop manually)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 15000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 15000)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Timber.e(e, "Error starting speech recognition")
            onSpeechError?.invoke("Could not start microphone")
        }
    }

    fun startListening() {
        isContinuousListening = true
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                
                override fun onError(error: Int) {
                    onSpeechError?.invoke("Speech recognition error: $error")
                    // If we get an error like timeout, but we want continuous listening, restart!
                    // Except ERROR_CLIENT (usually means manually stopped)
                    if (isContinuousListening && error != SpeechRecognizer.ERROR_CLIENT) {
                        startRecognizerIntent()
                    }
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val resultText = matches?.firstOrNull() ?: ""
                    onSpeechFinal?.invoke(resultText)
                    
                    if (isContinuousListening) {
                        startRecognizerIntent() // restart listening!
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val resultText = matches?.firstOrNull() ?: ""
                    onSpeechPartial?.invoke(resultText)
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
        startRecognizerIntent()
    }

    fun stopListening() {
        isContinuousListening = false
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        isContinuousListening = false
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
