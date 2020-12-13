package hu.droth.kristof.mindlearning.view.game.learning.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.entity.Meaning
import hu.droth.kristof.mindlearning.view.game.learning.viewModels.GameMusicalViewModel
import kotlinx.android.synthetic.main.game_musical_fragment.*
import java.util.*

@AndroidEntryPoint
class GameMusicalFragment :
    GameBaseLearningFragment<GameMusicalViewModel>(R.layout.game_musical_fragment) {

    private lateinit var currentMeaning: Meaning
    private lateinit var tts: TextToSpeech

    override fun setObservables() {
        super.setObservables()
        viewModel.currentWordData.observe(viewLifecycleOwner, {
            it?.let { meaning ->
                currentMeaning = meaning
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextToSpeech()
    }

    private fun setTextToSpeech() {
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = tts.setLanguage(Locale.UK)
                if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    throw Exception("Language not supported")
                }
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(p0: String?) {

                    }

                    override fun onDone(p0: String?) {

                    }

                    override fun onError(p0: String?) {}
                })
                btnPlayWordMusicalFragment.isEnabled = true
            } else {
                throw Exception("Failed to initialize TextToSpeechObject")
            }
        }
    }

    override fun setButtonClicks() {
        super.setButtonClicks()
        btnPlaySyllableMusicalFragment.setOnClickListener { playSyllableMusic() }
        btnPlayWordMusicalFragment.setOnClickListener { playWordMusic() }
    }

    private fun playWordMusic() {
        val text = currentMeaning.writing
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun playSyllableMusic() {
        val multiple = currentMeaning.syllable
        if (multiple != 0) {
            setPlayingButtonEnabled(false)
            val syllableSound = MediaPlayer.create(requireContext(), R.raw.syllable_sound)
            var currentMultiple = 1
            syllableSound.start()
            syllableSound.setOnCompletionListener {
                if (currentMultiple < multiple) {
                    currentMultiple++
                    it.seekTo(0)
                    it.start()
                } else if (currentMultiple == multiple) {
                    setPlayingButtonEnabled(true)
                    it.release()
                }
            }
        }
    }

    private fun setPlayingButtonEnabled(enabled: Boolean) {
        btnPlaySyllableMusicalFragment.isEnabled = enabled
        btnPlayWordMusicalFragment.isEnabled = enabled
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.shutdown()
        }
    }


}