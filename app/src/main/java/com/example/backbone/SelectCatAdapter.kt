package com.example.backbone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.backbone.databinding.CategorySelectItemBinding

class SelectCatAdapter(context: Context, private val categoryArrayList:ArrayList<CategoryList>):BaseAdapter() {

    // 이건 그냥 화면 연결을 위한 코드... 깊이 이해할 필요 없는 코드!! ! (나도 이해 못함 ㅋㅋ)
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    // category_select_item.xml 화면 불러오기
    lateinit var binding: CategorySelectItemBinding

    // 리스트에 아이템이 몇 개가 들어있는 지 갯수 반환
    override fun getCount(): Int = categoryArrayList.size

    // 몇 번 째 아이템을 가져올 건지
    override fun getItem(position: Int): Any = categoryArrayList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    // 리스트 뷰에 보이는 내용
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
       binding = CategorySelectItemBinding.inflate(inflater,p2,false)
        binding.cateRadioBtn.text = categoryArrayList[position].categoryName

        return binding.root
    }

}