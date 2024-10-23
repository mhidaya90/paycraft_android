package com.paycraft.network

import com.paycraft.base.BaseResponse
import com.paycraft.card.add.otp.AddCardSendOtpRequest
import com.paycraft.card.add.otp.AddCardSendOtpResponse
import com.paycraft.card.add.verify.AddCardActivateRequest
import com.paycraft.card.add.verify.AddCardActivateResponse
import com.paycraft.card.add.verify.AddCardRegisterRequest
import com.paycraft.card.add.verify.AddCardRegisterResponse
import com.paycraft.card.add.verify.AddCardVerifyOtpRequest
import com.paycraft.card.add.verify.AddCardVerifyOtpResponse
import com.paycraft.card.cards.CardsResponse
import com.paycraft.card.cards.balances.TopUpRequest
import com.paycraft.card.cards.balances.TopUpResponse
import com.paycraft.card.cards.settings.BlockCardReasonsRequest
import com.paycraft.card.cards.settings.BlockCardRequest
import com.paycraft.card.cards.settings.BlockCardResponse
import com.paycraft.card.cards.settings.BlockReasonsCardResponse
import com.paycraft.card.cards.settings.SetPinResponse
import com.paycraft.card.cards.settings.SetUpRequest
import com.paycraft.card.cards.virtual.CardRequest
import com.paycraft.card.cards.virtual.CardResponse
import com.paycraft.card.cart_number.UpdateCardMobileNumberResponse
import com.paycraft.card.cart_number.UpdateCardMobileRequest
import com.paycraft.ems.advance.create.CreateAdvanceResponse
import com.paycraft.ems.advance.detail.AdvanceResponse
import com.paycraft.ems.advance.detail.LinkAdvanceToRequest
import com.paycraft.ems.advance.detail.LinkAdvancesToRequest
import com.paycraft.ems.advance.detail.LinkAdvancesToResponse
import com.paycraft.ems.advance.detail.RecallAdvanceRequest
import com.paycraft.ems.advance.detail.SubmitAdvanceRequest
import com.paycraft.ems.advance.detail.SubmitAdvanceResponse
import com.paycraft.ems.advance.list.AdvancesResponse
import com.paycraft.ems.comments.CommentsResponse
import com.paycraft.ems.comments.CreateAdvanceCommentRequest
import com.paycraft.ems.comments.CreateCommentResponse
import com.paycraft.ems.comments.CreateReportCommentRequest
import com.paycraft.ems.comments.CreateTransactionCommentRequest
import com.paycraft.ems.history.HistoryResponse
import com.paycraft.ems.options_picker.MerchantsResponse
import com.paycraft.ems.report_expenses.TransactionResponse
import com.paycraft.ems.reports.ReportsResponse
import com.paycraft.ems.reports.SubmitReportRequest
import com.paycraft.ems.reports.SubmitReportResponse
import com.paycraft.ems.reports.create.CreateReportResponse
import com.paycraft.ems.reports.detail.RecallReportRequest
import com.paycraft.ems.reports.detail.ReportResponse
import com.paycraft.ems.reports.detail.ValidateTransactionRequest
import com.paycraft.ems.reports.detail.ValidateTransactionResponse
import com.paycraft.ems.transaction_picker.LinkTransactionsToRequest
import com.paycraft.ems.transaction_picker.LinkTransactionsToResponse
import com.paycraft.ems.transactions.CategoriesResponse
import com.paycraft.ems.transactions.CreateTransactionResponse
import com.paycraft.ems.transactions.FiltersResponse
import com.paycraft.ems.transactions.TransactionFieldsResponse
import com.paycraft.ems.transactions.TransactionRequest
import com.paycraft.ems.transactions.TransactionsResponse
import com.paycraft.ems.transactions.TripRequest
import com.paycraft.ems.trip.create.CreateTripResponse
import com.paycraft.ems.trip.details.CreateTripCommentRequest
import com.paycraft.ems.trip.details.LinkTripToRequest
import com.paycraft.ems.trip.details.LinkTripsToRequest
import com.paycraft.ems.trip.details.LinkTripsToResponse
import com.paycraft.ems.trip.details.RecallTripRequest
import com.paycraft.ems.trip.details.SubmitTripRequest
import com.paycraft.ems.trip.details.SubmitTripResponse
import com.paycraft.ems.trip.details.TripResponse
import com.paycraft.ems.trip.list.TripsResponse
import com.paycraft.home.ProfileResponse
import com.paycraft.notifications.NotificationResponse
import com.paycraft.user.change_passsword.ChangePasswordRequest
import com.paycraft.user.forgot.ForgotPasswordRequest
import com.paycraft.user.login.LoginInRequest
import com.paycraft.user.login.LoginInResponse
import com.paycraft.user.profile.DeleteAccountRequest
import com.paycraft.user.signup.SignUpRequest
import com.paycraft.user.signup.SignupResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class ApiRepo @Inject constructor(val api: Api) : BaseRepo() {
    suspend fun login(email: String, password: String): ApiResponse<LoginInResponse> {
        return executeApi {
            api.logInAsync(
                LoginInRequest(email, password)
            ).await()
        }
    }

    suspend fun logout(): ApiResponse<BaseResponse> {
        return executeApi {
            api.logOutAsync().await()
        }
    }

    suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ): ApiResponse<SignupResponse> {
        return executeApi {
            api.signUpAsync(SignUpRequest(name, email, phone, password)).await()
        }
    }

    suspend fun deleteAccount(request: DeleteAccountRequest): ApiResponse<ProfileResponse> {
        return executeApi {
            api.deleteAccountAsync(request).await()
        }
    }

    suspend fun profile(): ApiResponse<ProfileResponse> {
        return executeApi {
            api.profileAsync(true).await()
        }
    }

    suspend fun changePasswordAsync(
        currentPassword: String,
        password: String,
        passwordConfirmation: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.changePasswordAsync(
                ChangePasswordRequest(
                    currentPassword,
                    password,
                    passwordConfirmation
                )
            ).await()
        }
    }

    suspend fun forgotPasswordAsync(email: String): ApiResponse<BaseResponse> {
        return executeApi {
            api.forgotPasswordAsync(ForgotPasswordRequest(email)).await()
        }
    }

    suspend fun categories(): ApiResponse<CategoriesResponse> {
        return executeApi {
            api.categoriesAsync().await()
        }
    }

    suspend fun getTransactionFields(
        id: String = "",
        categoryId: String
    ): ApiResponse<TransactionFieldsResponse> {
        val map = HashMap<String, String>()
        if (id.isNotEmpty())
            map["transaction_id"] = id
        if (categoryId.isNotEmpty())
            map["category_id"] = categoryId
        return executeApi {
            api.getTransactionFieldsAsync(map).await()
        }
    }

    suspend fun validateTransaction(
        request: ValidateTransactionRequest
    ): ApiResponse<ValidateTransactionResponse> {
        return executeApi {
            api.validateTransactionAsync(request).await()
        }
    }

    suspend fun getTransactionFilters(
        type: String,
        cardId: String = ""
    ): ApiResponse<FiltersResponse> {
        val map = HashMap<String, String>()
        if (cardId.isNotEmpty()) {
            map["card_id"] = cardId
        }
        return executeApi {
            api.getTransactionFiltersAsync(type, map).await()
        }
    }

    suspend fun createTransaction(
        parts: List<MultipartBody.Part>,
        files: List<MultipartBody.Part>
    ): ApiResponse<CreateTransactionResponse> {
        return executeApi {
            api.createTransactionAsync(parts, files, true).await()
        }
    }

    suspend fun updateTransaction(
        id: String,
        parts: List<MultipartBody.Part>,
        files: List<MultipartBody.Part>,
        deletedFiles: List<MultipartBody.Part>
    ): ApiResponse<CreateTransactionResponse> {
        return executeApi {
            api.updateTransactionAsync(id, parts, files, deletedFiles, base64 = true).await()
        }
    }

    suspend fun deleteTransaction(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.deleteTransactionAsync(id).await()
        }
    }

    suspend fun getTransactions(
        map: Map<String, String> = emptyMap(),
        isCardTransactions: Boolean = false
    ): ApiResponse<TransactionsResponse> {
        return executeApi {
            if (isCardTransactions)
                api.getCardTransactionsAsync(map.filter { it.key.isNotEmpty() && it.value.isNotEmpty() })
                    .await()
            else
                api.getTransactionsAsync(map.filter { it.key.isNotEmpty() && it.value.isNotEmpty() })
                    .await()
        }
    }

    suspend fun getTransaction(id: String): ApiResponse<TransactionResponse> {
        return executeApi {
            api.getTransactionAsync(id).await()
        }
    }

    suspend fun getTransactionAttachments(id: String): ApiResponse<TransactionResponse> {
        return executeApi {
            api.getTransactionAttachmentsAsync(id).await()
        }
    }

    suspend fun getAdvance(id: String): ApiResponse<AdvanceResponse> {
        return executeApi {
            api.getAdvanceAsync(id).await()
        }
    }

    suspend fun getReportsAsync(
        map: Map<String, String> = emptyMap()
    ): ApiResponse<ReportsResponse> {
        return executeApi {
            api.getReportsAsync(map).await()
        }
    }

    suspend fun getReport(id: String): ApiResponse<ReportResponse> {
        return executeApi {
            api.getReportAsync(id).await()
        }
    }

    suspend fun getReportFields(id: String = ""): ApiResponse<TransactionFieldsResponse> {
        return executeApi {
            api.getReportFieldsAsync(id).await()
        }
    }

    suspend fun getAdvanceFields(id: String = ""): ApiResponse<TransactionFieldsResponse> {
        return executeApi {
            api.getAdvanceFieldsAsync(id).await()
        }
    }

    suspend fun linkTransactionToReport(request: LinkTransactionsToRequest)
            : ApiResponse<LinkTransactionsToResponse> {
        return executeApi {
            api.linkTransactionToReportAsync(request).await()
        }
    }

    suspend fun linkAdvanceToReport(request: LinkAdvancesToRequest)
            : ApiResponse<LinkAdvancesToResponse> {
        return executeApi {
            api.linkAdvanceToReportAsync(request).await()
        }
    }

    suspend fun unlinkTransactionsAsync(rId: String, tId: String)
            : ApiResponse<BaseResponse> {
        return executeApi {
            api.unlinkTransactionsAsync(false, LinkTransactionsToRequest(rId, arrayListOf(tId)))
                .await()
        }
    }

    suspend fun unlinkAdvanceAsync(rId: String, tId: String)
            : ApiResponse<BaseResponse> {
        return executeApi {
            api.unlinkAdvanceAsync(false, LinkAdvanceToRequest(rId, arrayListOf(tId)))
                .await()
        }
    }

    suspend fun unlinkTripAsync(rId: String, tId: String)
            : ApiResponse<BaseResponse> {
        return executeApi {
            api.unlinkTripAsync(false, LinkTripToRequest(rId, arrayListOf(tId)))
                .await()
        }
    }

    suspend fun createReport(
        req: TransactionRequest
    ): ApiResponse<CreateReportResponse> {
        return executeApi {
            api.createReportAsync(req).await()
        }
    }

    suspend fun submitReport(
        id: String
    ): ApiResponse<SubmitReportResponse> {
        return executeApi {
            api.submitReportAsync(SubmitReportRequest(id)).await()
        }
    }

    suspend fun submitAdvance(
        id: String
    ): ApiResponse<SubmitAdvanceResponse> {
        return executeApi {
            api.submitAdvanceAsync(SubmitAdvanceRequest(id)).await()
        }
    }

    suspend fun updateReport(
        id: String,
        req: TransactionRequest
    ): ApiResponse<CreateReportResponse> {
        return executeApi {
            api.updateReportAsync(id, req).await()
        }
    }

    suspend fun recallReport(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.recallReportAsync(RecallReportRequest(id)).await()
        }
    }

    suspend fun recallAdvance(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.recallAdvanceAsync(RecallAdvanceRequest(id)).await()
        }
    }

    suspend fun recallTrip(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.recallTripAsync(RecallTripRequest(id)).await()
        }
    }

    suspend fun closeTrip(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.closeTripAsync(RecallTripRequest(id)).await()
        }
    }

    suspend fun cancelTrip(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.cancelTripAsync(RecallTripRequest(id)).await()
        }
    }

    suspend fun createAdvance(
        req: TransactionRequest
    ): ApiResponse<CreateAdvanceResponse> {
        return executeApi {
            api.createAdvanceAsync(req).await()
        }
    }

    suspend fun updateAdvance(
        id: String,
        req: TransactionRequest
    ): ApiResponse<CreateAdvanceResponse> {
        return executeApi {
            api.updateAdvanceAsync(id, req).await()
        }
    }

    suspend fun deleteReport(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.deleteReportAsync(id).await()
        }
    }

    suspend fun reportComments(
        id: String
    ): ApiResponse<CommentsResponse> {
        return executeApi {
            api.reportCommentsAsync(id).await()
        }
    }

    suspend fun advanceComments(
        id: String
    ): ApiResponse<CommentsResponse> {
        return executeApi {
            api.advanceCommentsAsync(id).await()
        }
    }

    suspend fun transactionComments(
        id: String,
        page: Int
    ): ApiResponse<CommentsResponse> {
        return executeApi {
            api.transactionCommentsAsync(id, page).await()
        }
    }

    suspend fun reportHistory(
        id: String
    ): ApiResponse<HistoryResponse> {
        return executeApi {
            api.reportHistoryAsync(id).await()
        }
    }

    suspend fun advanceHistory(
        id: String
    ): ApiResponse<HistoryResponse> {
        return executeApi {
            api.advanceHistoryAsync(id).await()
        }
    }

    suspend fun transactionHistory(
        id: String,
        page: Int
    ): ApiResponse<HistoryResponse> {
        return executeApi {
            api.transactionHistoryAsync(id, page).await()
        }
    }

    suspend fun createReportComment(
        id: String, comment: String
    ): ApiResponse<CreateCommentResponse> {
        return executeApi {
            api.createReportCommentAsync(CreateReportCommentRequest(id, comment)).await()
        }
    }

    suspend fun createTransactionComment(
        id: String, comment: String
    ): ApiResponse<CreateCommentResponse> {
        return executeApi {
            api.createTransactionCommentsAsync(CreateTransactionCommentRequest(id, comment)).await()
        }
    }

    suspend fun createAdvanceComment(
        id: String, comment: String
    ): ApiResponse<CreateCommentResponse> {
        return executeApi {
            api.createAdvanceCommentsAsync(CreateAdvanceCommentRequest(id, comment)).await()
        }
    }

    suspend fun getAdvances(
        map: Map<String, String> = emptyMap()
    ): ApiResponse<AdvancesResponse> {
        return executeApi {
            api.getAdvancesAsync(map).await()
        }
    }

    suspend fun deleteAdvance(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.deleteAdvanceAsync(id).await()
        }
    }

    suspend fun getTripFields(id: String = ""): ApiResponse<TransactionFieldsResponse> {
        return executeApi {
            api.getTripsFieldAsync(id).await()
        }
    }

    suspend fun updateTrip(
        id: String,
        req: TripRequest
    ): ApiResponse<CreateTripResponse> {
        return executeApi {
            api.updateTripAsync(id, req).await()
        }
    }

    suspend fun createTrip(
        req: TripRequest
    ): ApiResponse<CreateTripResponse> {
        return executeApi {
            api.createTripAsync(req).await()
        }
    }

    suspend fun getTrips(
        map: Map<String, String> = emptyMap()
    ): ApiResponse<TripsResponse> {
        return executeApi {
            api.getTripsAsync(map).await()
        }
    }

    suspend fun getTrip(id: String): ApiResponse<TripResponse> {
        return executeApi {
            api.getTripAsync(id).await()
        }
    }

    suspend fun submitTrip(
        id: String
    ): ApiResponse<SubmitTripResponse> {
        return executeApi {
            api.submitTripAsync(SubmitTripRequest(id)).await()
        }
    }

    suspend fun linkTripsToReport(request: LinkTripsToRequest)
            : ApiResponse<LinkTripsToResponse> {
        return executeApi {
            api.linkAdvanceToReportAsync(request).await()
        }
    }

    suspend fun deleteTrip(
        id: String
    ): ApiResponse<BaseResponse> {
        return executeApi {
            api.deleteTripAsync(id).await()
        }
    }

    suspend fun createTripComment(
        id: String, comment: String
    ): ApiResponse<CreateCommentResponse> {
        return executeApi {
            api.createTripCommentsAsync(CreateTripCommentRequest(id, comment)).await()
        }
    }

    suspend fun tripComments(
        id: String
    ): ApiResponse<CommentsResponse> {
        return executeApi {
            api.tripCommentsAsync(id).await()
        }
    }

    suspend fun tripHistory(
        id: String
    ): ApiResponse<HistoryResponse> {
        return executeApi {
            api.tripHistoryAsync(id).await()
        }
    }

    suspend fun card(
    ): ApiResponse<CardResponse> {
        return executeApi {
            api.card(CardRequest()).await()
        }
    }

    suspend fun cards(): ApiResponse<CardsResponse> {
        return executeApi {
            api.cardsAsync().await()
        }
    }

    suspend fun cardTopUp(topUpRequest: TopUpRequest): ApiResponse<TopUpResponse> {
        return executeApi {
            api.cardTopUpAsync(topUpRequest).await()
        }
    }

    suspend fun blockCard(id: String, reason: String): ApiResponse<BlockCardResponse> {
        return executeApi {
            api.blockCardAsync(BlockCardRequest(id, reason)).await()
        }
    }

    suspend fun blockCardReasons(id: String): ApiResponse<BlockReasonsCardResponse> {
        return executeApi {
            api.blockCardReasonsAsync(BlockCardReasonsRequest(id)).await()
        }
    }

    suspend fun updateCardMobileNumber(
        id: String,
        mobile: String
    ): ApiResponse<UpdateCardMobileNumberResponse> {
        return executeApi {
            api.updateCardMobileNumberAsync(UpdateCardMobileRequest(id, mobile)).await()
        }
    }

    suspend fun setPin(id: String): ApiResponse<SetPinResponse> {
        return executeApi {
            api.setPinAsync(SetUpRequest(id)).await()
        }
    }

    suspend fun addCardSendOtp(
        id: String,
        actionType: String
    ): ApiResponse<AddCardSendOtpResponse> {
        return executeApi {
            api.addCardSendOtpAsync(AddCardSendOtpRequest(id, actionType)).await()
        }
    }

    suspend fun addCardVerifyOtp(
        id: String,
        otp: String,
        trxId: String
    ): ApiResponse<AddCardVerifyOtpResponse> {
        return executeApi {
            api.addCardVerifyOtpAsync(AddCardVerifyOtpRequest(id, otp, trxId)).await()
        }
    }

    suspend fun cardRegister(id: String): ApiResponse<AddCardRegisterResponse> {
        return executeApi {
            api.addCardRegisterAsync(AddCardRegisterRequest(id)).await()
        }
    }

    suspend fun addCardActivate(id: String): ApiResponse<AddCardActivateResponse> {
        return executeApi {
            api.addCardActivateAsync(AddCardActivateRequest(id)).await()
        }
    }

    suspend fun getMerchants(searchKey: String = ""): ApiResponse<MerchantsResponse> {
        val map = HashMap<String, String>()
        if (searchKey.isNotEmpty()) {
            map["search_key"] = searchKey
        }
        return executeApi {
            api.getMerchantsAsync(map).await()
        }
    }

    suspend fun createMerchant(merchantName: String): ApiResponse<BaseResponse> {
        return executeApi {
            api.createMerchantAsync(merchantName).await()
        }
    }

    suspend fun notifications(page: Int = 1): ApiResponse<NotificationResponse> {
        return executeApi {
            api.notificationsAsync(page, true).await()
        }
    }

    suspend fun markNotificationAsSeen(id: String): ApiResponse<BaseResponse> {
        return executeApi {
            api.markNotificationAsSeenAsync(id).await()
        }
    }
}