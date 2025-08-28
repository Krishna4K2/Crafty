from flask import Flask, jsonify, render_template
from datetime import datetime
import socket
import os
import json
import psycopg2
from dotenv import load_dotenv

app = Flask(__name__,
            static_folder='static',
            static_url_path='/static')

# Load environment variables from .env file
load_dotenv()

# Load product data from JSON file (still local for non-db mode)
with open('products.json', 'r') as f:
    products = json.load(f)

def get_db_connection():
    conn = psycopg2.connect(
        host=os.getenv("DB_HOST", "catalogue-db"),
        database=os.getenv("DB_NAME", "catalogue"),
        user=os.getenv("DB_USER", "crafty"),
        password=os.getenv("DB_PASSWORD", "crafty")
    )
    return conn

@app.route('/')
def home():
    system_info = get_system_info()
    app_version = os.getenv("APP_VERSION", "N/A")  # Default to "N/A" if not set
    return render_template(
        'index.html',
        current_year=datetime.now().year,
        system_info=system_info,
        version=app_version
    )

@app.route('/api/products', methods=['GET'])
def get_products():
    data_source = os.getenv("DATA_SOURCE", "json")  # Default to json
    if data_source == "db":
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute('SELECT id, name, category, difficulty, tags, short_description, description, image_url, created_at FROM products;')
        db_products = cur.fetchall()
        products_dict = [
            {
                'id': row[0],
                'name': row[1],
                'category': row[2],
                'difficulty': row[3],
                'tags': row[4],
                'short_description': row[5],
                'description': row[6],
                'image_url': row[7],
                'created_at': str(row[8]) if row[8] else None
            }
            for row in db_products
        ]
        cur.close()
        conn.close()
        return jsonify(products_dict), 200
    else:
        return jsonify(products), 200

@app.route('/api/products/<int:product_id>', methods=['GET'])
def get_product(product_id):
    data_source = os.getenv("DATA_SOURCE", "json")  # Default to json
    if data_source == "db":
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute('SELECT id, name, category, difficulty, tags, short_description, description, image_url, created_at FROM products WHERE id = %s;', (product_id,))
        db_product = cur.fetchone()
        cur.close()
        conn.close()
        if db_product:
            product = {
                'id': db_product[0],
                'name': db_product[1],
                'category': db_product[2],
                'difficulty': db_product[3],
                'tags': db_product[4],
                'short_description': db_product[5],
                'description': db_product[6],
                'image_url': db_product[7],
                'created_at': str(db_product[8]) if db_product[8] else None
            }
            return jsonify(product)
        else:
            return jsonify({'message': 'Product not found'}), 404
    else:
        product = next((product for product in products if product['id'] == product_id), None)
        if product is not None:
            return jsonify(product)
        else:
            return jsonify({'message': 'Product not found'}), 404

def get_system_info():
    hostname = socket.gethostname()
    ip_address = socket.gethostbyname(hostname)

    # Additional logic for container and Kubernetes check
    is_container = os.path.exists('/.dockerenv')
    is_kubernetes = os.path.exists('/var/run/secrets/kubernetes.io/serviceaccount')
    
    return {
        "hostname": hostname,
        "ip_address": ip_address,
        "is_container": is_container,
        "is_kubernetes": is_kubernetes
    }

if __name__ == "__main__":
    app.run(debug=True)
