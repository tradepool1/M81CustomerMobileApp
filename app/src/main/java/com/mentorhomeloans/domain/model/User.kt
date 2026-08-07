package com.mentorhomeloans.domain.model

/**
 * Represents the authenticated customer's profile in the domain layer.
 *
 * This is a pure Kotlin data class with no Android dependencies,
 * ensuring the domain layer remains framework-agnostic and fully testable.
 *
 * @property id             Unique customer identifier (UUID or numeric ID from backend).
 * @property customerId     Human-readable customer ID (e.g. "CUST00123").
 * @property fullName       Customer's complete legal name.
 * @property mobileNumber   Registered mobile number (used for OTP login).
 * @property email          Email address (may be optional/nullable).
 * @property dateOfBirth    Date of birth in ISO-8601 format (yyyy-MM-dd).
 * @property panNumber      PAN card number (masked for display).
 * @property address        Primary residential address.
 * @property coApplicant    Optional co-applicant details.
 * @property kycStatus      KYC verification status.
 * @property profileImageUrl URL to the customer's profile photo.
 */
data class User(
    val id: String,
    val customerId: String,
    val fullName: String,
    val mobileNumber: String,
    val email: String?,
    val dateOfBirth: String,
    val panNumber: String,
    val address: Address,
    val coApplicant: CoApplicant?,
    val kycStatus: KycStatus,
    val profileImageUrl: String?
)

/**
 * Represents a physical address.
 *
 * @property line1      House/flat number and street.
 * @property line2      Area/locality (optional).
 * @property city       City name.
 * @property state      State name.
 * @property pinCode    6-digit postal code.
 * @property country    Country (defaults to India).
 */
data class Address(
    val line1: String,
    val line2: String?,
    val city: String,
    val state: String,
    val pinCode: String,
    val country: String = "India"
)

/**
 * Represents a co-applicant associated with the loan.
 *
 * @property name         Co-applicant's full name.
 * @property relationship Relationship to the primary applicant.
 * @property mobileNumber Co-applicant's contact number.
 * @property panNumber    Co-applicant's PAN (masked).
 */
data class CoApplicant(
    val name: String,
    val relationship: String,
    val mobileNumber: String,
    val panNumber: String
)

/**
 * KYC (Know Your Customer) verification status.
 */
enum class KycStatus {
    /** KYC documents verified and approved. */
    VERIFIED,
    /** KYC documents submitted and awaiting review. */
    PENDING,
    /** KYC documents rejected; re-submission required. */
    REJECTED,
    /** KYC documents not yet submitted. */
    NOT_SUBMITTED
}
