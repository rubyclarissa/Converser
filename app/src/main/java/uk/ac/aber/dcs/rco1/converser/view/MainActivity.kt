package uk.ac.aber.dcs.rco1.converser.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import uk.ac.aber.dcs.rco1.converser.R
import uk.ac.aber.dcs.rco1.converser.databinding.ActivityMainBinding

/**
 * This is the entry point activity for the app (Single Activity : Many fragments)
 * Hosts either the fragment for the home screen (translator) or the download languages screen
 * Implements AppCompatActivity - provides backwards compatibility of material design components
 */
class MainActivity : AppCompatActivity() {

    //view binding tp simplify view interactions - replaces findViewById method
    private lateinit var binding: ActivityMainBinding

    /**
     * TODO
     * Initialises main activity and sets the content view
     * Also sets up the toolbar, navigation, nav drawer and burger icon to interact with naw drawer
     * @param savedInstanceState - bundle object passed into every Android activity
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

        //set up navigation drawer
        val navigationDrawer = binding.mainActivityDrawerLayout
        val navDrawerView = binding.navControllerView
        val navController = navHostFragment.navController

        //home is only top level nav destination
        val navDrawerConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home
                //R.id.navigation_download_languages
            ), navigationDrawer
        )


        //set up to have burger icon in top bar for naw drawer control
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