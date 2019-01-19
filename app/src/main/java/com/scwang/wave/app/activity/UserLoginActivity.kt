package com.scwang.wave.app.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.scwang.wave.app.R
import com.scwang.wave.app.fragment.WaterWaveFragment
import com.scwang.wave.app.fragment.WavePairFragment
import com.scwang.wave.app.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_user_login.*


class UserLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this)

        login.setOnClickListener {
            //            startActivity(Intent(this, MainActivity::class.java))
            FragmentActivity.start(this, WavePairFragment::class.java)
        }
        tourist_mode.setOnClickListener {
            FragmentActivity.start(this, WaterWaveFragment::class.java)
        }

    }
}
