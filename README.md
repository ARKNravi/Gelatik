# STUDEAF: Aplikasi Pembelajaran Inklusif Menggunakan Teknologi Speech-to-Sign Language dengan Karakter Visual

**Nama Kelompok**: BC(K)C  
**Ketua Kelompok**: Salsa Zufar Radinka Akmal  
**Anggota Kelompok**:  
1. Ananda Ravi Kuntadi  
2. Naufal Afkaar  
**Dosen Pembimbing**: Dr. Eng. Irawati Nurmala Sari, S.Kom., M.Sc.

---

## **Tech Stack Overview**

### **Frontend**

- **Android Development (Mobile)**:  
  The mobile app is built using **Kotlin**, the modern programming language for Android development. It ensures seamless integration of the UI/UX designs into a fully functioning mobile interface that supports users with hearing impairments.
  
- **Communication with Backend**:  
  The frontend communicates with the backend API via **RESTful services** using **JSON** format for data exchange. 

### **Backend**

- **Backend Framework**:  
  The backend is built using **Fastapi**, a high-level Python framework that promotes rapid development and clean, pragmatic design. It manages the application’s server-side logic, handles user data, and serves the API endpoints that the frontend communicates with.

- **Database**:  
  **PostgreSQL** is used as the relational database management system (RDBMS) to store user data, app content, and interaction logs.

- **API & Data Management**:  
  The backend uses Fastapi's **REST Framework** to build the API endpoints for the frontend. The system manages authentication, data retrieval, and processing of speech-to-text and text-to-sign operations.

- **Machine Learning**:  
  Python is used for the development of the **Speech-to-Text** and **Text-to-Sign** models. These models are built using state-of-the-art **Natural Language Processing (NLP)** techniques and **Deep Learning** to enable real-time speech recognition and sign language generation in Bahasa Isyarat Indonesia (BISINDO).

- **Model Integration**:  
  The trained machine learning models are integrated into the backend using **Flask** for smooth communication between the AI models and the Fastapi backend. 

### **Technologies Used**

- **Kotlin**: Mobile development for Android.
- **Fastapi**: Backend framework and API management.
- **PostgreSQL**: Database management.

### **Development Tools**

- **IDE for Frontend**:  
  - **Android Studio**: The main IDE for developing the Android mobile application.
  
- **IDE for Backend**:  
  - **VS Code**: For working on frontend integration and quick scripts.

- **Version Control**:  
  - **Git**: For version control, ensuring smooth collaboration and tracking of changes across the project.
  - **GitHub**: For managing project code and collaboration.

- **CI/CD**:  
  - **GitHub Actions**: Continuous integration and deployment setup to automatically test and deploy updates to the application.
