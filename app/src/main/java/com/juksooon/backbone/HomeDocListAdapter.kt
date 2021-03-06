package com.juksooon.backbone

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.juksooon.backbone.databinding.HomeWriteItemBinding

class HomeDocListAdapter(context: HomeActivity, var myDocList:ArrayList<HomeDocListData>): RecyclerView.Adapter<Holder>(){

    var context: Context = context
    private lateinit var binding: HomeWriteItemBinding

    // 한 화면에 그려지는 아이템 개수만큼 레이아웃 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder{
        val binding = HomeWriteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return Holder(binding, context)
    }

    // 목록에 보여줄 아이템의 개수
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val docListData =  myDocList.get(position)
        holder.setDocList(docListData)


    }

    // 목록에 보여줄 아이템의 개수
    override fun getItemCount(): Int {
        return myDocList.size
    }
    fun setData(data : ArrayList<HomeDocListData>){
        myDocList = data
        notifyDataSetChanged()
    }


}

// Holder 를 생성해서 adapter 에 연결해야 요청이 있을 때마다 꺼내서 사용가능!
// ListView 는 그냥 담겨 있는 내용을 한 번에 쫙- 불러오기 때문에 메모리를 많이 잡아 먹지만
// Recyclerview 는 말 그대로 Holder 덕분에 필요할 때만 해당 화면 보여줌! => 메모리 적게 잡아먹음
class Holder(val binding: HomeWriteItemBinding, var context: Context): RecyclerView.ViewHolder(binding.root){

    // 화면에 데이터를 세팅하는 setDocList()메서드 구현
    fun setDocList(myDocList: HomeDocListData){
        binding.questionIcon.setImageDrawable(myDocList.icon)
        binding.writeIdTitle.text = myDocList.title
        binding.writeIdCatName.text = myDocList.catName
        binding.writeIdDate.text = myDocList.date
        binding.questionDivide.text = myDocList.divideTxt
        binding.writeIdQCount.text = myDocList.qCount

        itemView.setOnClickListener {
            Intent(context, ReadingActivity::class.java).apply {
                putExtra("data", myDocList.writeID.toString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { context.startActivity(this) }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int){

        }

    }
    //를릭 리스너
    private lateinit var itemClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}

