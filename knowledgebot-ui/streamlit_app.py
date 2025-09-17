import streamlit as st
import requests

# Page settings
st.set_page_config(page_title="HR Policy Assistant", page_icon="🤖", layout="wide")

# Sidebar for quick categories
st.sidebar.title("📂 Policy Categories")
st.sidebar.markdown(
    """
    Choose a category to explore:
    - 🏖️ Leave Policies  
    - 🏠 Remote Work  
    - 🕒 Working Hours  
    - 💰 Salary & Bonuses  
    - 🏥 Health & Benefits  
    - 📊 Performance & Promotions  
    - 🔐 Security & Conduct  
    """
)

# Main title and description
st.title("🤖 HR Policy Assistant")
st.markdown(
    """
    Welcome to the **HR KnowledgeBot**!  
    I can answer your questions about company policies such as **leave, remote work, health insurance, reimbursements, and more**.  

    💡 *Try asking:*  
    - "Tell me about leave policy?"  
    - "What is the remote work policy?"  
    - "Am I entitled to health insurance?"  
    - "Tell me about working hours?"  
    """
)

# Input field for user questions
user_question = st.text_input(
    "Ask your question:",
    placeholder="e.g., What is the maternity leave policy?"
)

# Submit button
if st.button("Submit"):
    if user_question.strip():
        try:
            response = requests.post(
                "http://localhost:8000/ask",
                json={"question": user_question, "userId": "demo"}
            )
            if response.status_code == 200:
                answer = response.json().get("answer", "No answer found.")
                st.success("### 🗨️ Bot’s Answer")
                st.markdown(f"**{answer}**")
            else:
                st.error("⚠️ Something went wrong. Please try again.")
        except Exception as e:
            st.error(f"🚨 Error: {e}")
    else:
        st.warning("⚠️ Please enter a question before submitting.")
