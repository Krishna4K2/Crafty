Python virtual environment setup command in local

> python -m venv venv  -->  to setup virtual environment
>
> venv\Scripts\activate --> to activate virtual environment
>
> deactivate        -->     to deactivate virtual environment

How to build Python Flask based Catalogue App

  * Python version: latest
  * Build Tool : pip
  * Build Command : pip install -r requirements.txt
  * Port : 5000
  * Launch Command : gunicorn app:app --bind 0.0.0.0:5000 
    * In windows : python app.py
  