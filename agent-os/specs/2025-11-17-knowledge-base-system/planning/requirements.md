# Spec Requirements: Knowledge Base System

## Initial Description
Implement general knowledge base storage for restaurant information (policies, hours, amenities, etc.) in PostgreSQL. Create API endpoints for storing and querying non-menu information.

## Requirements Discussion

### First Round Questions

**Q1: Storage Schema & Data Model - How should the knowledge base data be structured?**
**Answer:** Should be flexible (JSON/JSONB approach)

**Q2: Restaurant Isolation - Should each restaurant have its own isolated knowledge base?**
**Answer:** Yes - each knowledge base isolated per restaurant

**Q3: Query & Search Mechanism - How will the LLM query and retrieve relevant knowledge?**
**Answer:** The LLM will receive all the knowledge base entries

**Q4: CRUD & Management - Should knowledge entries have categories, tags, or be free-form?**
**Answer:** Free-form (no predefined categories/tags)

**Q5: Multi-Tenancy & Permissions - Does the existing RBAC system apply to knowledge base management?**
**Answer:** Yes - existing RBAC applies

**Q6: Data Types & Format - Should the system support structured data (lists, booleans, numbers) or only free-form text?**
**Answer:** Yes, support structured data (lists, booleans, numbers) and free-form text

**Q7: Integration Scope - Which parts of the system need access to the knowledge base?**
**Answer:** The Android app should have access to this. If local model is used, the knowledge base may be downloaded as a JSON/text file. If it's a remote model, then the data will be retrieved from our backend and fed into the LLM API.

**Q8: Out of Scope - Should multi-lingual support be considered or deferred?**
**Answer:** Multi-lingual support SHOULD be present (not out of scope)

### Existing Code to Reference
No similar existing features identified for reference.

### Follow-up Questions
No follow-up questions needed.

## Visual Assets
No visual assets found in the planning directory.

## Requirements Summary

### Functional Requirements
- Store restaurant-specific knowledge base entries in PostgreSQL using flexible JSON/JSONB schema
- Provide full CRUD operations for knowledge base entries (create, read, update, delete)
- Support both structured data types (lists, booleans, numbers) and free-form text content
- Implement restaurant-level isolation - each restaurant has its own knowledge base
- Apply existing RBAC (Role-Based Access Control) to knowledge base management operations
- Provide API endpoints that return all knowledge base entries for a restaurant (to be fed to LLM)
- Enable Android app access to knowledge base data
- Support two integration modes:
  - Local model: Knowledge base exportable as JSON/text file for download
  - Remote model: Backend API provides knowledge base data to be fed into LLM API
- Include multi-lingual support for knowledge base content
- Free-form entry structure without predefined categories or tags

### Reusability Opportunities
None identified.

### Scope Boundaries
**In Scope:**
- PostgreSQL database schema for knowledge base storage using JSONB
- REST API endpoints for CRUD operations on knowledge base entries
- Restaurant-level data isolation
- RBAC integration for access control
- API endpoint to retrieve all knowledge base entries for a restaurant
- Export functionality for JSON/text file download (local model support)
- Multi-lingual content support
- Android app integration capabilities

**Out of Scope:**
- LLM implementation itself (knowledge base only provides data to existing LLM)
- Advanced search/filtering mechanisms (LLM receives all entries)
- Predefined categorization or tagging system
- Knowledge base versioning or history tracking
- Automatic content translation (multi-lingual support means storing content in multiple languages, not auto-translation)

### Technical Considerations
- Database: PostgreSQL with JSONB column type for flexible schema
- Integration Points:
  - Existing RBAC system for permissions
  - Android app for client-side access
  - LLM service (both local and remote models)
- API Design:
  - RESTful endpoints for CRUD operations
  - Bulk retrieval endpoint for feeding to LLM
  - Export endpoint for JSON/text file download
- Multi-tenancy: Restaurant-level isolation must be enforced at database and API layers
- Performance: Consider indexing strategies for JSONB queries and bulk retrieval optimization
- Data Format: JSONB allows flexible structure while supporting typed data (strings, numbers, booleans, arrays, objects)
