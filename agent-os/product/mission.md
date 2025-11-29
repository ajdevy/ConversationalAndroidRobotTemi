# Product Mission

## Pitch
Temi Restaurant Assistant is a proof-of-concept voice-interactive robot that helps restaurant customers get information in loud environments by providing multilingual conversational AI that can understand questions, search knowledge bases, and deliver natural responses even when interrupted.

## Users

### Primary Customers
- **Restaurant Owners**: Need a scalable way to provide instant information to customers without dedicating staff time
- **Restaurant Chains**: Want to test automated customer service technology before wider deployment
- **Restaurant Managers**: Need to manage menu content, robot assignments, and control access for staff members

### User Personas
**Restaurant Patron** (25-65 years old)
- **Role:** Customer visiting a restaurant
- **Context:** Waiting for a table, browsing menu options, or seeking information about the restaurant
- **Pain Points:** Hard to get staff attention in busy/loud restaurants, language barriers, slow service during peak hours
- **Goals:** Quick access to menu information, wait times, restaurant details without waiting for staff

**Restaurant Manager** (30-55 years old)
- **Role:** Manager or owner of a restaurant location
- **Context:** Responsible for operations, staff management, and customer experience
- **Pain Points:** Need to keep menu information updated, manage which staff can access what, ensure robot has current information
- **Goals:** Easy menu updates, control over user permissions, assign robots to specific locations, monitor robot effectiveness

## The Problem

### Information Access in Noisy Environments
Restaurant customers struggle to get timely information in loud, busy environments where staff are overwhelmed. Traditional kiosks require touching screens, can't handle natural conversation, and fail in noisy settings.

**Our Solution:** A voice-first robot powered by LLM technology that can understand natural speech in loud environments, respond intelligently in multiple languages (Spanish, Portuguese, English), and gracefully handle interruptions by stopping and reevaluating its response.

## Differentiators

### Interruption-Aware Conversations
Unlike traditional voice assistants that continue talking when interrupted, our robot immediately stops and reevaluates its response based on the new input. This creates a more natural, human-like interaction.

### Multilingual Noise-Resistant Design
Built specifically for loud restaurant environments with support for Spanish, Portuguese, and English. Uses advanced speech recognition optimized for noisy settings, making it practical for real-world deployment.

### Hybrid LLM Architecture
Combines local on-device LLM (Gemma 2/3) for offline capability with cloud-based models (GPT-4o) for highest quality responses when online. Ensures consistent service regardless of connectivity.

## Key Features

### Core Features
- **Voice Conversation in Loud Environments:** Customers can naturally speak to the robot and be heard even in noisy restaurant settings
- **Multilingual Support:** Responds intelligently in Spanish, Portuguese, and English with high language quality
- **Menu-Based Question Answering:** Customers ask about menu items and robot provides accurate answers based on the restaurant's specific menu
- **Restaurant-Specific Knowledge:** Each robot is assigned to one restaurant and has access only to that restaurant's menu and information
- **Visual Status Feedback:** Clear visual indicator shows robot state - red pulsating circle when thinking, green pulsating circle when speaking, grey static circle when idle and ready for interaction

### Intelligent Interaction Features
- **LLM-Powered Conversations:** Uses AI to understand context and provide natural, helpful responses to customer questions
- **Interruption Handling:** Immediately stops speaking when interrupted and reevaluates to provide a better, more relevant response
- **Menu Search Integration:** LLM automatically searches restaurant menu to answer questions about dishes, ingredients, prices, and availability

### Management Features
- **Multi-Restaurant Support:** Platform manages multiple restaurants, each with their own menus, robots, and settings
- **Role-Based Permissions:** Admin users can manage which users have access to which restaurants
- **Menu Management:** Restaurant managers can create, update, and organize menu items with descriptions, prices, and allergen information
- **Robot Assignment:** Each robot is configured to serve a specific restaurant location

### Advanced Features
- **Hybrid Local/Cloud AI:** Falls back to local LLM when offline, uses cloud API for best quality when connected
- **Comprehensive Test Coverage:** Backend code maintained with maximum unit test coverage for reliability
- **Business Intelligence:** Tracks common questions and interaction patterns to help restaurants improve service
- **Test Data Included:** Pre-loaded test data for restaurants, menus, users, and permissions to facilitate development and testing
