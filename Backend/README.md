# FastAPI Project with SQLAlchemy & Alembic

This is a FastAPI project that uses SQLAlchemy as ORM and Alembic for database migrations. The project includes models, repositories, services, and usecases. It is designed to demonstrate the use of FastAPI in combination with SQLAlchemy and Alembic.

## Features

-   **FastAPI** for building the API.
-   **SQLAlchemy** ORM for interacting with the database.
-   **Alembic** for database migrations.
-   Folder structure using **snake_case** naming convention.
-   Docker-friendly configuration (optional).

## Requirements

-   Python 3.x
-   FastAPI
-   SQLAlchemy
-   Alembic
-   SQLite (or other supported databases)

## Installation

1. Clone this repository:

    ```bash
    git clone https://github.com/yourusername/yourrepo.git
    cd yourrepo
    ```

2. Create and activate a virtual environment:

    ```bash
    python -m venv venv
    source venv/bin/activate  # On Windows, use `venv\Scripts\activate`
    ```

3. Install the dependencies:

    ```bash
    pip install -r requirements.txt
    ```

## Database Setup & Migrations

1. To run the migrations, first configure **Alembic**. Ensure that your `alembic.ini` is properly set up for the database.

2. Create an initial migration:

    ```bash
    alembic revision --autogenerate -m "Initial migration"
    ```

3. Apply the migrations:

    ```bash
    alembic upgrade head
    ```

4. You can now start your FastAPI app!

## Running the Application

To run the FastAPI application, use the following command:

```bash
uvicorn main:app --reload
```
