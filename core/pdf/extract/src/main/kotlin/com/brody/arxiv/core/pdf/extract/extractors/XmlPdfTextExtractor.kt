package com.brody.arxiv.core.pdf.extract.extractors

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xml.sax.helpers.AttributesImpl
import java.io.File
import java.io.StringWriter
import javax.inject.Inject
import javax.xml.transform.stream.StreamResult

class XmlPdfTextExtractor @Inject constructor(@ApplicationContext context: Context) :
    SimplePdfTextExtractor(context) {
    private val atts = AttributesImpl()

    override suspend fun extract(pdfFile: File): String {
        val text = super.extract(pdfFile)
        return convertTextToXML(text)
    }

    private fun convertTextToXML(text: String): String {
        val writer = StringWriter()
        streamResult = StreamResult(writer)

        handler.setResult(streamResult)
        handler.startDocument()
        handler.startElement("", "", "data", atts)

        val elements = text.split("\\|")
        elements.forEach { element ->
            atts.clear()
            handler.startElement("", "", "Message", atts)
            handler.characters(element.toCharArray(), 0, element.length)
            handler.endElement("", "", "Message")
        }

        handler.endElement("", "", "data")
        handler.endDocument()

        return writer.toString()
    }
}