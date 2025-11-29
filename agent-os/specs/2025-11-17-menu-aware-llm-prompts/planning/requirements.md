# Spec Requirements: Menu-Aware LLM Prompts

## Initial Description
Enhance LLM prompts to automatically search and reference restaurant menu when answering customer questions. Train prompts to handle menu-specific queries (ingredients, prices, recommendations, allergens).

## Requirements Discussion

### First Round Questions

**Q1: Menu Search Scope and Retrieval**
How should the system access menu data when processing customer queries?
- Option A: Inject the full menu into the prompt context
- Option B: Use RAG/vector search to retrieve relevant menu items
- Option C: Hybrid approach (full menu for small menus, RAG for large ones)

**Answer:** Inject the full menu into the prompt context

**Q2: Menu Query Categories**
What types of menu-related questions should the system handle? (Select all that apply)
- Ingredients/allergens
- Prices
- Dish descriptions
- Recommendations (e.g., "What's popular?")
- Availability (e.g., "Do you have vegetarian options?")

**Answer:** No additional categories beyond: ingredients/allergens, prices, dish descriptions, recommendations, availability

**Q3: LLM Integration Point**
Where will this menu-aware prompting be used?
- Option A: Local LLM only (Gemma 2/3)
- Option B: Cloud API only (GPT-4o)
- Option C: Both local and cloud

**Answer:** Both local LLM (Gemma 2/3) and cloud API (GPT-4o)

**Q4: Multi-Restaurant Context Handling**
Should the system handle queries about multiple restaurants, or only the restaurant the customer is interacting with?
- Option A: Single restaurant context only
- Option B: Multi-restaurant comparison queries
- Option C: Configurable per deployment

**Answer:** Correct - only access to assigned restaurant's menu

**Q5: Multilingual Menu Support**
Do menus need to support multiple languages?
- Option A: English only for now
- Option B: Specific languages (which ones?)
- Option C: Multi-language with translation capability

**Answer:** Basically all English, but some info may be multilingual

**Q6: Prompt Engineering Strategy**
How should menu-aware prompts be structured?
- Option A: Single universal prompt template for all restaurants
- Option B: Per-restaurant customizable prompts
- Option C: Per-restaurant with a common configurable template

**Answer:** Per restaurant with a common configurable template

**Q7: Response Format and Constraints**
Should responses have specific formatting requirements?
- Option A: Natural conversational responses
- Option B: Structured format (JSON, bullet points, etc.)
- Option C: Voice-optimized (concise, no complex formatting)

**Answer:** Yes, concise and voice-friendly. Prioritize brevity over comprehensiveness

**Q8: Out-of-Scope Scenarios**
How should the system handle questions it can't answer from the menu?
- Option A: Politely decline and redirect
- Option B: Use general knowledge fallback
- Option C: Escalate to knowledge base or human agent

**Answer:** Yes, use the knowledge base system for non-menu questions

### Existing Code to Reference
No similar existing features identified for reference.

### Follow-up Questions
No follow-up questions needed.

## Visual Assets
No visual files found in the planning/visuals directory.

## Requirements Summary

### Functional Requirements
- **Full Menu Context Injection**: System must inject the complete restaurant menu into the LLM prompt context for processing customer queries
- **Comprehensive Menu Query Handling**: Support queries across all standard categories:
  - Ingredients and allergen information
  - Pricing details
  - Dish descriptions and details
  - Recommendations and popular items
  - Availability and dietary options (vegetarian, vegan, gluten-free, etc.)
- **Dual LLM Support**: Implementation must work with both:
  - Local LLM (Gemma 2/3)
  - Cloud API (GPT-4o)
- **Single Restaurant Scope**: Each interaction context limited to one restaurant's menu only
- **Multilingual Capability**: Primary language is English with support for multilingual menu information where present
- **Configurable Per-Restaurant Prompts**: Common template structure with restaurant-specific customization capabilities
- **Voice-Optimized Responses**: Generate concise, brief responses suitable for voice interaction, prioritizing brevity over comprehensive detail
- **Knowledge Base Integration**: Redirect non-menu questions to the existing knowledge base system for handling

### Reusability Opportunities
- **Common Prompt Template**: Create a reusable base template that can be specialized per restaurant while maintaining consistent structure
- **Menu Injection Utility**: Build a reusable module for formatting and injecting menu data into prompt context
- **Query Classification**: Develop a reusable query categorization system to identify menu vs. non-menu questions
- **LLM Abstraction Layer**: Create unified interface that works across both local (Gemma) and cloud (GPT-4o) LLMs
- **Response Formatting**: Build reusable voice-optimization utilities for consistent concise output formatting

### Scope Boundaries

**In Scope:**
- Menu data injection into LLM prompt context
- Prompt templates for menu-aware queries (ingredients, prices, descriptions, recommendations, availability)
- Support for both Gemma 2/3 (local) and GPT-4o (cloud) LLMs
- Per-restaurant prompt customization with common base template
- Voice-optimized response formatting (concise, brief)
- Integration with knowledge base system for out-of-scope queries
- Single restaurant context per interaction
- Basic multilingual menu support (primarily English)

**Out of Scope:**
- RAG/vector search implementation for menu retrieval
- Multi-restaurant comparison queries
- Full translation services for multilingual menus
- Custom response formats (JSON, complex structured data)
- Menu data management and CRUD operations
- Menu item image handling or visual content
- Real-time menu availability updates
- Ordering or transaction functionality
- Customer preference learning or personalization
- Analytics or query performance tracking

### Technical Considerations
- **Integration Points**:
  - Existing knowledge base system for non-menu queries
  - Local LLM infrastructure (Gemma 2/3)
  - Cloud API infrastructure (GPT-4o)
  - Restaurant menu data storage/access layer

- **Technical Constraints**:
  - Context window limitations for full menu injection (must monitor token usage)
  - Latency considerations for both local and cloud LLM calls
  - Consistent behavior across different LLM providers (local vs. cloud)
  - Response brevity requirements for voice interaction

- **Technology Preferences**:
  - Local LLM: Gemma 2/3
  - Cloud LLM: GPT-4o
  - Template-based prompt engineering approach
  - Integration with existing knowledge base routing

- **Performance Considerations**:
  - Menu size impact on prompt context (token limits)
  - Response time optimization for voice interactions
  - Fallback strategy if LLM call fails
  - Caching strategies for frequently accessed menu data

- **Quality Considerations**:
  - Accuracy of menu information in responses
  - Consistent handling of allergen and dietary restriction queries
  - Clear boundaries between menu and non-menu question handling
  - Voice-friendly response format validation
