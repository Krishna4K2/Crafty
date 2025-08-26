FROM golang:1.25-alpine AS builder 

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build -o recommendation .


FROM alpine:latest

WORKDIR /app

COPY --from=builder /app/recommendation .
COPY static ./static
COPY templates ./templates
COPY config.json .

EXPOSE 8080

CMD [ "./recommendation" ]
