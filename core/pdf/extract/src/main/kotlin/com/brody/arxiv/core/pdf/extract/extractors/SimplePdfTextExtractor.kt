package com.brody.arxiv.core.pdf.extract.extractors

import android.content.Context
import android.util.Log
import com.brody.arxiv.core.pdf.extract.PdfTextExtractor
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xml.sax.helpers.AttributesImpl
import java.io.File
import java.io.StringWriter
import javax.inject.Inject
import javax.xml.transform.OutputKeys
import javax.xml.transform.sax.SAXTransformerFactory
import javax.xml.transform.sax.TransformerHandler
import javax.xml.transform.stream.StreamResult

open class SimplePdfTextExtractor @Inject constructor(
    @ApplicationContext context: Context
) : PdfTextExtractor {
    protected lateinit var streamResult: StreamResult
    protected lateinit var handler: TransformerHandler

    init {
        if(!PDFBoxResourceLoader.isReady()) {
            PDFBoxResourceLoader.init(context)
            Log.d("HELLOEXTRACT", "HERE")
        }
        try {
            val factory = SAXTransformerFactory.newInstance() as SAXTransformerFactory
            handler = factory.newTransformerHandler().apply {
                transformer.apply {
                    setOutputProperty(OutputKeys.ENCODING, "UTF-8")
                    setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
                    setOutputProperty(OutputKeys.INDENT, "yes")
                }
            }
            // Initialization that requires catching exceptions can be placed here.
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any exceptions appropriately.
        }
    }

    override suspend fun extract(pdfFile: File): String {
        PDDocument.load(pdfFile).use { document ->
            val pdfStripper = PDFTextStripper()
            // Convert PDF text to XML format here.

            return pdfStripper.getText(document)
        }
    }
}