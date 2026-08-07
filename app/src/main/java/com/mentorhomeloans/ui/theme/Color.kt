package com.mentorhomeloans.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Mentor Home Loan - Brand Color Palette (2026)
// Premium Banking UI
// ─────────────────────────────────────────────────────────────────────────────

// ── Primary Brand ────────────────────────────────────────────────────────────
val MentorBlue          = Color(0xFF005B99)   // Primary Brand
val MentorBlueDark      = Color(0xFF00497A)   // Pressed / Dark Theme
val MentorBlueDeep      = Color(0xFF00365C)   // Headers / Toolbar
val MentorBlueMid       = Color(0xFF2B7FC4)   // Secondary Blue
val MentorBlueLight     = Color(0xFFD9ECFA)   // Selected Background
val MentorBluePale      = Color(0xFFEFF7FD)   // Cards / Info Sections
val MentorBlueXPale     = Color(0xFFF8FBFE)   // Screen Background

// ── Accent Colors ────────────────────────────────────────────────────────────
val MentorOrange        = Color(0xFFF59E0B)   // CTA Buttons / Highlights
val MentorGold          = Color(0xFFD4AF37)   // Premium Badge / Rewards
val MentorGreen         = Color(0xFF2E7D32)   // Success
val MentorWarning       = Color(0xFFF59E0B)   // Warning
val MentorError         = Color(0xFFD32F2F)   // Error
val MentorInfo          = Color(0xFF0288D1)   // Information

// ── Neutral Colors ───────────────────────────────────────────────────────────
val NeutralWhite        = Color(0xFFFFFFFF)
val NeutralBlack        = Color(0xFF000000)
val NeutralSurface      = Color(0xFFF7F8FA)   // App Background
val NeutralCard         = Color(0xFFFFFFFF)
val NeutralBorder       = Color(0xFFE5E7EB)
val NeutralDivider      = Color(0xFFF1F5F9)

// ── Text Colors ──────────────────────────────────────────────────────────────
val TextPrimary         = Color(0xFF1F2937)
val TextSecondary       = Color(0xFF6B7280)
val TextHint            = Color(0xFF9CA3AF)
val TextDisabled        = Color(0xFFD1D5DB)
val TextOnPrimary       = Color(0xFFFFFFFF)

// ── Icon Colors ──────────────────────────────────────────────────────────────
val IconPrimary         = MentorBlue
val IconSecondary       = TextSecondary
val IconSuccess         = MentorGreen
val IconWarning         = MentorWarning
val IconError           = MentorError

// ── Button Colors ────────────────────────────────────────────────────────────
val ButtonPrimary       = MentorBlue
val ButtonPrimaryDark   = MentorBlueDark
val ButtonSecondary     = MentorOrange
val ButtonDisabled      = Color(0xFFAFC7DA)

// ── Loan Status Colors ───────────────────────────────────────────────────────
val LoanApproved        = MentorGreen
val LoanPending         = MentorWarning
val LoanRejected        = MentorError
val LoanProcessing      = MentorBlue

// ── EMI Progress ─────────────────────────────────────────────────────────────
val EmiPaid             = MentorGreen
val EmiRemaining        = MentorBlue
val EmiUpcoming         = MentorOrange