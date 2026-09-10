FROM node:24-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
ARG VITE_SERVER_URL=http://localhost:8080
ARG VITE_KAKAO_MAP_SCRIPT_URL
ENV VITE_SERVER_URL=$VITE_SERVER_URL
ENV VITE_KAKAO_MAP_SCRIPT_URL=$VITE_KAKAO_MAP_SCRIPT_URL
RUN npm run build

FROM nginxinc/nginx-unprivileged:1.29-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 8080
