package com.paycraft.network

import com.paycraft.base.BaseResponse
import com.paycraft.card.add.otp.AddCardSendOtpRequest
import com.paycraft.card.add.otp.AddCardSendOtpResponse
import com.paycraft.card.add.verify.*
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
import com.paycraft.ems.advance.detail.*
import com.paycraft.ems.advance.list.AdvancesResponse
import com.paycraft.ems.comments.*
import com.paycraft.ems.history.HistoryResponse
import com.paycraft.ems.options_picker.MerchantsResponse
import com.paycraft.ems.report_expenses.TransactionResponse
import com.paycraft.ems.reports.ReportsResponse
import com.paycraft.ems.reports.SubmitReportRequest
import com.paycraft.ems.reports.SubmitReportResponse
import com.paycraft.ems.reports.create.CreateReportResponse
import com.paycraft.ems.reports.detail.*
import com.paycraft.ems.transaction_picker.LinkTransactionsToRequest
import com.paycraft.ems.transaction_picker.LinkTransactionsToResponse
import com.paycraft.ems.transactions.*
import com.paycraft.ems.trip.create.CreateTripResponse
import com.paycraft.ems.trip.details.*
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
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @Headers("device_type: android")
    @POST("auth/login")
    fun logInAsync(
        @Body request: LoginInRequest
    ): Deferred<Response<LoginInResponse>>

    @POST("auth/logout")
    fun logOutAsync(): Deferred<Response<BaseResponse>>

    @Headers("device_type: android")
    @POST("auth/signup")
    fun signUpAsync(
        @Body request: SignUpRequest
    ): Deferred<Response<SignupResponse>>

    @POST("admin/employees/update")
    fun deleteAccountAsync(
        @Body request: DeleteAccountRequest
    ): Deferred<Response<ProfileResponse>>

    @GET("user/profile")
    fun profileAsync(
        @Query("is_employee") isEmployee: Boolean,
    ): Deferred<Response<ProfileResponse>>

    @POST("user/change_password")
    fun changePasswordAsync(@Body request: ChangePasswordRequest): Deferred<Response<BaseResponse>>

    @POST("user/forgot_password")
    fun forgotPasswordAsync(@Body request: ForgotPasswordRequest): Deferred<Response<BaseResponse>>

    @GET("categories")
    fun categoriesAsync(): Deferred<Response<CategoriesResponse>>

    @GET("transactions/transaction_fields")
    fun getTransactionFieldsAsync(@QueryMap id: Map<String, String>): Deferred<Response<TransactionFieldsResponse>>

    @POST("transactions/validate_expense_policy")
    fun validateTransactionAsync(@Body request: ValidateTransactionRequest)
            : Deferred<Response<ValidateTransactionResponse>>

    @GET("{type}/filters")
    fun getTransactionFiltersAsync(
        @Path("type") type: String,
        @QueryMap query: Map<String, String>
    ): Deferred<Response<FiltersResponse>>

    @Multipart
    @POST("transactions")
    fun createTransactionAsync(
        @Part parts: List<MultipartBody.Part>,
        @Part files: List<MultipartBody.Part>,
        @Query("base64") base64: Boolean
    ): Deferred<Response<CreateTransactionResponse>>

    @Multipart
    @POST("transactions/update")
    fun updateTransactionAsync(
        @Query("id") id: String,
        @Part parts: List<MultipartBody.Part>,
        @Part files: List<MultipartBody.Part>,
        @Part deletedFiles: List<MultipartBody.Part>,
        @Query("base64") base64: Boolean
    ): Deferred<Response<CreateTransactionResponse>>

    @FormUrlEncoded
    @POST("transactions/delete")
    fun deleteTransactionAsync(
        @Field("id") id: String
    ): Deferred<Response<BaseResponse>>

    @GET("transactions")
    fun getTransactionsAsync(
        @QueryMap map: Map<String, String>
    ): Deferred<Response<TransactionsResponse>>

    @GET("admin/transactions/list")
    fun getCardTransactionsAsync(
        @QueryMap map: Map<String, String>
    ): Deferred<Response<TransactionsResponse>>

    @GET("transactions/show")
    fun getTransactionAsync(@Query("id") id: String): Deferred<Response<TransactionResponse>>

    @GET("transactions/show_attachments")
    fun getTransactionAttachmentsAsync(@Query("id") id: String): Deferred<Response<TransactionResponse>>

    @GET("advances/show")
    fun getAdvanceAsync(@Query("id") id: String): Deferred<Response<AdvanceResponse>>

    @GET("reports")
    fun getReportsAsync(@QueryMap map: Map<String, String>): Deferred<Response<ReportsResponse>>

    @GET("reports/show")
    fun getReportAsync(@Query("id") id: String): Deferred<Response<ReportResponse>>

    @POST("reports")
    fun createReportAsync(
        @Body request: TransactionRequest
    ): Deferred<Response<CreateReportResponse>>

    @POST("reports/submit")
    fun submitReportAsync(
        @Body request: SubmitReportRequest
    ): Deferred<Response<SubmitReportResponse>>

    @POST("reports/update")
    fun updateReportAsync(
        @Query("id") id: String,
        @Body request: TransactionRequest
    ): Deferred<Response<CreateReportResponse>>

    @POST("reports/recall")
    fun recallReportAsync(
        @Body request: RecallReportRequest
    ): Deferred<Response<BaseResponse>>

    @POST("advances/recall")
    fun recallAdvanceAsync(
        @Body request: RecallAdvanceRequest
    ): Deferred<Response<BaseResponse>>

    @POST("trips/close")
    fun closeTripAsync(
        @Body request: RecallTripRequest
    ): Deferred<Response<BaseResponse>>

    @POST("trips/cancel")
    fun cancelTripAsync(
        @Body request: RecallTripRequest
    ): Deferred<Response<BaseResponse>>

    @POST("trips/recall")
    fun recallTripAsync(
        @Body request: RecallTripRequest
    ): Deferred<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("reports/delete")
    fun deleteReportAsync(
        @Field("id") id: String
    ): Deferred<Response<BaseResponse>>

    @GET("reports/report_fields")
    fun getReportFieldsAsync(@Query("report_id") id: String): Deferred<Response<TransactionFieldsResponse>>

    @POST("reports/link_transactions")
    fun linkTransactionToReportAsync(@Body request: LinkTransactionsToRequest)
            : Deferred<Response<LinkTransactionsToResponse>>

    @GET("reports/comments")
    fun reportCommentsAsync(@Query("report_id") reportId: String)
            : Deferred<Response<CommentsResponse>>

    @POST("reports/comments")
    fun createReportCommentAsync(@Body request: CreateReportCommentRequest)
            : Deferred<Response<CreateCommentResponse>>

    @POST("transactions/unlink")
    fun unlinkTransactionsAsync(
        @Query("added_to_report") addedToReport: Boolean,
        @Body req: LinkTransactionsToRequest
    ): Deferred<Response<BaseResponse>>

    @POST("advances/unlink")
    fun unlinkAdvanceAsync(
        @Query("added_to_report") addedToReport: Boolean,
        @Body req: LinkAdvanceToRequest
    ): Deferred<Response<BaseResponse>>

    @POST("trips/unlink")
    fun unlinkTripAsync(
        @Query("added_to_report") addedToReport: Boolean,
        @Body req: LinkTripToRequest
    ): Deferred<Response<BaseResponse>>

    @GET("advances/comments")
    fun advanceCommentsAsync(@Query("advance_id") reportId: String)
            : Deferred<Response<CommentsResponse>>

    @GET("transactions/comments")
    fun transactionCommentsAsync(
        @Query("transaction_id") reportId: String,
        @Query("page") page: Int,
    ): Deferred<Response<CommentsResponse>>

    @GET("reports/history")
    fun reportHistoryAsync(@Query("report_id") reportId: String)
            : Deferred<Response<HistoryResponse>>

    @GET("advances/history")
    fun advanceHistoryAsync(@Query("advance_id") reportId: String)
            : Deferred<Response<HistoryResponse>>

    @GET("transactions/history")
    fun transactionHistoryAsync(
        @Query("transaction_id") reportId: String,
        @Query("page") page: Int,
    ): Deferred<Response<HistoryResponse>>


    @POST("transactions/comments")
    fun createTransactionCommentsAsync(@Body request: CreateTransactionCommentRequest)
            : Deferred<Response<CreateCommentResponse>>

    @POST("advances/comments")
    fun createAdvanceCommentsAsync(@Body request: CreateAdvanceCommentRequest)
            : Deferred<Response<CreateCommentResponse>>

    @POST("advances/submit")
    fun submitAdvanceAsync(
        @Body request: SubmitAdvanceRequest
    ): Deferred<Response<SubmitAdvanceResponse>>

    @POST("advances")
    fun createAdvanceAsync(
        @Body request: TransactionRequest
    ): Deferred<Response<CreateAdvanceResponse>>

    @POST("advances/update")
    fun updateAdvanceAsync(
        @Query("id") id: String,
        @Body request: TransactionRequest
    ): Deferred<Response<CreateAdvanceResponse>>

    @GET("advances/advance_fields")
    fun getAdvanceFieldsAsync(@Query("advance_id") id: String): Deferred<Response<TransactionFieldsResponse>>

    @GET("advances")
    fun getAdvancesAsync(
        @QueryMap map: Map<String, String>,
    ): Deferred<Response<AdvancesResponse>>

    @POST("reports/link_advances")
    fun linkAdvanceToReportAsync(@Body request: LinkAdvancesToRequest)
            : Deferred<Response<LinkAdvancesToResponse>>

    @FormUrlEncoded
    @POST("advances/delete")
    fun deleteAdvanceAsync(
        @Field("id") id: String
    ): Deferred<Response<BaseResponse>>

    @GET("trips/trip_fields")
    fun getTripsFieldAsync(@Query("trip_id") id: String): Deferred<Response<TransactionFieldsResponse>>

    @POST("trips")
    fun createTripAsync(
        @Body request: TripRequest
    ): Deferred<Response<CreateTripResponse>>

    @POST("trips/update")
    fun updateTripAsync(
        @Query("id") id: String,
        @Body request: TripRequest
    ): Deferred<Response<CreateTripResponse>>

    @GET("trips")
    fun getTripsAsync(
        @QueryMap map: Map<String, String>
    ): Deferred<Response<TripsResponse>>

    @GET("trips/show")
    fun getTripAsync(@Query("id") id: String): Deferred<Response<TripResponse>>

    @POST("trips/submit")
    fun submitTripAsync(
        @Body request: SubmitTripRequest
    ): Deferred<Response<SubmitTripResponse>>

    @POST("reports/link_trips")
    fun linkAdvanceToReportAsync(@Body request: LinkTripsToRequest)
            : Deferred<Response<LinkTripsToResponse>>

    @FormUrlEncoded
    @POST("trips/delete")
    fun deleteTripAsync(
        @Field("id") id: String
    ): Deferred<Response<BaseResponse>>

    @POST("trips/comments")
    fun createTripCommentsAsync(@Body request: CreateTripCommentRequest)
            : Deferred<Response<CreateCommentResponse>>

    @GET("trips/comments")
    fun tripCommentsAsync(@Query("trip_id") reportId: String)
            : Deferred<Response<CommentsResponse>>

    @GET("trips/history")
    fun tripHistoryAsync(@Query("trip_id") reportId: String)
            : Deferred<Response<HistoryResponse>>

    @GET("cards")
    fun cardsAsync(): Deferred<Response<CardsResponse>>

    @POST("cards/payment_gateway")
    fun cardTopUpAsync(@Body topUpRequest: TopUpRequest): Deferred<Response<TopUpResponse>>

    @POST("cards/update_mobile_number")
    fun updateCardMobileNumberAsync(@Body request: UpdateCardMobileRequest): Deferred<Response<UpdateCardMobileNumberResponse>>

    @POST("cards/block")
    fun blockCardAsync(@Body request: BlockCardRequest): Deferred<Response<BlockCardResponse>>

    @POST("cards/block_reason")
    fun blockCardReasonsAsync(@Body request: BlockCardReasonsRequest): Deferred<Response<BlockReasonsCardResponse>>

    @POST("cards/set_pin")
    fun setPinAsync(@Body request: SetUpRequest): Deferred<Response<SetPinResponse>>

    @POST("cards/send_otp")
    fun addCardSendOtpAsync(@Body request: AddCardSendOtpRequest): Deferred<Response<AddCardSendOtpResponse>>

    @POST("cards/verify_otp")
    fun addCardVerifyOtpAsync(@Body request: AddCardVerifyOtpRequest): Deferred<Response<AddCardVerifyOtpResponse>>

    @POST("cards/register_card")
    fun addCardRegisterAsync(@Body request: AddCardRegisterRequest): Deferred<Response<AddCardRegisterResponse>>

    @POST("cards/activate")
    fun addCardActivateAsync(@Body request: AddCardActivateRequest): Deferred<Response<AddCardActivateResponse>>

    @GET("transaction_merchants/search")
    fun getMerchantsAsync(@QueryMap searchKey: Map<String, String>): Deferred<Response<MerchantsResponse>>

    @FormUrlEncoded
    @POST("transaction_merchants")
    fun createMerchantAsync(@Field("name") merchantName: String): Deferred<Response<BaseResponse>>

    @GET("notifications")
    fun notificationsAsync(
        @Query("page") page: Int,
        @Query("is_employee") isEmployee: Boolean,
    ): Deferred<Response<NotificationResponse>>

    @FormUrlEncoded
    @PUT("notifications/update_notification")
    fun markNotificationAsSeenAsync(@Field("id") id: String): Deferred<Response<BaseResponse>>


    @POST("card-management-service/card/getVirtualCard")
    fun card(
        @Body cardRequest: CardRequest
    ): Deferred<Response<CardResponse>>
}