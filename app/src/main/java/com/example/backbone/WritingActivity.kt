package com.example.backbone

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.ActivityWritingBinding
import com.example.backbone.databinding.CancelWritingBinding
import com.example.backbone.databinding.WriteContentItemBinding
import com.example.backbone.databinding.WriteQuestionItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.jsoup.Jsoup


class WritingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWritingBinding

    private lateinit var binding2:WriteQuestionItemBinding
    private lateinit var binding3:WriteContentItemBinding

    private lateinit var binding4:CancelWritingBinding

    // 리사이클러뷰에 붙일 어댑터 선언
   // private lateinit var writingAdapter: WritingAdapter
    private lateinit var writingAdapter:WriteMultiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding2 = WriteQuestionItemBinding.inflate(layoutInflater)
        binding3 = WriteContentItemBinding.inflate(layoutInflater)

        binding4 = CancelWritingBinding.inflate(layoutInflater)

        // write_qustion_item.xml에서 view들 가져오기
        val qIcon = binding2.qIcon
        val aIcon = binding2.aIcon
        val qTitle = binding2.qTitle
        val aTxt = binding2.aTxt
        val addBtn= binding2.addAnswer
        val qlinkLayout = binding2.clLinkArea

        //write_content_item.xml에서 view들 가져오기
        val docContent = binding3.docContent
        val contentImg = binding3.contentImg
        val clinkLayout = binding3.clLinkArea
        val clinkInsertTxt = binding3.linkInsertTxt
        val clinkInsertBtn = binding3.linkInsertBtn


        // 글쓰기 취소 버튼 눌렀을 때 뜨는 팝업
        binding.cancelButton.setOnClickListener {

            val mBuilder = AlertDialog.Builder(this,R.style.MyDialogTheme).setView(binding4.root)

            // view의 중복 사용을 방지하기 위한 코드
            if(binding4.root.parent != null)
                (binding4.root.parent as ViewGroup).removeView(binding4.root)

            val  mAlertDialog = mBuilder.show()

            // 확인 버튼 다이얼로그
            binding4.confirmBtn.setOnClickListener {
                // 홈 화면으로 이동
                //val backIntent = Intent(this@WritingActivity, HomeActivity::class.java)
                //startActivity(backIntent)
                finish()
            }

            //취소 버튼 다이얼로그
            binding4.cancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
            }

        }

        //어댑터 연결
        writingAdapter = WriteMultiAdapter()
        binding.docList.adapter = writingAdapter

                /*
                        //시작할 때 title과 content만 뜨도록 하기
        binding.contentImg.visibility = View.GONE


        binding.linkContent.visibility = View.GONE
        binding.linkUri.visibility = View.GONE
        binding.linkInsertTxt.visibility = View.GONE
        binding.linkInsertBtn.visibility = View.GONE
        binding.linkImg.visibility = View.GONE
        binding.linkIcon.visibility = View.GONE
        binding.linkTitle.visibility = View.GONE

        binding.clLinkArea.visibility = View.GONE
                 */




        //기본 질문 생성
        //writingAdapter.addItems(WriteQuestionData(qTitle,null,null,null,null,null,
        //    null,null,null,aTxt,null))

        // 리사이클러 뷰 타입 설정
        binding.docList.layoutManager = LinearLayoutManager(this)

        binding.linkInsertTxt.setText("https://blog.naver.com/rapael860429/222441480711")
        var linkUri:String = binding.linkInsertTxt.getText().toString()
        var title:String = ""
        var content:String = ""


        //네트워크를 통한 작업이기 때문에 비동기식으로 구현을 해야 한다.
        Thread(Runnable {
            //linkIcon에 파비콘 추출해서 삽입하기
            Log.d("태그그", "${linkUri}")
            val doc = Jsoup.connect("${linkUri}").get()

            //이미지 들고 오기
            val favicon = doc.select("link[rel=shortcut icon]").first().attr("href")
            Log.d("태그그", "${favicon}")

            //제목 들고 오기
            //val Title = doc.select("meta[name=\"title\"]").attr("content")
            val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")

            val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
            title = doc2.title()
            content = doc2.select("meta[property=\"og:description\"]").attr("content")

            Log.d("태그그", "${title}")
            Log.d("태그그", "${content}")

            val linkImg = doc.select("link[rel=\"image_src\"]").attr("src")
            Log.d("태그그", "${linkImg}")

            //val links = doc.select("a[href]")

            Log.d("태그그", "여기 읽히냐")
            //

            this@WritingActivity.runOnUiThread(java.lang.Runnable {
                //println(doc)
                //어답터 연결하기
                binding.docList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                var adapter = WriteMultiAdapter()
                binding.docList.adapter = adapter
            })
        }).start()

        Log.d("태그", "${title}")
        binding.linkUri.text = linkUri
        binding.linkTitle.text = title
        binding.linkContent.text = content

        //하단의 '본문' 버튼 클릭 리스너
        binding.addContentBTN.setOnClickListener {
            //본문 객체 생성
            writingAdapter.addItems(WriteContentData(null,null,null,null,title,content,
                linkUri,null,null, docContent))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '링크' 버튼 클릭 리스너
        binding.addLinkBtn.setOnClickListener {
            // 본문에 링크 생성
            writingAdapter.addItems(WriteContentData(null,clinkInsertTxt, clinkInsertBtn, null,null,null,
                null,null,null, null))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '사진' 버튼 클릭 리스너
        binding.addImgBtn.setOnClickListener {
            // 본문에 이미지 생성
            writingAdapter.addItems(WriteContentData(contentImg,null,null,null,null,null,
                null,null,null, null))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()
        }

        //하단의 '질문' 버튼 클릭 리스너
        binding.addQBtn.setOnClickListener {
            // 질문 추가
            writingAdapter.addItems(WriteQuestionData(qTitle,null,null,null,null,null,
                null,null,null,aTxt,null))

            //어댑터에 notifyDataSetChanged()를 선언해 변경된 내용을 갱신해 줌
            writingAdapter.notifyDataSetChanged()

        }
    }
}