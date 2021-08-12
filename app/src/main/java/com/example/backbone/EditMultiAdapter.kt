package com.example.backbone

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.text.*
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.*
import org.jsoup.Jsoup
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


private var isrun:Boolean = false

class EditMultiAdapter(editActivity: EditingActivity, context:Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: EditQuestionItemBinding
    private lateinit var binding2: EditContentItemBinding
    private lateinit var binding3: ActivityEditingBinding
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    var activity = editActivity
    val items = mutableListOf<EditItem>()
    var context = context

    companion object {
        private const val TYPE_Question = 0
        private const val TYPE_Content = 1
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is EditloadQuestionData -> {
            TYPE_Question
        }
        is EditloadContentData -> {
            TYPE_Content
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            TYPE_Question -> {
                binding = EditQuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadQHolder(binding)
            }
            TYPE_Content -> {
                //MyContentHolder.create(parent)
                binding2 = EditContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadContentHolder(binding2)
            }
            else -> {
                throw IllegalStateException("Not Found ViewHolder Type $viewType")
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //실행될 때: 버튼 누를때마다. 본문/질문 이런 거.
        when (holder) {
            is LoadQHolder -> {
                (holder as LoadQHolder).setQList(items[position] as EditloadQuestionData)
                holder.setIsRecyclable(false)
                //선택된 아이템에 대한 정보 빼내오기
                var QuestionList = items[position] as EditloadQuestionData

                //답변 추가 버튼 눌렀을 때 리스너
                binding.addAnswer.setOnClickListener {
                    QuestionList.ColorChanged = true

                    AddAnswer(EditloadQuestionData("추가로 넣는 거", null, null, QuestionList.linkLayout, null, null, "", null,
                            "", binding.addAnswer, binding.qImgAddBtn, binding.qLinkAddBtn, activity.today, false, false), position)
                }

                //답변 추가될 때 리스너
                holder.binding.aTxt.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null
                    //val thisitem= item
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding.aTxt.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding.aTxt.getText().toString()
                                //items[position].
                                QuestionList.aTxt = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        Log.d("태그", "afterTextChanged")
                        QuestionList.aTxt = s.toString()
                        QuestionList.Date = activity.today
                        //updateQuestions에 저장해주기.
                        //updateQuestionItems(QuestionList, position)
                        //Log.d("태그", "afterTextChanged ${QuestionList.id}: ${QuestionList.aTxt}")
                    }
                })

                //질문 입력됐을 때 리스너
                holder.binding.qTitle.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null
                    //val thisitem= item
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding.qTitle.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding.qTitle.getText().toString()
                                //items[position].
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        QuestionList.qTitle = s.toString()
                    }
                })

                //답변 링크 입력 버튼 눌렀을 때!
                if (QuestionList.linkUri == "" && QuestionList.ColorChanged == false && QuestionList.aTxt == "") {
                    holder.binding.qLinkAddBtn.setOnClickListener {
                        holder.binding.linkInsertTxt.visibility = View.VISIBLE
                        holder.binding.linkInsertBtn.visibility = View.VISIBLE
                    }
                }



                //답변 링크 입력됐을 때 리스너
                holder.binding.linkInsertTxt.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null
                    //val thisitem= item
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding.linkInsertTxt.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding.linkInsertTxt.getText().toString()
                                QuestionList.linkUri = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        QuestionList.linkUri = s.toString()
                        //updateQuestionItems(QuestionList, position)
                    }
                })

                //링크 입력 후 확인을 누르면 실행되는 리스너
                holder.binding.linkInsertBtn.setOnClickListener {
                    holder.binding.linkInsertBtn.visibility = View.GONE
                    holder.binding.linkInsertTxt.visibility = View.GONE
                    holder.binding.clLinkArea.visibility = View.VISIBLE
                    //입력 받은 링크를 String으로 넣어 준 후
                    var linkUri = QuestionList.linkUri.toString()
                    //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
                    //쓰레드 실행(한번만 실행함.)
                    holder.loadLink(linkUri, QuestionList)
                    holder.binding.qLinkAddBtn.setClickable(false)
                    holder.binding.qLinkAddBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)
                    holder.binding.linkInsertTxt.setText("")
                }

                //링크 롱클릭 리스너 (변경, 삭제)
                holder.binding.clLinkArea.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if (which == 0) {
                                holder.binding.clLinkArea.visibility = View.GONE
                                holder.binding.linkInsertBtn.visibility = View.VISIBLE
                                holder.binding.linkInsertTxt.visibility = View.VISIBLE
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if (which == 1) {
                                holder.binding.clLinkArea.visibility = View.GONE
                                holder.binding.qLinkAddBtn.setClickable(true)
                                holder.binding.qLinkAddBtn.imageTintList = ColorStateList.valueOf(Color.WHITE)
                                QuestionList.linkUri = null
                            }
                        }
                        ).show()
                    true
                }



                //답변 사진 입력 버튼 눌렀을 때!
                holder.binding.qImgAddBtn.setOnClickListener {
                    //권한이 허용되어있는지 self로 체크(확인)
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                        //허용되지 않았을 때 - 권한이 필요한 알림창을 올림 )
                        //이전에 거부한 적이 있는지 확인
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            var dlg = AlertDialog.Builder(context)
                            dlg.setTitle("권한이 필요한 이유")
                            dlg.setMessage("사진 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다")
                            //OK버튼
                            dlg.setPositiveButton("확인") { dialog, which ->
                                ActivityCompat.requestPermissions(activity,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                            }
                            dlg.setNegativeButton("취소", null)
                            dlg.show()
                        } else {
                            //권한 요청
                            ActivityCompat.requestPermissions(activity,
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
                        }
                    }else{
                        if (QuestionList.aImg == null && QuestionList.ColorChanged == false && QuestionList.aTxt == "") {
                            openGalleryForImage(QuestionList)
                        }
                    }

                }

                //사진 롱클릭 리스너 (변경, 삭제)
                holder.binding.aImg.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if (which == 0) {
                                openGalleryForImage(QuestionList)
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if (which == 1) {
                                holder.binding.aImg.visibility = View.GONE
                                holder.binding.qImgAddBtn.setClickable(true)
                                holder.binding.qImgAddBtn.imageTintList =
                                    ColorStateList.valueOf(Color.WHITE)
                                QuestionList.aImg = null
                            }
                        }
                        ).show()
                    true
                }

            }
            is LoadContentHolder -> {
                (holder as LoadContentHolder).setContentList(items[position] as EditloadContentData)
                holder.setIsRecyclable(false)

                //선택된 아이템에 대한 정보 빼내오기
                var WriteList = items[position] as EditloadContentData
                //holder.setQList(items[position] as WriteQuestionData)

                holder.binding2.docContent.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null
                    //val thisitem= item
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding2.docContent.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                //afterTxt = binding.aTxt.getText().toString()
                                //items[position].
                                WriteList.docContent = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        updateItems(WriteList, position)
                    }
                })

                holder.binding2.linkInsertTxt.addTextChangedListener(object : TextWatcher {
                    var preTxt: String? = null
                    var afterTxt: String? = null
                    //val thisitem= item
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        preTxt = s.toString()
                    }

                    //start 위치에서 before 문자열 갯수의 문자열이 count 갯수만큼 변경되었을 때 호출
                    //CharSequence: 새로 입력한 문자열이 추가된 EditText의 값
                    //before: 삭제된 기존 문자열의 개수
                    //count: 새로 추가된 문자열의 개수
                    override fun onTextChanged(s: CharSequence, i: Int, i2: Int, i3: Int) {
                        if (binding2.linkInsertTxt.isFocusable() && !s.toString().equals(preTxt)) {
                            try {
                                afterTxt = binding2.linkInsertTxt.getText().toString()
                                WriteList.linkUri = s.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    //EditText의 Text가 변경된 것을 다른 곳에 통보할 때 사용.
                    override fun afterTextChanged(s: Editable) {
                        WriteList.linkUri = s.toString()
                        updateItems(WriteList, position)
                    }
                })

                //링크 입력 후 확인을 누르면 실행되는 리스너
                holder.binding2.linkInsertBtn.setOnClickListener {
                    holder.binding2.linkInsertBtn.visibility = View.GONE
                    holder.binding2.linkInsertTxt.visibility = View.GONE
                    holder.binding2.clLinkArea.visibility = View.VISIBLE
                    //입력 받은 링크를 String으로 넣어 준 후
                    var linkUri2 = WriteList.linkUri.toString()
                    //loadLink에 있는 쓰레드를 구동시키기 위해서는 isrun이 ture가 되어있어야 함.
                    //쓰레드 실행(한번만 실행함.)
                    holder.loadLink(linkUri2, WriteList)
                    holder.binding2.linkInsertTxt.setText("")
                }

                //링크 롱클릭 리스너 (변경, 삭제)
                holder.binding2.clLinkArea.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if(which == 0){
                                holder.binding2.clLinkArea.visibility = View.GONE
                                holder.binding2.linkInsertBtn.visibility = View.VISIBLE
                                holder.binding2.linkInsertTxt.visibility = View.VISIBLE
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if(which == 1){
                                holder.binding2.clLinkArea.visibility = View.GONE
                                removeItems(position)
                            }
                        }
                        ).show()
                    true
                }

                //사진 롱클릭 리스너 (변경, 삭제)
                holder.binding2.contentImg.setOnLongClickListener {
                    val selectList = arrayOf("변경", "삭제")
                    var selectDialog =
                        AlertDialog.Builder(context, R.style.LongClickPopUp)

                    selectDialog
                        .setItems(selectList, DialogInterface.OnClickListener { dialog, which ->

                            // 변경 버튼을 클릭했을 때
                            if (which == 0) {
                                activity.openGalleryForImage()
                                removeItems(position)
                            }
                            // 삭제 버튼을 클릭했을 때
                            else if (which == 1) {
                                holder.binding2.contentImg.visibility = View.GONE
                                removeItems(position)
                            }
                        }
                        ).show()
                    true
                }

            }
        }
    }

    // 질문 Holder
    class LoadQHolder(val binding: EditQuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setQList(item: EditloadQuestionData) {
            Log.d("태그", "${item.aTxt}")
            // 질문 제목
            if (item.qTitle == "" || item.qTitle == null) {
                binding.qIcon.visibility = View.GONE
                binding.qTitle.visibility = View.GONE
            } else {
                binding.qTitle.setText(item.qTitle)
                binding.qTitle.setClickable(false);
                binding.qTitle.setFocusable(false);
            }

            if (item.onActivityCalled == true) {
                binding.qIcon.visibility = View.VISIBLE
                binding.qTitle.visibility = View.VISIBLE
            }

            if (item.aImg != null) {
                //*****나중에 구현
                // 삽입 이미지
                binding.aImg.setImageBitmap(item.aImg)
            } else {
                binding.aImg.visibility = View.GONE
            }

            binding.clLinkArea.visibility = View.GONE
            binding.linkInsertTxt.visibility = View.GONE
            binding.linkInsertBtn.visibility = View.GONE

            // 링크
            if (item.linkUri == "" || item.linkUri == null) {
                //링크 내용이 없으면?
                binding.clLinkArea.visibility = View.GONE
            } else {
                // 링크 정보는 있는데. 두번째로 불러온 정보일 때 -> 첫번째 정보에서 이미 받아온 링크 내용, 이미지 등 정보가 있을 때
                if (item.linkContent != null || item.linkTitle != null) {
                    binding.clLinkArea.visibility = View.VISIBLE
                    binding.linkTitle.text = item.linkTitle.toString()
                    binding.linkContent.text = item.linkContent.toString()
                    binding.linkUri.text = item.linkUri.toString()
                    if (item.linkIcon != null) {
                        binding.linkIcon.setImageBitmap(item.linkIcon)
                    } else {
                        binding.linkIcon.visibility = View.GONE
                    }
                    //링크 내용이 있으면?
                    //binding.clLinkArea.visibility = item.linkLayout?.visibility!!
                } else {
                    // 링크 정보를 불러오는 것이 처음 일때!
                    binding.clLinkArea.visibility = View.VISIBLE
                    loadLink(item.linkUri.toString(), item)
                }
            }

            // 대답 내용 삽입
            if (item.aTxt != "" && item.aTxt != null) {
                // 대답 상태에 따라 색 바꿔줌. - 대답이 2개 이상인 경우를 고려
                if (item.ColorChanged == true) {
                    binding.addAnswer.visibility = View.GONE
                    // 대답이 이전 대답일 때
                    var date: String? = item.Date
                    var text: String = item.aTxt + "\n${date}"
                    var start = text.indexOf(date!!)
                    var end = start + date!!.length
                    val spannableString = SpannableString(text)
                    spannableString.setSpan(ForegroundColorSpan(Color.GRAY), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(RelativeSizeSpan(0.8f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.aTxt.setText(spannableString)
                    binding.addAnswer.visibility = View.GONE
                    binding.aTxt.setClickable(false)
                    binding.aTxt.setFocusable(false)
                    binding.qImgAddBtn.setClickable(false)
                    binding.qImgAddBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)
                    binding.qLinkAddBtn.setEnabled(false)
                    binding.qLinkAddBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)
                } else {
                    // 대답이 마지막 대답일 때
                    var date: String? = item.Date
                    var text: String = item.aTxt + "\n${date}"
                    var start = text.indexOf(date!!)
                    var end = start + date!!.length
                    val spannableString = SpannableString(text)
                    spannableString.setSpan(ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(RelativeSizeSpan(0.8f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.aTxt.setText(spannableString)
                    binding.aTxt.setClickable(false);
                    binding.aTxt.setFocusable(false);
                    binding.qImgAddBtn.setClickable(false)
                    binding.qImgAddBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)
                    binding.qLinkAddBtn.setClickable(false)
                    binding.qLinkAddBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }
            // 임베드 누르면 인터넷 연결되어서 화면이 넘어가는 리스너
            binding.clLinkArea.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${item.linkUri}"))
                binding.root.context.startActivity(intent)
            }

        }


        fun setLink(linkUri: String, title: String, content: String, bm1: Bitmap?) {
            binding.clLinkArea.visibility = View.VISIBLE

            if (bm1 == null) {
                binding.linkIcon.visibility = View.GONE
            }
            binding.linkUri.text = linkUri
            binding.linkTitle.text = title
            binding.linkContent.text = content
            binding.linkIcon.setImageBitmap(bm1)
        }

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content: String = ""

        fun loadLink(uri: String, item: EditloadQuestionData) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            linkUri = uri
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true


            Thread(Runnable {
                while (isrun) {//네이버의 경우에만 해당되는 것 같아.
                    try {
                        if (linkUri.contains("naver")) {
                            Log.d("태그", "네이버")
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            //linkIcon에 파비콘 추출해서 삽입하기
                            val doc = Jsoup.connect("${linkUri}").get()

                            //제목 들고 오기
                            val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                            if (linkUri.contains("blog")) {
                                val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                                title = doc2.title()
                                content = doc2.select("meta[property=\"og:description\"]").attr("content")
                            } else if (linkUri == "https://www.naver.com/") {
                                title = doc.title()
                                content = doc.select("meta[name=\"og:description\"]").attr("content")
                            } else {
                                title = doc.title()
                                content = doc.select("meta[property=\"og:description\"]").attr("content")
                            }
                            url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)
                            if (bm1 == null) {
                                binding.linkIcon.visibility = View.GONE
                            }

                            bis.close()
                            item.linkUri = linkUri
                            item.linkTitle = title
                            item.linkContent = content
                            item.linkIcon = bm1
                            setLink(linkUri, title, content, bm1)
                            isrun = false
                        } else {
                            if (!linkUri.contains("http")) {
                                linkUri = "https://${linkUri}"
                                Log.d("태그", "링크 고침: ${linkUri}")
                            }
                            val doc = Jsoup.connect("${linkUri}").get()
                            var favicon: String
                            var link: String
                            if (linkUri.contains("google")) {
                                favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                                link = "https://www.google.com" + favicon
                                url1 = URL("${link}")
                            } else {
                                //파비콘 이미지 들고 오기
                                favicon = doc.select("link[rel=\"icon\"]").attr("href")
                                if (favicon == "") {
                                    favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                                }
                                if (!favicon.contains("https:")) {
                                    link = "https://" + favicon
                                    url1 = URL("${link}")
                                } else {
                                    url1 = URL("${favicon}")
                                }
                            }

                            try {
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                bis.close()
                            } catch (e: Exception) {
                                binding.linkIcon.visibility = View.GONE
                            }
                            title = doc.title()

                            content = doc.select("meta[name=\"description\"]").attr("content")
                            if (content == "") {
                                content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if (title == "") {
                                title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            item.linkUri = linkUri
                            item.linkTitle = title
                            item.linkContent = content
                            item.linkIcon = bm1
                            setLink(linkUri, title, content, bm1)
                            isrun = false
                        }
                    } catch (e: Exception) {
                        //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

                    }
                }
            }).start()
        }

    }

    // 본문 Hodler
    class LoadContentHolder(val binding2: EditContentItemBinding) : RecyclerView.ViewHolder(binding2.root) {

        // 링크 삽입 관련 메소드
        var linkUri: String = ""
        var title: String = ""
        var bm1: Bitmap? = null
        var url1: URL? = null
        var content: String = ""

        fun setContentList(item: EditloadContentData) {
            //사진 띄우기 **** - 나중에 하기.
            if (item.contentImg != null) {
                binding2.contentImg.setImageBitmap(item.contentImg)
            } else {
                binding2.contentImg.visibility = View.GONE
            }

            //본문내용(텍스트)
            if (item.docContent == null) {
                binding2.docContent.visibility = View.GONE
            } else {
                binding2.docContent.setText(item.docContent)
            }

            binding2.linkInsertBtn.visibility = View.GONE
            binding2.linkInsertTxt.visibility = View.GONE

            Log.d("태그", "${item.docContent}")
            Log.d("태그", "${item.linkUri}")
            // 링크
            if(item.linkUri == ""||item.linkUri == null){
                if(item.linkInsertTxt != null && item.linkInsertBtn != null)
                {
                    binding2.linkInsertBtn.visibility = View.VISIBLE
                    binding2.linkInsertTxt.visibility = View.VISIBLE
                    binding2.clLinkArea.visibility = View.GONE
                }else{
                    //링크 내용이 없으면?
                    binding2.clLinkArea.visibility = View.GONE
                }
            }else{
                // 링크 정보는 있는데. 두번째로 불러온 정보일 때 -> 첫번째 정보에서 이미 받아온 링크 내용, 이미지 등 정보가 있을 때
                if(item.linkContent != null || item.linkTitle != null)
                {
                    binding2.clLinkArea.visibility = View.VISIBLE
                    binding2.linkTitle.text = item.linkTitle.toString()
                    binding2.linkContent.text = item.linkContent.toString()
                    binding2.linkUri.text = item.linkUri.toString()
                    if(item.linkIcon != null)
                    {
                        binding2.linkIcon.setImageBitmap(item.linkIcon)
                    }else{
                        binding2.linkIcon.visibility = View.GONE
                    }
                }else{
                    // 링크 정보를 불러오는 것이 처음 일때!
                    binding2.clLinkArea.visibility = View.VISIBLE
                    loadLink(item.linkUri.toString(), item)
                }
            }

            binding2.clLinkArea.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${item.linkUri}"))
                binding2.root.context.startActivity(intent)
            }
        }

        fun setLink(linkUri: String, title: String, content: String, bm1: Bitmap?) {
            binding2.linkUri.text = linkUri
            binding2.linkTitle.text = title
            binding2.linkContent.text = content
            binding2.linkIcon.setImageBitmap(bm1)
        }

        companion object Factory {
            fun create(parent: ViewGroup): LoadContentHolder {
                val binding2 = EditContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LoadContentHolder(binding2)
            }
        }

        fun loadLink(url: String, item: EditloadContentData) {
            //함수 실행하면 쓰레드에 필요한 메소드 다 null해주기
            var linkUri = url
            title = ""
            bm1 = null
            url1 = null
            content = ""
            isrun = true
            Thread(Runnable {
                while (isrun) {//네이버의 경우에만 해당되는 것 같아.
                    try {
                        if (linkUri.contains("naver")) {
                            if (!linkUri.contains("http")) {
                                linkUri = "https://${linkUri}"
                            }
                            //linkIcon에 파비콘 추출해서 삽입하기
                            val doc = Jsoup.connect("${linkUri}").get()

                            //제목 들고 오기
                            val link2 = doc.select("body").select("iframe[id=mainFrame]").attr("src")//.attr("content")
                            if (linkUri.contains("blog")) {
                                val doc2 = Jsoup.connect("https://blog.naver.com/${link2}").get()
                                title = doc2.title()
                                content = doc2.select("meta[property=\"og:description\"]").attr("content")
                            } else if (linkUri == "https://www.naver.com/") {
                                title = doc.title()
                                content = doc.select("meta[name=\"og:description\"]").attr("content")
                            } else {
                                title = doc.title()
                                content = doc.select("meta[property=\"og:description\"]").attr("content")
                            }
                            url1 = URL("https://ssl.pstatic.net/sstatic/search/favicon/favicon_191118_pc.ico")
                            var conn: URLConnection = url1!!.openConnection()
                            conn.connect()
                            var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                            bm1 = BitmapFactory.decodeStream(bis)
                            if (bm1 == null) {
                                binding2.linkIcon.visibility = View.GONE
                            }


                            bis.close()
                            item.linkUri = linkUri
                            item.linkTitle = title
                            item.linkContent = content
                            item.linkIcon = bm1
                            Log.d("태그", "아이템에 잘 저장 되어있나?? ${item.linkTitle}")
                            setLink(linkUri, title, content, bm1)
                            isrun = false
                        } else {
                            if (!linkUri.contains("https://")) {
                                linkUri = "https://${linkUri}"
                            }
                            val doc = Jsoup.connect("${linkUri}").get()
                            var favicon: String
                            var link: String
                            if (linkUri.contains("google")) {
                                favicon = doc.select("meta[itemprop=\"image\"]").attr("content")
                                link = "https://www.google.com" + favicon
                                url1 = URL("${link}")
                            } else {
                                //파비콘 이미지 들고 오기
                                favicon = doc.select("link[rel=\"icon\"]").attr("href")
                                if (favicon == "") {
                                    favicon = doc.select("link[rel=\"SHORTCUT ICON\"]").attr("href")
                                }
                                if (!favicon.contains("https:")) {
                                    link = "https://" + favicon
                                    url1 = URL("${link}")
                                } else {
                                    url1 = URL("${favicon}")
                                }
                            }

                            try {
                                var conn: URLConnection = url1!!.openConnection()
                                conn.connect()
                                var bis: BufferedInputStream = BufferedInputStream(conn.getInputStream())
                                bm1 = BitmapFactory.decodeStream(bis)
                                bis.close()
                            } catch (e: Exception) {
                                binding2.linkIcon.visibility = View.GONE
                            }
                            title = doc.title()

                            content = doc.select("meta[name=\"description\"]").attr("content")
                            if (content == "") {
                                content = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if (title == "") {
                                title = doc.select("meta[property=\"og:site_name\"]").attr("content")
                            }
                            if (bm1 == null) {
                                binding2.linkIcon.visibility = View.GONE
                            }

                            //선택된 아이템에 대한 정보 빼내오기
                            item.linkUri = linkUri
                            item.linkTitle = title
                            item.linkContent = content
                            item.linkIcon = bm1
                            setLink(linkUri, title, content, bm1)
                            Log.d("태그", "아이템에 잘 저장 되어있나?? ${item.linkTitle}")
                            isrun = false
                        }
                    } catch (e: Exception) {
                        //링크가 올바르지 않을때->안내 토스트 메시지를 띄움

                    }
                }
            }).start()
        }
    }





    /*
본문 제목: docTitle
본문 이미지: titleImg

<링크 삽입을 위한 요소들>
텍스트입력 받는 곳: linkInsertTxt
확인 버튼: linkInsertBtn

<링크된 영역>
링크영역 항목들을 감싸는 레이아웃 = linkLayout
제목 = linkTitle
내용 = linkContent
uri = linkUri
아이콘 = linkIcon
썸네일 = linkImg

본문 내용: docContent

 */

    override fun getItemCount() = items.size

    fun updateItems(item: EditItem, position: Int)
    {
        var WriteList = item as EditloadContentData
        Log.d("태그", "${WriteList.id}")
        Log.d("태그", "본문 추가: ${activity.writeContentList.size}")
        activity.writeContentList[WriteList.id].docContent = WriteList.docContent
        activity.writeContentList[WriteList.id].linkUri = WriteList.linkUri
    }

    /*


        fun updateLoadQuestionItem(item: EditItem)
    {
        //var activity:EditingActivity = EditingActivity()
        var EditList = item as ReadQuestionData
        activity.editContentList[EditList.id].docContent = EditList.docContent
        activity.editContentList[EditList.id].linkUri = EditList.linkUri
    }
     */

    fun addItems(item: EditItem) {
        this.items.add(item)
        this.notifyDataSetChanged()
    }

    fun AddAnswer(item: EditItem, position: Int) {
        //선택한 대답 바로 밑에 내용 추가.
        this.items.add(position+1, item)
        this.notifyDataSetChanged()
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)
    }

    //를릭 리스너
    private lateinit var itemClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    // answer 이미지, 링크 추가 시 사용
    fun modifyItems(position: Int, item: EditItem) {
        this.items.set(position, item)
        this.notifyDataSetChanged()
    }

    class ContentDiffUtil(private val oldList: List<EditItem>, private val currentList: List<EditItem>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = currentList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == currentList[newItemPosition].id

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldList = oldList[oldItemPosition]
            val currentList = currentList[newItemPosition]

            return oldList == currentList
        }

    }

    fun updateList(items: List<EditItem>?) {
        items?.let {
            val diffCallback = ContentDiffUtil(this.items, items)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.items.run {
                clear()
                addAll(items)
                diffResult.dispatchUpdatesTo(this@EditMultiAdapter)
            }
        }
    }

    private val REQUEST_TAKE_ALBUM = 1
    var itemInfo: EditloadQuestionData? = null
    private fun openGalleryForImage(item: EditloadQuestionData) {
        itemInfo = item
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_TAKE_ALBUM)
    }

    @Override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode){
            1 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_ALBUM) {
                    if (data != null) {
                        var photo: InputStream? = activity.contentResolver.openInputStream(data.getData()!!)
                        val img = BitmapFactory.decodeStream(photo)
                        if (photo != null) {
                            photo.close()
                        }
                        this.itemInfo?.aImg = img
                        this.binding.aImg.visibility = View.VISIBLE
                        this.binding.aImg.setImageBitmap(img)
                        binding.qImgAddBtn.setClickable(false)
                        binding.qImgAddBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)

                    }
                }
            }
        }
    }

    fun removeItems(position: Int) {
        this.items.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyDataSetChanged()

    }

}