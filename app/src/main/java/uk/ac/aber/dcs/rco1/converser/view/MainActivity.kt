package uk.ac.aber.dcs.rco1.converser.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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

        val navigationDrawer = binding.drawerLayout
        val navDrawerView = binding.navControllerView
        val navController = navHostFragment.navController
        //val navController = findNavController(R.id.navigation_host_fragment)

        //home is only top level nav destination
        val navDrawerConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home
                    //R.id.navigation_download_languages
        ), navigationDrawer)


        //set up to have burger icon in top bar
        setupActionBarWithNavController(navController, navDrawerConfiguration)
        navDrawerView.setupWithNavController(navController)

        val navigationDrawerToggle = ActionBarDrawerToggle(
            this,
            navigationDrawer,
            toolbar,
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        )
        navigationDrawer.addDrawerListener(navigationDrawerToggle)
        navigationDrawerToggle.syncState()

    }
}