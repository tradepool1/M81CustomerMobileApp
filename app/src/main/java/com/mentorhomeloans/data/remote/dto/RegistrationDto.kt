package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO representing a State from GetState API.
 */
data class StateDto(
    @SerializedName("StateId")    val stateId: Int,
    @SerializedName("State_Name") val stateName: String,
    @SerializedName("Code")       val code: String,
    @SerializedName("StateCode")  val stateCode: String,
    @SerializedName("GSTCode")    val gstCode: String
)

/**
 * DTO representing a Branch from GetBranchesByStateId API.
 */
data class BranchDto(
    @SerializedName("BranchId")        val branchId: Int,
    @SerializedName("Branch_Name")     val branchName: String,
    @SerializedName("Branch_Address")  val branchAddress: String,
    @SerializedName("Branch_Code")     val branchCode: String,
    @SerializedName("Branch_Type")     val branchType: String,
    @SerializedName("Branch_StateId")  val branchStateId: Int,
    @SerializedName("Branch_ZoneId")   val branchZoneId: Int
)

/**
 * Request body for CustomerAppRegistration endpoint.
 */
data class RegistrationRequestDto(
    @SerializedName("name")          val name: String,
    @SerializedName("email")         val email: String,
    @SerializedName("mobileNo")      val mobileNo: String,
    @SerializedName("stateId")       val stateId: Int,
    @SerializedName("branchId")      val branchId: Int,
    @SerializedName("loanAcNo")      val loanAcNo: String,
    @SerializedName("panNo")         val panNo: String,
    @SerializedName("allowAppLogin") val allowAppLogin: Boolean = true
)

/**
 * Response from CustomerAppRegistration endpoint.
 */
data class RegistrationResponseDto(
    @SerializedName("Status")  val status: Boolean,
    @SerializedName("Message") val message: String
)
