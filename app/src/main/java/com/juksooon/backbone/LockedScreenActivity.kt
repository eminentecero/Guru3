package com.juksooon.backbone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.juksooon.backbone.databinding.ActivityLockedScreenBinding
import java.lang.Exception

//스플래쉬 화면 뒤에 나오는 맨 처음 나오는 잠금화면
//잠금 화면 액티비티
class LockedScreenActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityLockedScreenBinding

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

        //현재 사용자에게 비밀번호를 관리하는 테이블이 있는지 확인
        //존재한다면 -> 그대로 실행
        //존재하지 않는다면 -> HomeActivity로 이동
        try{
            //User 테이블이 있는지 SQL문으로 돌려보기
            db.PWisExist()
        }catch (e: Exception){
            //존재 하지 않으면 익셉션 발생하므로 그냥 flag 변수 false로 설정해주기.
            //만약 사용자 User 테이블이 존재하지 않는다면 바로 홈 화면으로 넘어가기
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }



        binding = ActivityLockedScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

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
    }

    // 해당 버튼을 클릭했을 때 PWList 배열에 해당 숫자를 입력받는다
    override fun onClick(view: View?) {

        when(view) {
            //0번을 누르면
            binding.key0 -> {
                //PWList에 0번 입력
                PWList.add("0")
                //
                passNumber(PWList)
            }

            binding.key1 -> {
                PWList.add("1")
                passNumber(PWList)
            }

            binding.key2 -> {
                PWList.add("2")
                passNumber(PWList)
            }

            binding.key3 -> {
                PWList.add("3")
                passNumber(PWList)
            }

            binding.key4 -> {
                PWList.add("4")
                passNumber(PWList)
            }

            binding.key5 -> {
                PWList.add("5")
                passNumber(PWList)
            }

            binding.key6 -> {
                PWList.add("6")
                passNumber(PWList)
            }

            binding.key7 -> {
                PWList.add("7")
                passNumber(PWList)
            }

            binding.key8 -> {
                PWList.add("8")
                passNumber(PWList)
            }

            binding.key9 -> {
                PWList.add("9")
                passNumber(PWList)
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

                // 여기서 passCode가 기존에 등록해 놓은 것과 일치하면 홈화면으로 통과시키기
                // 기존에 등록해 놓은 비밀번호 내용 불러오기
                UserPassWord = db.getUserPassWord()
                if(passCode == UserPassWord)
                {
                    //두 내용이 일치하면 홈 화면으로 넘어가기
                    var intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    //비밀번호 일치 하지 않으면 -> 일치하지 않는다는 토스트 메시지 띄우고, 입력되었던 내용 다 지우기
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                    //입력되었던 내용 다 지우고
                    PWList.removeAll(PWList)
                    //해당 내용 UI에 반영하기 위해 콜백 함수로 이용.
                    passNumber(PWList)
                }

            }
        }

    }


}