FROM golang:1.25-alpine AS builder 

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build -o recommendation .

FROM alpine:latest

# Install curl for health checks and entrypoint script
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /app/recommendation .
COPY static ./static
COPY templates ./templates
COPY config.json .

# Make entrypoint executable
RUN chmod +x entrypoint.sh

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/recommendation-status || exit 1

ENTRYPOINT ["./entrypoint.sh"]
