package com.juksooon.backbone

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView



import com.juksooon.backbone.databinding.SearchWriteItemBinding

class TitleTabAdapter(var myDocList:ArrayList<SearchDocListData>, val fragment_s: Fragment, var context: Context): RecyclerView.Adapter<DocHolder>()
{
    var contxt:Context = context
    lateinit var fragment: Fragment
    lateinit var binding: SearchWriteItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocHolder {
        val binding = SearchWriteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        this.fragment = fragment_s
        return DocHolder(binding)
    }

    // 목록에 보여줄 아이템의 개수
    override fun onBindViewHolder(holder: DocHolder, position: Int) {
        val docListData =  myDocList.get(position)
        holder.setDocList(docListData)

        // 아이템 간 간격 설정
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 59.toPx()
        holder.itemView.requestLayout()

        holder.itemView.setOnClickListener {
            Intent(contxt, ReadingActivity::class.java).apply {
                putExtra("data", myDocList[position].WriteID.toString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { context.startActivity(this) }
        }
    }

    // 목록에 보여줄 아이템의 개수
    override fun getItemCount(): Int {
        return myDocList.size
    }

    // px을 dp 단위로 바꿔주는 코드 (layoutParamas가 px로만 값을 받기 때문에 바꿔줘야 한다.)
    fun Int.toPx():Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}

class DocHolder(val binding: SearchWriteItemBinding): RecyclerView.ViewHolder(binding.root){

    // 화면에 데이터를 세팅하는 setDocList()메서드 구현
    fun setDocList(docListData: SearchDocListData){

        binding.writeIdTitle.text = docListData.title
        binding.writeIdCatName.text = docListData.catName
        binding.writeIdDate.text = docListData.date
    }

}
