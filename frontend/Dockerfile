# ===== Build =====
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --silent
COPY . .
RUN npm run build -- --configuration production

# ===== Nginx =====
FROM nginx:alpine
RUN apk add --no-cache gettext
RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf.template /etc/nginx/templates/default.conf.template
COPY nginx.conf          /etc/nginx/conf.d/default.conf.static
COPY --from=build /app/dist/checklist/browser /usr/share/nginx/html

EXPOSE 80

# Se BACKEND_URL estiver definida (Railway): substitui via envsubst
# Senão (Docker Compose local): usa nginx.conf estático com backend:8080
CMD ["/bin/sh", "-c", "\
  if [ -n \"$BACKEND_URL\" ]; then \
    envsubst '${BACKEND_URL}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf; \
  else \
    cp /etc/nginx/conf.d/default.conf.static /etc/nginx/conf.d/default.conf; \
  fi && nginx -g 'daemon off;'"]
