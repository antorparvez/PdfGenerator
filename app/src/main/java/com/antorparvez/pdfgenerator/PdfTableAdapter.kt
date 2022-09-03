package com.antorparvez.pdfgenerator
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.antorparvez.pdfgenerator.databinding.VhListItemBinding

class PdfTableAdapter(
    val context: Context,
    val list: List<ListModel>
): RecyclerView.Adapter<PdfTableAdapter.PdfItemListViewHolder>() {

    inner class PdfItemListViewHolder(private val holder: VhListItemBinding) :
        RecyclerView.ViewHolder(holder.root) {
        fun bindData(item: ListModel) {
            // Glide.with(context).load(product.imageUrl).into(holder.productIv);
            //holder.currentPriceTv.typeface = ResourcesCompat.getFont(context, R.font.hind_siliguri_bold)
            holder.col1.text = "$adapterPosition "+ item.col1
            holder.col2.text = item.col2
            holder.col3.text = item.col3
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PdfItemListViewHolder {
        val binding =
            VhListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PdfItemListViewHolder(binding)

    }

    override fun onBindViewHolder(holder: PdfItemListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int {
       return list.size
    }
}