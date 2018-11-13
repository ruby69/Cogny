FROM nginx:latest

COPY dist /usr/share/nginx/html
COPY cogny-staff.conf /etc/nginx/conf.d/default.conf

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
