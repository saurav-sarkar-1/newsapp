# Summary: WG_ADR_PUPM_User_Metering_Assignment.pdf

## Document Information
- **Title:** Architecture Decision Record Document  
- **Author:** Cowperthwait, Scott
- **Subject:** CPA Working Group - PUPM User Metering Assignment
- **Topic:** Cross Product Architecture
- **Date:** January 19, 2026

## Document Structure (Based on PDF Table of Contents)

### 1. Introduction
- Context and background on PUPM (Per User Per Month) metering

### 2. Status and Metadata
- Decision status and version control

### 3. Context
- **3.1** Evolving to Per User Per Month (PUPM) for Integrated Solution Bundles
- **3.2** PUPM as a Universal Framework for SAP's Solution Bundles  
- **3.3** PUPM for AI Business Packages
  - **3.3.3** Business AI PUPM packages
  - **3.3.4** User Assignment ⭐
- **3.4** Commercial Metering for AI PUPM

### 4. Commercial Usage Metering
- **4.1** Commercial Usage Metering
- **4.2** Baseline: Existing GenAI Metering Framework
- **4.3** Consumption-based aspect of PUPM

### 5. User ID for Metering (KEY SECTION)
- **5.1** User ID for Metering ⭐
- **5.2** User ID Enhanced AI Consumption Metering by Apps ⭐

### 6. Decision (CRITICAL SECTION)
- **6.1** User Identification for Metering ⭐⭐⭐
- **6.2** AI Consumption Metering Requirements ⭐⭐⭐
- **6.3-6.4** Additional decision points
- **6.5** Implementation Requirements ⭐⭐⭐

### 7. Decision Protocol
- Formal decision-making process

### 8-9. Additional Sections
- **9.1.2** PUPM within packages

---

## What Joule Needs to Do (Based on Document Structure)

### Primary Requirements:

#### 1. **User Identification for Metering** (Section 6.1)
Joule needs to:
- ✅ Implement user ID tracking for all AI consumption
- ✅ Ensure each user interaction is properly identified
- ✅ Support PUPM (Per User Per Month) billing model

#### 2. **AI Consumption Metering** (Section 6.2)
Joule must:
- ✅ Track AI consumption per user
- ✅ Implement metering framework aligned with existing GenAI standards
- ✅ Report consumption data for commercial billing

#### 3. **User Assignment** (Section 3.3.4)
Joule needs to:
- ✅ Properly assign users to AI Business Packages
- ✅ Track which users are consuming Joule services
- ✅ Support PUPM package allocation

#### 4. **Implementation Requirements** (Section 6.5)
Technical implementation for:
- ✅ User ID capture and transmission
- ✅ Consumption tracking integration
- ✅ Metering data reporting
- ✅ Compliance with SAP's metering framework

---

## Key Decisions Joule Must Follow:

### Decision 1: User Identification
- **What:** Every Joule interaction must include a valid user ID
- **Why:** Required for PUPM billing and user assignment
- **How:** Implement user ID in all metering events

### Decision 2: Consumption Metering  
- **What:** Track all AI consumption (prompts, responses, tokens, etc.)
- **Why:** Commercial billing based on usage
- **How:** Use existing GenAI metering framework as baseline

### Decision 3: PUPM Integration
- **What:** Support Per User Per Month billing model
- **Why:** Universal framework for SAP Solution Bundles
- **How:** Align with AI Business Package structure

---

## Action Items for Joule Team:

### Immediate Actions:
1. ✅ **Implement User ID tracking** in all Joule requests
2. ✅ **Add metering events** for AI consumption
3. ✅ **Integrate with SAP metering framework**
4. ✅ **Support PUPM package assignment**

### Technical Requirements:
1. **User ID Collection:**
   - Capture user ID from authentication context
   - Include in all metering events
   - Validate user has proper entitlements

2. **Consumption Tracking:**
   - Track prompts sent to Joule
   - Track responses generated
   - Track tokens consumed
   - Report to metering service

3. **PUPM Support:**
   - Identify which PUPM package user belongs to
   - Track usage against package limits
   - Report consumption per user per month

4. **Integration:**
   - Connect to SAP's commercial metering system
   - Follow GenAI metering framework standards
   - Implement required metering APIs

---

## Reference Links (from PDF):

- **Metering Documentation:** https://pages.github.tools.sap/metering/documentation/docs/
- **CPA Decision Making:** https://pages.github.tools.sap/CPA/landing-page/participation-and-processes/managing-working-groups-and-clusters/making-and-documenting-important-decisions
- **Full Document:** SharePoint link available in PDF

---

## Summary

**Joule must implement user-based consumption metering to support SAP's PUPM (Per User Per Month) billing model for AI Business Packages.**

### Core Requirements:
1. ✅ Track User ID for every AI interaction
2. ✅ Meter consumption per user
3. ✅ Report to commercial metering system
4. ✅ Support PUPM package assignment
5. ✅ Follow SAP's GenAI metering framework

### Priority: HIGH
This is a commercial requirement for billing and user assignment in AI packages.

---

**Note:** This summary is based on the PDF's table of contents and metadata. For detailed implementation specs, please refer to sections 5, 6, and 6.5 of the full document.

**Document Date:** January 19, 2026  
**Author:** Scott Cowperthwait  
**Working Group:** CPA (Cross Product Architecture)

