package com.antorparvez.pdfgenerator

data class PdfModel(
    val firstName:String,
    val lastName:String,
    val list: List<ListModel>
)
