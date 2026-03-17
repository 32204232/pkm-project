# frontend/Dockerfile

FROM node:20-alpine
WORKDIR /app

# 패키지 정보 복사 및 의존성 설치
COPY package*.json ./
RUN npm install

# 나머지 소스 코드 복사
COPY . .

# Vite 기본 포트 개방
EXPOSE 5173

# 외부(도커 네트워크 밖)에서 접속할 수 있도록 --host 옵션 추가 필수
CMD ["npm", "run", "dev", "--", "--host"]