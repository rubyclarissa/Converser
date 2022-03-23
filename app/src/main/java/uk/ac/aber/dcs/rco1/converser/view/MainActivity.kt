package uk.ac.aber.dcs.rco1.converser.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.databinding.ActivityMainBinding

/**
 * TODO
 *
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * TODO
     *
     * @param savedInstanceState
     */
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