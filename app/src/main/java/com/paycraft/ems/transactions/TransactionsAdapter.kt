package com.paycraft.ems.transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowAdvanceBinding
import com.paycraft.databinding.RowDateTimeStripBinding
import com.paycraft.databinding.RowExpenseBinding
import com.paycraft.databinding.RowReportBinding
import com.paycraft.databinding.RowTripBinding
import com.paycraft.ems.Record
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.reports.Report
import com.paycraft.ems.trip.create.Trip

class TransactionsAdapter(
    val mContext: Context,
    val mLayout: Int,
    val mTransactionsAdapterListener: TransactionsAdapterListener,
    var mTransactions: List<Record>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun notifyWithData(transactions: List<Record>) {
        mTransactions = transactions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mTransactions.size

    override fun getItemViewType(position: Int): Int {
        return if (mTransactions[position].id == null)
            R.layout.row_date_time_strip
        else
            mLayout
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            R.layout.row_date_time_strip -> {
                val mBinding: RowDateTimeStripBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.row_date_time_strip, viewGroup, false
                )
                viewHolder =
                    DateSectionViewHolder(mBinding)
            }

            R.layout.row_expense -> {
                val mRowExpenseBinding: RowExpenseBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.row_expense, viewGroup, false
                )
                viewHolder = TransactionViewHolder(
                    mRowExpenseBinding
                )
            }

            R.layout.row_report -> {
                val mRowReportBinding: RowReportBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.row_report, viewGroup, false
                )
                viewHolder = ReportViewHolder(
                    mRowReportBinding
                )
            }

            R.layout.row_trip -> {
                val mRowExpenseBinding: RowTripBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.row_trip, viewGroup, false
                )
                viewHolder = TripViewHolder(
                    mRowExpenseBinding
                )
            }

            R.layout.row_advance -> {
                val mRowExpenseBinding: RowAdvanceBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext),
                    R.layout.row_advance, viewGroup, false
                )
                viewHolder = AdvanceViewHolder(
                    mRowExpenseBinding
                )
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionViewHolder -> {
                holder.bind(
                    mTransactions[position] as Transaction
                )
            }

            is ReportViewHolder -> {
                holder.bind(
                    mTransactions[position] as Report
                )
            }

            is TripViewHolder -> {
                holder.bind(
                    mTransactions[position] as Trip
                )
            }

            is AdvanceViewHolder -> {
                holder.bind(mTransactions[position] as Advance)
            }

            is DateSectionViewHolder -> {
                holder.bind(mTransactions[position])
            }
        }
    }

    inner class TransactionViewHolder(val mBinding: RowExpenseBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        init {
            mBinding.root.setOnClickListener(this)
        }

        fun bind(transaction: Transaction) {
            val transactionType = when (transaction.transactionType) {
                "credit" -> {
                    R.drawable.ic_credit
                }

                "debit" -> {
                    R.drawable.ic_debit
                }

                "reversal" -> {
                    R.drawable.ic_reversal
                }

                else -> 0
            }
            mBinding.expenseTitleTv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_profile_place_holder,
                0,
                transactionType,
                0
            )

            when (transaction.transactionType) {
                "credit" -> {
                    mBinding.expenseIndicatorView.visibility = View.GONE
                    mBinding.expenseStatusTv.visibility = View.GONE
                }

                "debit" -> {
                    mBinding.expenseIndicatorView.visibility = View.VISIBLE
                    mBinding.expenseStatusTv.visibility = View.VISIBLE
                }

                "reversal" -> {
                    mBinding.expenseIndicatorView.visibility = View.GONE
                    mBinding.expenseStatusTv.visibility = View.GONE
                }

                else -> {
                    mBinding.expenseIndicatorView.visibility = View.GONE
                    mBinding.expenseStatusTv.visibility = View.GONE
                }
            }

            mBinding.expenseTitleTv.text = transaction.merchant ?: "-"
            mBinding.expenseDateAndTimeTv.text = transaction.dateAndTime ?: "--"
            mBinding.expenseStatusTv.text = transaction.displayStatus()
            mBinding.expenseAmountTv.text =
                transaction.displayAmount()
            mBinding.expenseAmountTv.setCompoundDrawablesWithIntrinsicBounds(
                if (transaction.expenseErrors?.getViolationList()
                        .isNullOrEmpty()
                ) 0 else R.drawable.ic_small_policy_violation,
                0, 0, 0
            )
            mBinding.cardTypeTv.text = transaction.walletName ?: transaction.category ?: " - "
            mBinding.travelTv.text = if (transaction.isCardTransaction())
                transaction.walletName ?: transaction.data?.txnType
                ?: transaction.paymentMethod
                ?: " - "
            else
                transaction.data?.txnType ?: transaction.paymentMethod ?: " - "

            mBinding.selectionIndicatorIv.visibility =
                if (mTransactionsAdapterListener.isSelected(transaction)) View.VISIBLE else View.GONE

            RecordActivity.statusColors(
                mContext,
                transaction.status ?: "",
                mBinding.expenseIndicatorView,
                mBinding.expenseStatusTv,
                mBinding.indicatorView
            )
        }

        override fun onClick(v: View?) {
            mTransactionsAdapterListener.onTransactionClicked(mTransactions[layoutPosition] as Transaction)
        }
    }

    inner class ReportViewHolder(val mBinding: RowReportBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        init {
            mBinding.root.setOnClickListener(this)
        }

        fun bind(report: Report) {
            mBinding.violationIv.visibility =
                if (report.expenseErrors?.getViolationList()
                        .isNullOrEmpty()
                ) View.GONE else View.VISIBLE
            mBinding.reportTitleTv.text = report.reportTitle ?: "Report"
            mBinding.reportAmountTv.text = report.displayAmount()
            mBinding.reportDateAndTimeTv.text =
                "${report.fromDate ?: "-"} - ${report.toDate ?: "-"}"
            mBinding.reportStatusTv.text = report.displayStatus()
            RecordActivity.statusColors(
                mContext,
                report.status ?: "",
                mBinding.expenseIndicatorView,
                mBinding.reportStatusTv,
                mBinding.indicatorView
            )
        }

        override fun onClick(v: View?) {
            mTransactionsAdapterListener.onReportClicked(mTransactions[layoutPosition] as Report)
        }
    }

    inner class AdvanceViewHolder(val mBinding: RowAdvanceBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        init {
            mBinding.root.setOnClickListener(this)
        }

        fun bind(advance: Advance) {
            mBinding.expenseTitleTv.text = advance.title ?: "Advance"
            mBinding.expenseDateAndTimeTv.text = advance.advanceDate ?: "--"
            mBinding.expenseStatusTv.text = advance.displayStatus()
            mBinding.expenseAmountTv.text = advance.displayAmount()
            mBinding.selectionIndicatorIv.visibility =
                if (mTransactionsAdapterListener.isSelected(advance)) View.VISIBLE else View.GONE
            RecordActivity.statusColors(
                mContext,
                advance.status ?: "",
                mBinding.expenseIndicatorView,
                mBinding.expenseStatusTv,
                mBinding.indicatorView
            )
        }

        override fun onClick(v: View?) {
            mTransactionsAdapterListener.onAdvanceClicked(mTransactions[layoutPosition] as Advance)
        }
    }

    inner class TripViewHolder(val mBinding: RowTripBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        init {
            mBinding.root.setOnClickListener(this)
        }

        fun bind(trip: Trip) {
            mBinding.tripTitleTv.text = trip.title ?: "Trip"
            mBinding.tripDateTv.text = trip.createdAt ?: "--"
            mBinding.expenseStatusTv.text = trip.displayStatus()
            mBinding.selectionIndicatorIv.visibility =
                if (mTransactionsAdapterListener.isSelected(trip)) View.VISIBLE else View.GONE
            RecordActivity.statusColors(
                mContext,
                trip.status ?: "",
                mBinding.expenseIndicatorView,
                mBinding.expenseStatusTv,
                mBinding.indicatorView
            )
        }

        override fun onClick(v: View?) {
            mTransactionsAdapterListener.onTripClicked(mTransactions[layoutPosition] as Trip)
        }
    }


    inner class DateSectionViewHolder(val mBinding: RowDateTimeStripBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(record: Record) {
        }
    }
}