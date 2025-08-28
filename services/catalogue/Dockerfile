FROM python:3.11-slim

RUN apt-get update && apt-get install -y postgresql-client curl && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Make entrypoint executable (it's already copied by COPY . . above)
RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
