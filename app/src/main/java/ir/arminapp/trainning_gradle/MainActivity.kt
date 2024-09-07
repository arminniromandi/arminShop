package ir.arminapp.trainning_gradle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import ir.arminapp.trainning_gradle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.bottomNavigation.menu.getItem(1).isChecked = true
        replaceFragment(HomeFragment())

        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            when(it.itemId){
                R.id.home ->{
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.category ->{
                    replaceFragment(CategoryFragment())
                    true
                }
                R.id.account->{
                    replaceFragment(AccountFragment())
                    true
                }
                else->false
            }
        }

















}
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame , fragment)
            .commit()

    }
}