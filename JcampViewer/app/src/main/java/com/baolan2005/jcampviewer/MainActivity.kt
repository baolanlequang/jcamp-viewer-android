package com.baolan2005.jcampviewer

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.baolan2005.jcampconverter.JcampReader
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        chart = findViewById(R.id.chart) as LineChart

//        val input = assets.open("testdata/test_file_5.dx")

        val input = URL("https://raw.githubusercontent.com/baolanlequang/jcamp-converter-ios/master/JcampConverter/TestJcamp/testdata/test_file_5.dx").openStream()

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
            Log.d("baolanlequang", "null")
        }
    }
}