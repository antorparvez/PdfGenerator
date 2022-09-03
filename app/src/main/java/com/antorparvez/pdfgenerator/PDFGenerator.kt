package com.antorparvez.pdfgenerator

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.antorparvez.pdfgenerator.databinding.PdfLayoutBinding
import java.io.File
import java.io.FileOutputStream

class PDFGenerator {
    private fun createBitmapFromView(
        context: Context,
        view: View,
        pdfDetails: PdfModel,
       // adapter: MarksRecyclerAdapter,
        adapterList: PdfTableAdapter,
        activity: Activity
    ): Bitmap {
        val binding = PdfLayoutBinding.bind(view)
        //binding.lifecycleOwner = binding.root.findViewTreeLifecycleOwner()    --   To be used when working with lifecycle components
       /* binding.pdfDetails = pdfDetails
        binding.executePendingBindings()
        binding.pdfMarks.adapter = adapter*/

        binding.columnRv.apply {
            binding.columnRv.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            binding.columnRv.adapter = adapterList
        }
        binding.columnRv.adapter?.notifyDataSetChanged()

        binding.nameTv.text = pdfDetails.firstName
        binding.lastNameTv.text = pdfDetails.lastName

        return createBitmap(context, binding.root, activity)
    }

    private fun createBitmap(
        context: Context,
        view: View,
        activity: Activity,
    ): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return Bitmap.createScaledBitmap(bitmap, 595, 842, true)
    }

    private fun convertBitmapToPdf(bitmap: ArrayList<Bitmap>, context: Context) :File {
        val pdfDocument = PdfDocument()

        for (i in 0.. bitmap.size){
            Log.d("TAG", " create convertBitmapToPdf: $i")
            if (i< bitmap.size){
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap[i].width, bitmap[i].height, i).create()
                val page = pdfDocument.startPage(pageInfo)
                page.canvas.drawBitmap(bitmap[i], 0F, 0F, null)
                pdfDocument.finishPage(page)
            }

        }

        val filePath = File(context.getExternalFilesDir(null), "PDF${System.currentTimeMillis()}.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        renderPdf(context, filePath)
        return filePath
    }

    fun createPdf(
        context: Context,
        pdfDetails: PdfModel,
        activity: Activity
    ):File {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pdf_layout, null)

        val newList = pdfDetails.list.chunked(15)
        val bitmapList: ArrayList<Bitmap>  = arrayListOf()
        for (i in 0.. newList.size){
            if (i<newList.size){
                val adapter = PdfTableAdapter(context,newList[i])
                Log.d("TAG", "createPdf: ${newList.size}")
                bitmapList.add(createBitmapFromView(context, view, pdfDetails, adapter, activity))
            }
        }

        //val bitmap = createBitmapFromView(context, view, pdfDetails, activity) //without recyclerView



       return convertBitmapToPdf(bitmapList, activity)
    }


    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {

        }
    }
}