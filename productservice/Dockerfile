FROM node:lts-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install --only=production
COPY . .

EXPOSE 8092
CMD ["node", "index.js"]