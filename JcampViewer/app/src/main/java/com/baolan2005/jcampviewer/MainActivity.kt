package com.baolan2005.jcampviewer

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baolan2005.jcampconverter.JcampReader
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.lang.Exception
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private lateinit var btnScan: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        btnScan = findViewById(R.id.btnScan) as Button
        btnScan.setOnClickListener(View.OnClickListener { v ->
            startScan()

        })
        chart = findViewById(R.id.chart) as LineChart

//        val input = assets.open("testdata/test_file_5.dx")


    }

    private fun startScan() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a QR Code")
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            val jcampurl = result.contents
            readJcamp(jcampurl)
            Toast.makeText(
                this@MainActivity,
                "Scanned: " + result.contents,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun readJcamp(jcampurl: String) {
//        val input = URL("https://raw.githubusercontent.com/baolanlequang/jcamp-converter-ios/master/JcampConverter/TestJcamp/testdata/test_file_5.dx").openStream()
        try {
            val input = URL(jcampurl).openStream()

            val reader = JcampReader(input)
            val jcamp = reader.jcamp
            if (jcamp != null) {
                if (jcamp.spectra.size > 0) {
                    val spectra = jcamp.spectra[0]
                    var entries: ArrayList<Entry> = arrayListOf()
                    val xValues = spectra.xValues
                    val yValues = spectra.yValues
                    xValues.forEachIndexed {index, xval ->
                        val yval = yValues[index]
                        val entry = Entry(xval.toFloat(), yval.toFloat())
                        entries.add(entry)
                    }

                    val dataSet = LineDataSet(entries, "test file")
                    dataSet.setDrawCircles(false)
                    val lineData = LineData(dataSet)
                    chart.data = lineData
                    chart.invalidate()
                }
            }
            else {
                Toast.makeText(this@MainActivity, "Cannot read the file", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }
}