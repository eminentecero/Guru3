package com.juksooon.backbone

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

interface WriteItem {
    abstract val id: Any
}

data class WriteQuestionData(
    override var id: Int, var qTitle: String?, var aImg: Bitmap?,
    var linkInsertTxt: String?, var linkInsertBtn: Button?,
    var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?,
    var aTxt: String?, var addAnswer: ImageButton?, var qImgAddBtn:ImageButton?, var qLinkAddBtn:ImageButton?, var Date:String?): WriteItem

data class WriteContentData(
        override var id: Int, var contentImg: Bitmap?,
        var linkInsertTxt: EditText?, var linkInsertBtn:Button?,
        var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?,
        var docContent:String?, var qImgAddBtn:ImageButton?, var qLinkAddBtn:ImageButton?): WriteItem





//onActivityCalled: 수정의 경우 대답 밑 버튼을 눌러서 추가된 것이면 false, 액티비티에 리스너가 있는 밑에 바에서 질문을 추가 버튼을 눌러서 추가된 것이면 true
data class loadQuestionData(
        override var id: String, var qTitle: String?, var aImg: ByteArray?,
        var linkLayout: View?, var linkTitle:String?, var linkContent: String?, var linkUri:String?, var linkIcon: Bitmap?, var linkImg: Drawable?,
        var aTxt: String?, var Date:String?, var ColorChanged:Boolean?, var onActivityCalled:Boolean?):WriteItem
data class loadContentData(override var id: Int, var contentImg: ByteArray?,
                           var linkLayout: View?, var linkTitle:String?, var linkContent:String?, var linkUri:String?, var linkIcon: Bitmap?, var linkImg: Drawable?,
                           var docContent: String?):WriteItem
/*
<<WriteQuestionData>>
qIcon: 질문 아이콘
qTitle: 질문 제목
aImg: 대답(삽입)이미지

<링크 삽입 영역>
linkInsertTxt: 링크 텍스트 넣는 곳
linkInsertBtn: 확인버튼

<임베디드 영역>
linkLayout : 영역을 담고 있는 레이아웃(임베드된 게 없으면 View.Gone으로 하면 되고, 있으면 보이게 해주세요)
linkTtile: 사이트 이름
linkUri: 사이트 주소
linkIcon: 사이트 아이콘
linkImg: 썸네일

aIcon: 대답 아이콘
aTxt: 대답 텍스트영역
*/

/*
<<WriteContentData>>
contentImg : 본문 이미지

<링크 삽입 영역>
linkInsertTxt: 링크 텍스트 넣는 곳
linkInsertBtn: 확인버튼

<임베디드 영역>
linkLayout : 영역을 담고 있는 레이아웃(링크된 게 없으면 View.Gone 으로 하면 되고, 있으면 보이게 해주세요)
linkTtile: 사이트 이름
linkUri: 사이트 주소
linkIcon: 사이트 아이콘
linkImg: 썸네일

docContent: 본문 텍스트 영역

* */