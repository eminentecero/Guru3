package com.example.backbone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backbone.databinding.FragmentBottomListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomFragmentList(db: DBHelper)  : BottomSheetDialogFragment(){

    var db:DBHelper = db
    //lateinit var context:Context
    private lateinit var binding1:FragmentBottomListBinding

    // 리사이클러뷰에 붙일 어댑터 선언
    private lateinit var homeCateListAdapter: HomeCateListAdapter

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        var view:View = inflater.inflate(R.layout.fragment_bottom_list, container, false)

        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.category_add).setOnClickListener { view ->
            hoemActivity?.onButtonClicked()
        }

        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //리스트가 딸려있는 곳의 binding 연결
        binding1 = FragmentBottomListBinding.inflate(layoutInflater)
        // xml에서 리사이클러뷰를 가져와서 변수 선언함.
        recyclerView = binding1.docList

        // HomeCateListData 클래스를 담는 배열 생성
        var myDocList = ArrayList<HomeCateListData>()

        //참고용!
        //수영아 사랑해...
        //카테고리 데이터 받아와서 카테고리용 배열에 넣어놓기. - 이해 안되면 HomeActivity에 띄운 거 참고하기.
        var categoryList = ArrayList<String>()
        categoryList = db.getCategory()
        //배열로 받아온 글 객체를 순서대로 출력하기.
        for(i in 0..(categoryList.size-1))
        {
            myDocList.add(
                    HomeCateListData("${categoryList[i]}")
            )
        }
        // 어댑터 변수 초기화
        homeCateListAdapter = HomeCateListAdapter(myDocList, this)

        // 리사이클러 뷰 타입 설정
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 만든 어댑터 recyclerview에 연결
        view.findViewById<RecyclerView>(R.id.docList).adapter = homeCateListAdapter
    }

    //mainactivity의 함수를 사용하기 위해 호출해준 부분
    var hoemActivity: HomeActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        hoemActivity = getActivity() as HomeActivity
    }

    //삭제
    override fun onDetach() {
        super.onDetach()

        hoemActivity = null
    }


    private fun addMoreOnItem() {
        //추가하기 버튼 누르면 될 것 같음
        //homeCateListAdapter = HomeCateListAdapter(myDocList)
    }
    /*
        public fun replaceFragment(fragment: Fragment) {
        var myContext: Context = activity as FragmentActivity
         var fragmentManager:FragmentManager = myContext.getSupportFragmentManager();
         var fragmentTransaction:FragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ContentLayout, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }
     */

}
