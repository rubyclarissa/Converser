package uk.ac.aber.dcs.rco1.converser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //activate toolbar and use as actionbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        //find the home fragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host_fragment)
        as NavHostFragment

        val navController = navHostFragment.navController

    }
}