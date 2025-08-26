import os
import psycopg2
import json
from dotenv import load_dotenv

# Load environment variables from .env
load_dotenv()

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_NAME = os.getenv("DB_NAME", "catalogue")
DB_USER = os.getenv("DB_USER", "devops")
DB_PASSWORD = os.getenv("DB_PASSWORD", "devops")

conn = psycopg2.connect(
        host=DB_HOST,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD
)
cur = conn.cursor()

# Drop and recreate table with all fields from products.json
cur.execute('DROP TABLE IF EXISTS products;')
cur.execute('''
        CREATE TABLE products (
                id INTEGER PRIMARY KEY,
                name VARCHAR(150) NOT NULL,
                category VARCHAR(100),
                difficulty VARCHAR(50),
                tags JSONB,
                short_description VARCHAR(300),
                description VARCHAR(1000),
                image_url VARCHAR(300),
                created_at DATE
        );
''')

# Load products from products.json
with open('products.json', 'r') as f:
        products = json.load(f)

# Insert products
for product in products:
        cur.execute('''
                INSERT INTO products (
                        id, name, category, difficulty, tags, short_description, description, image_url, created_at
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (id) DO NOTHING;
        ''', (
                product.get('id'),
                product.get('name'),
                product.get('category'),
                product.get('difficulty'),
                json.dumps(product.get('tags')),  # store tags as JSONB
                product.get('short_description'),
                product.get('description'),
                product.get('image_url'),
                product.get('created_at')
        ))

conn.commit()
cur.close()
conn.close()