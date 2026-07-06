# ---------- Etapa 1: compilar Angular ----------
FROM node:22-slim AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN timeout -k 10 120 npm run build -- --configuration production; test -f dist/CareMe/browser/index.html

# ---------- Etapa 2: servir con Nginx ----------
FROM nginx:alpine
COPY --from=build /app/dist/CareMe/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
