package com.ktl.bondoman.ui.graph

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import com.ktl.bondoman.db.Transaction
import com.ktl.bondoman.token.TokenManager
import com.ktl.bondoman.ui.scan.Item
import com.ktl.bondoman.ui.transaction.TransactionViewModelFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch


class GraphFragment : Fragment() {
    private var totalExpense: Double = 0.0
    private var totalIncome: Double = 0.0
    private var totalPersonalExpense: Double = 0.0
    private var totalPersonalIncome: Double = 0.0
    private var maxVal: Double = 0.0
    private var minVal: Double = 0.0
    private var q1Count: Int = 0
    private var q2Count: Int = 0
    private var q3Count: Int = 0
    private var q4Count: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_graph, container, false)
        activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)


        val personalDiagram : PieChart = view.findViewById(R.id.personalChart)
        val barDiagram : BarChart = view.findViewById(R.id.barChart)
        val pieDiagram : PieChart = view.findViewById(R.id.pieChart)

        val personalButton: Button = view.findViewById(R.id.personalChartButton)
        val barButton: Button = view.findViewById(R.id.barChartButton)
        val pieButton: Button = view.findViewById(R.id.pieChartButton)

        personalDiagram.visibility = View.VISIBLE
        barDiagram.visibility = View.GONE
        pieDiagram.visibility = View.GONE

        personalButton.setOnClickListener {
            openPersonalDiagram()
        }

        barButton.setOnClickListener {
            openBarDiagram()
        }

        pieButton.setOnClickListener {
            openPieDiagram()
        }

        val transactionDao = (requireActivity().application as TransactionApplication).database.transactionDao()
        val tokenManager = TokenManager(requireContext())
        val nim = tokenManager.loadToken()?.nim

        lifecycleScope.launch {
            val transactionsArray = transactionDao.getAllList()
            for (transaction in transactionsArray){
                if (transaction.amount > maxVal){
                    maxVal = transaction.amount
                }
                if (transaction.category.equals("Expense") ){
                    if (transaction.nim == nim){
                        totalPersonalExpense += transaction.amount
                    }
                    totalExpense += transaction.amount
                } else {
                    if (transaction.nim == nim){
                        totalPersonalIncome += transaction.amount
                    }
                    totalIncome += transaction.amount
                }
            }

            val q1 = maxVal/4
            val q2 = maxVal/2
            val q3 = q1+q2

            for (transaction in transactionsArray){
                if (transaction.amount > q3){
                    q4Count +=1
                } else if (transaction.amount > q2){
                    q3Count +=1
                } else if (transaction.amount > q1){
                    q2Count +=1
                } else {
                    q1Count +=1
                }
            }

            // Personal Data
            val transactionsPersonal : ArrayList<PieEntry> = ArrayList()
            transactionsPersonal.add(PieEntry(totalPersonalExpense.toFloat(),"Income"))
            transactionsPersonal.add(PieEntry(totalPersonalIncome.toFloat(),"Expense"))
            val personalDataSet = PieDataSet(transactionsPersonal, "Sum Transactions Price Based on Category")
            personalDataSet.setDrawIcons(false)
            personalDataSet.sliceSpace = 3f
            personalDataSet.iconsOffset = MPPointF(0F, 40F)
            personalDataSet.selectionShift = 5f
            personalDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            val personalData = PieData(personalDataSet)
            personalDataSet.setValueTextSize(20f)
            personalDataSet.setValueTextColor(Color.WHITE)
            personalDiagram.data = personalData
            personalDiagram.highlightValues(null)
            personalDiagram.invalidate()
            personalDiagram.animateXY(750,750)

            // Bar Data
            val transactionsBar : ArrayList<BarEntry> = ArrayList()
            transactionsBar.add(BarEntry(1f,q1Count.toFloat()))
            transactionsBar.add(BarEntry(2f,q2Count.toFloat()))
            transactionsBar.add(BarEntry(3f ,q3Count.toFloat()))
            transactionsBar.add(BarEntry(4f ,q4Count.toFloat()))
            val barDataSet = BarDataSet(transactionsBar, "Count Transactions Price Grouping Based on each Quarter")
//            barDataSet.setDrawIcons(false)
            barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
            val barData = BarData(barDataSet)
            barData.setBarWidth(0.9f);
            barDataSet.setValueTextSize(20f)
            barDataSet.setValueTextColor(Color.BLACK)
            barDiagram.setFitBars(true)
            barDiagram.data = barData
            barDiagram.highlightValues(null)
            barDiagram.invalidate()
            barDiagram.animateY(750)

            // Pie Data
            val transactionsPie : ArrayList<PieEntry> = ArrayList()
            transactionsPie.add(PieEntry(totalExpense.toFloat(),"Income"))
            transactionsPie.add(PieEntry(totalIncome.toFloat(),"Expense"))
            val pieDataSet = PieDataSet(transactionsPie, "Sum Transactions Price Based on Category")
            pieDataSet.setDrawIcons(false)
            pieDataSet.sliceSpace = 3f
            pieDataSet.iconsOffset = MPPointF(0F, 40F)
            pieDataSet.selectionShift = 5f
            pieDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            val pieData = PieData(pieDataSet)
            pieDataSet.setValueTextSize(20f)
            pieDataSet.setValueTextColor(Color.WHITE)
            pieDiagram.data = pieData
            pieDiagram.highlightValues(null)
            pieDiagram.invalidate()
            pieDiagram.animateXY(750,750)

        }


        return view
    }

    private fun openPersonalDiagram(){
        val personalDiagram : PieChart = view!!.findViewById(R.id.personalChart)
        val barDiagram : BarChart = view!!.findViewById(R.id.barChart)
        val pieDiagram : PieChart = view!!.findViewById(R.id.pieChart)
        personalDiagram.visibility = View.VISIBLE
        barDiagram.visibility = View.GONE
        pieDiagram.visibility = View.GONE
    }

    private fun openBarDiagram(){
        val personalDiagram : PieChart = view!!.findViewById(R.id.personalChart)
        val barDiagram : BarChart = view!!.findViewById(R.id.barChart)
        val pieDiagram : PieChart = view!!.findViewById(R.id.pieChart)
        personalDiagram.visibility = View.GONE
        barDiagram.visibility = View.VISIBLE
        pieDiagram.visibility = View.GONE
    }

    private fun openPieDiagram(){
        val personalDiagram : PieChart = view!!.findViewById(R.id.personalChart)
        val barDiagram : BarChart = view!!.findViewById(R.id.barChart)
        val pieDiagram : PieChart = view!!.findViewById(R.id.pieChart)
        personalDiagram.visibility = View.GONE
        barDiagram.visibility = View.GONE
        pieDiagram.visibility = View.VISIBLE
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title = "Graph"
    }
}