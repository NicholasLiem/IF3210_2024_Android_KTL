package com.ktl.bondoman.ui.graph

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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.ktl.bondoman.R
import com.ktl.bondoman.TransactionApplication
import com.ktl.bondoman.db.Transaction
import com.ktl.bondoman.ui.scan.Item
import com.ktl.bondoman.ui.transaction.TransactionViewModelFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch


class GraphFragment : Fragment() {
    private val arrTransaction = mutableListOf<Transaction>()
//    private val graphViewModel: GraphViewModel by viewModels {
//        TransactionViewModelFactory((requireActivity().application as TransactionApplication).repository)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_graph, container, false)

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


        val transactions : ArrayList<PieEntry> = ArrayList()
        transactions.add(PieEntry(512f,"Income"))
        transactions.add(PieEntry(342f,"Expense"))

        val dataSet = PieDataSet(transactions, "Transactions Data")

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(20f)
        data.setValueTextColor(Color.WHITE)
        personalDiagram.data = data
        personalDiagram.highlightValues(null)
        personalDiagram.invalidate()
        personalDiagram.animateXY(750,750)


        return view
    }

    fun openPersonalDiagram(){
        val personalDiagram : PieChart = view!!.findViewById(R.id.personalChart)
        val barDiagram : BarChart = view!!.findViewById(R.id.barChart)
        val pieDiagram : PieChart = view!!.findViewById(R.id.pieChart)
        personalDiagram.visibility = View.VISIBLE
        barDiagram.visibility = View.GONE
        pieDiagram.visibility = View.GONE
    }

    fun openBarDiagram(){
        val personalDiagram : PieChart = view!!.findViewById(R.id.personalChart)
        val barDiagram : BarChart = view!!.findViewById(R.id.barChart)
        val pieDiagram : PieChart = view!!.findViewById(R.id.pieChart)
        personalDiagram.visibility = View.GONE
        barDiagram.visibility = View.VISIBLE
        pieDiagram.visibility = View.GONE
    }

    fun openPieDiagram(){
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

//        graphViewModel.allTransactions.observe(this) { transactions ->
//            transactions.let {
//                for (transaction in it){
//                    arrTransaction.add(transaction)
//                }
//            }
//        }
    }
}