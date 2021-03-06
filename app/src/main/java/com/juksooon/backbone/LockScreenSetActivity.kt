package com.juksooon.backbone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.juksooon.backbone.databinding.LockScreenSetBinding

//암호 설정 액티비티
class LockedScreenSetActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:LockScreenSetBinding

    // 비밀번호 4자리를 저장할 배열 생성
    private var PWList= mutableListOf<String>()

    // 사용자가 입력할 비밀번호 4자리
    lateinit var passCode:String

    // 첫 번째 숫자
    lateinit var PW1:String

    // 두 번째 슷자
    lateinit var PW2:String

    // 세 번째 숫자
    lateinit var PW3:String

    // 네 번째 숫자
    lateinit var PW4:String

    //실제로 사용자 DB에 저장되어 있는 비밀번호 정보
    lateinit var UserPassWord: String

    //DBHelper와 이어주도록 클래스 선언
    var db:DBHelper= DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LockScreenSetBinding.inflate(layoutInflater)

        setContentView(binding.root)

        UserPassWord = intent.getStringExtra("reset").toString()

        // 각각의 버튼에 클릭 이벤트 달아주기
        binding.key0.setOnClickListener(this)
        binding.key1.setOnClickListener(this)
        binding.key2.setOnClickListener(this)
        binding.key3.setOnClickListener(this)
        binding.key4.setOnClickListener(this)
        binding.key5.setOnClickListener(this)
        binding.key6.setOnClickListener(this)
        binding.key7.setOnClickListener(this)
        binding.key8.setOnClickListener(this)
        binding.key9.setOnClickListener(this)
        binding.keyDel.setOnClickListener(this)
        binding.keyReset.setOnClickListener(this)

        //PWList에 0번 입력
        if(PWList.size<4)
        {
            PWList.add("0")
            passNumber(PWList)
        }

        //체크 버튼 클릭 리스너
        binding.pwSetBtn.setOnClickListener {
            if(PWList.size==4)
            {
                if(intent.hasExtra("reset"))
                {
                    db.updatePassword(UserPassWord, passCode)
                }else{
                    db.createPassword(passCode)
                }

                // 암호 설정 화면으로 이동
                val lockSetIntent = Intent(this@LockedScreenSetActivity, LockScreenMenuActivity::class.java)
                startActivity(lockSetIntent)
                finish()
            }
            else{
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

        }

        //뒤로가기 버튼 클릭 리스너
        binding.backBtn.setOnClickListener {
            // 암호 설정 화면으로 이동
            val lockMenuIntent = Intent(this@LockedScreenSetActivity, LockScreenMenuActivity::class.java)
            startActivity(lockMenuIntent)
            finish()
        }
    }

    // 해당 버튼을 클릭했을 때 PWList 배열에 해당 숫자를 입력받는다
    override fun onClick(view: View?) {

        when(view) {
            //0번을 누르면
            binding.key0 -> {
                //PWList에 0번 입력
                if(PWList.size<4)
                {
                    PWList.add("0")
                    passNumber(PWList)
                }
            }

            binding.key1 -> {
                if(PWList.size<4)
                {
                    PWList.add("1")
                    passNumber(PWList)
                }
            }

            binding.key2 -> {
                if(PWList.size<4)
                {
                    PWList.add("2")
                    passNumber(PWList)
                }
            }

            binding.key3 -> {
                if(PWList.size<4)
                {
                    PWList.add("3")
                    passNumber(PWList)
                }
            }

            binding.key4 -> {
                if(PWList.size<4)
                {
                    PWList.add("4")
                    passNumber(PWList)
                }
            }

            binding.key5 -> {
                if(PWList.size<4)
                {
                    PWList.add("5")
                    passNumber(PWList)
                }
            }

            binding.key6 -> {
                if(PWList.size<4)
                {
                    PWList.add("6")
                    passNumber(PWList)
                }
            }

            binding.key7 -> {
                if(PWList.size<4)
                {
                    PWList.add("7")
                    passNumber(PWList)
                }
            }

            binding.key8 -> {
                if(PWList.size<4)
                {
                    PWList.add("8")
                    passNumber(PWList)
                }
            }

            binding.key9 -> {
                if(PWList.size<4)
                {
                    PWList.add("9")
                    passNumber(PWList)
                }
            }

            binding.keyDel -> { // 삭제 버튼
                if (PWList.size > 0) {
                    PWList.removeLast()
                    passNumber(PWList)
                }
            }

            binding.keyReset -> { // 초기화 버튼
                PWList.clear()
                passNumber(PWList)
            }
        }
    }

    // 해당 숫자가 클릭되면 자릿수에 따라 화면에 변화를 주고 각 자리 수를 저장한다
    private fun passNumber(pwList: MutableList<String>) {

        //자릿수에 따른 화면 변화
        when(PWList.size){
            0 -> {
                binding.PW1.colorFilter = null
                binding.PW2.colorFilter = null
                binding.PW3.colorFilter = null
                binding.PW4.colorFilter = null
            }
            1 -> {  PW1 = PWList[0]
                binding.PW1.setColorFilter(resources.getColor(R.color.greenMain))
                binding.PW2.colorFilter = null
                binding.PW3.colorFilter = null
                binding.PW4.colorFilter = null
            }

            2 -> {  PW2 = PWList[1]
                binding.PW2.setColorFilter(resources.getColor(R.color.greenMain))
                binding.PW3.colorFilter = null
                binding.PW4.colorFilter = null
            }

            3 -> {  PW3 = PWList[2]
                binding.PW3.setColorFilter(resources.getColor(R.color.greenMain))
                binding.PW4.colorFilter = null}

            4 -> {  PW4 = PWList[3]
                binding.PW4.setColorFilter(resources.getColor(R.color.greenMain))
                passCode = PW1 + PW2 + PW3 + PW4 // 4자리 비밀번호 입력받기 완료!

            }
        }

    }


}